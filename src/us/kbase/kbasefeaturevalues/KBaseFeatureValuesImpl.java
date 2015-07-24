package us.kbase.kbasefeaturevalues;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import us.kbase.auth.AuthToken;
import us.kbase.clusterservice.ClusterResults;
import us.kbase.clusterservice.ClusterServiceLocalClient;
import us.kbase.clusterservice.ClusterServiceRLocalClient;
import us.kbase.common.service.UObject;
import us.kbase.userandjobstate.UserAndJobStateClient;
import us.kbase.workspace.ObjectData;
import us.kbase.workspace.ObjectIdentity;
import us.kbase.workspace.ObjectSaveData;
import us.kbase.workspace.ProvenanceAction;
import us.kbase.workspace.SaveObjectsParams;
import us.kbase.workspace.SubObjectIdentity;
import us.kbase.workspace.WorkspaceClient;

public class KBaseFeatureValuesImpl {
    private String jobId;
    private String token;
    private Map<String, String> config;
    private File workDir;
    private String wsUrl = null;
    private WorkspaceClient wsClient;
    private UserAndJobStateClient ujsClient;
    
    public KBaseFeatureValuesImpl(String jobId, String token, Map<String, String> config,
            File workDir) throws Exception {
        this(jobId, token, config, workDir, null, null);
    }
    
    public KBaseFeatureValuesImpl(String jobId, String token, Map<String, String> config,
            File workDir, WorkspaceClient wsClient, UserAndJobStateClient ujsClient) throws Exception {
        this.jobId = jobId;
        this.token = token;
        this.config = config;
        this.workDir = workDir == null ? new File(".").getCanonicalFile() : workDir;
        this.wsClient = wsClient;
        this.ujsClient = ujsClient;
    }
    
    public String getWsUrl() {
        if (wsUrl == null)
            wsUrl = config.get(KBaseFeatureValuesServer.CONFIG_PARAM_WS_URL);
        return wsUrl;
    }
    
    public WorkspaceClient getWsClient() throws Exception {
        if (wsClient != null)
            return wsClient;
        wsClient = new WorkspaceClient(new URL(getWsUrl()), new AuthToken(token));
        wsClient.setAuthAllowedForHttp(true);
        return wsClient;
    }
    
    public UserAndJobStateClient getUjsClient() throws Exception {
        if (ujsClient != null)
            return ujsClient;
        String ujsUrl = config.get(KBaseFeatureValuesServer.CONFIG_PARAM_UJS_URL);
        ujsClient = new UserAndJobStateClient(new URL(ujsUrl), new AuthToken(token));
        ujsClient.setIsInsecureHttpConnectionAllowed(true);
        return ujsClient;
    }

    public ClusterServiceLocalClient getMathClient() throws Exception {
        ClusterServiceRLocalClient mathClient = new ClusterServiceRLocalClient(workDir);
        String binPath = config.get(KBaseFeatureValuesServer.CONFIG_PARAM_CLIENT_BIN_DIR);
        if (binPath != null)
            mathClient.setBinDir(new File(binPath));
        return mathClient;
    }
    
    public String getJobId() {
        return jobId;
    }
    
    public String estimateK(EstimateKParams params) throws Exception {
        ObjectData objData = getWsClient().getObjects(Arrays.asList(
                new ObjectIdentity().withRef(params.getInputMatrix()))).get(0);
        BioMatrix matrix = objData.getData().asClassInstance(BioMatrix.class);
        ClusterServiceLocalClient mathClient = getMathClient();
        EstimateKResult toSave = mathClient.estimateK(matrix.getData(), params.getMinK(), 
                params.getMaxK(), params.getMaxIter(), params.getRandomSeed(),
                params.getNeighbSize());
        List<ProvenanceAction> provenance = Arrays.asList(
                new ProvenanceAction().withService(KBaseFeatureValuesServer.SERVICE_NAME)
                .withServiceVer(KBaseFeatureValuesServer.SERVICE_VERSION)
                .withDescription("K estimation for K-Means clustering method")
                .withInputWsObjects(Arrays.asList(params.getInputMatrix()))
                .withMethod("estimate_k")
                .withMethodParams(Arrays.asList(new UObject(params))));
        getWsClient().saveObjects(new SaveObjectsParams().withWorkspace(params.getOutWorkspace())
                .withObjects(Arrays.asList(new ObjectSaveData()
                .withType("KBaseFeatureValues.EstimateKResult").withName(params.getOutEstimateResult())
                .withData(new UObject(toSave)).withProvenance(provenance))));
        return params.getOutWorkspace() + "/" + params.getOutEstimateResult();
    }

    public String clusterKMeans(ClusterKMeansParams params) throws Exception {
        ObjectData objData = getWsClient().getObjects(Arrays.asList(
                new ObjectIdentity().withRef(params.getInputData()))).get(0);
        BioMatrix matrix = objData.getData().asClassInstance(BioMatrix.class);
        ClusterServiceLocalClient mathClient = getMathClient();
        ClusterResults res = mathClient.clusterKMeans(matrix.getData(), params.getK(), 
                params.getNStart(), params.getMaxIter(), params.getRandomSeed(),
                params.getAlgorithm());
        ClusterSet toSave = new ClusterSet().withOriginalData(params.getInputData());
        toSave.withFeatureClusters(clustersFromLabels(matrix, res));
        List<ProvenanceAction> provenance = Arrays.asList(
                new ProvenanceAction().withService(KBaseFeatureValuesServer.SERVICE_NAME)
                .withServiceVer(KBaseFeatureValuesServer.SERVICE_VERSION)
                .withDescription("K-Means clustering method")
                .withInputWsObjects(Arrays.asList(params.getInputData()))
                .withMethod("cluster_k_means")
                .withMethodParams(Arrays.asList(new UObject(params))));
        getWsClient().saveObjects(new SaveObjectsParams().withWorkspace(params.getOutWorkspace())
                .withObjects(Arrays.asList(new ObjectSaveData()
                .withType("KBaseFeatureValues.ClusterSet").withName(params.getOutClustersetId())
                .withData(new UObject(toSave)).withProvenance(provenance))));
        return params.getOutWorkspace() + "/" + params.getOutClustersetId();
    }

    public List<LabeledCluster> clustersFromLabels(BioMatrix matrix, ClusterResults res) {
        Map<Long, LabeledCluster> labelToCluster = new LinkedHashMap<Long, LabeledCluster>();
        List<LabeledCluster> featureClusters = new ArrayList<LabeledCluster>();
        int minClusterLabel = -1;
        for (int featurePos = 0; featurePos < res.getClusterLabels().size(); featurePos++) {
            long clusterLabel = res.getClusterLabels().get(featurePos);
            if (minClusterLabel < 0 || minClusterLabel > clusterLabel)
                minClusterLabel = (int)clusterLabel;
            LabeledCluster cluster = labelToCluster.get(clusterLabel);
            if (cluster == null) {
                cluster = new LabeledCluster().withIdToPos(new LinkedHashMap<String, Long>());
                labelToCluster.put(clusterLabel, cluster);
                featureClusters.add(cluster);
            }
            String featureLabel = matrix.getData().getRowIds().get(featurePos);
            cluster.getIdToPos().put(featureLabel, (long)featurePos);
        }
        if (res.getMeancor() != null || res.getMsecs() != null) {
            for (long clusterLabel : labelToCluster.keySet()) {
                LabeledCluster cluster = labelToCluster.get(clusterLabel);
                int clusterPos = (int)clusterLabel - minClusterLabel;
                if (res.getMeancor() != null)
                    cluster.withMeancor(res.getMeancor().get(clusterPos));
                if (res.getMsecs() != null)
                    cluster.withMsec(res.getMsecs().get(clusterPos));
            }
        }
        return featureClusters;
    }

    public String clusterHierarchical(ClusterHierarchicalParams params) throws Exception {
        ObjectData objData = getWsClient().getObjects(Arrays.asList(
                new ObjectIdentity().withRef(params.getInputData()))).get(0);
        BioMatrix matrix = objData.getData().asClassInstance(BioMatrix.class);
        ClusterServiceLocalClient mathClient = getMathClient();
        ClusterResults res = mathClient.clusterHierarchical(matrix.getData(), params.getDistanceMetric(), 
                params.getLinkageCriteria(), params.getFeatureHeightCutoff(), 1L);
        ClusterSet toSave = new ClusterSet().withOriginalData(params.getInputData())
                .withFeatureClusters(clustersFromLabels(matrix, res))
                .withFeatureDendrogram(res.getDendrogram());
        List<ProvenanceAction> provenance = Arrays.asList(
                new ProvenanceAction().withService(KBaseFeatureValuesServer.SERVICE_NAME)
                .withServiceVer(KBaseFeatureValuesServer.SERVICE_VERSION)
                .withDescription("Hierarchical clustering method")
                .withInputWsObjects(Arrays.asList(params.getInputData()))
                .withMethod("cluster_hierarchical")
                .withMethodParams(Arrays.asList(new UObject(params))));
        getWsClient().saveObjects(new SaveObjectsParams().withWorkspace(params.getOutWorkspace())
                .withObjects(Arrays.asList(new ObjectSaveData()
                .withType("KBaseFeatureValues.ClusterSet").withName(params.getOutClustersetId())
                .withData(new UObject(toSave)).withProvenance(provenance))));
        return params.getOutWorkspace() + "/" + params.getOutClustersetId();
    }

    public String clustersFromDendrogram(ClustersFromDendrogramParams params) throws Exception {
        ObjectData objData = getWsClient().getObjects(Arrays.asList(
                new ObjectIdentity().withRef(params.getInputData()))).get(0);
        ClusterSet input = objData.getData().asClassInstance(ClusterSet.class);
        ObjectData objData2 = getWsClient().getObjects(Arrays.asList(
                new ObjectIdentity().withRef(input.getOriginalData()))).get(0);
        BioMatrix matrix = objData2.getData().asClassInstance(BioMatrix.class);
        ClusterServiceLocalClient mathClient = getMathClient();
        ClusterResults res = mathClient.clustersFromDendrogram(matrix.getData(), 
                input.getFeatureDendrogram(), params.getFeatureHeightCutoff());
        ClusterSet toSave = new ClusterSet().withOriginalData(input.getOriginalData())
                .withFeatureClusters(clustersFromLabels(matrix, res))
                .withFeatureDendrogram(res.getDendrogram());
        List<ProvenanceAction> provenance = Arrays.asList(
                new ProvenanceAction().withService(KBaseFeatureValuesServer.SERVICE_NAME)
                .withServiceVer(KBaseFeatureValuesServer.SERVICE_VERSION)
                .withDescription("Clusters from dendrogram")
                .withInputWsObjects(Arrays.asList(params.getInputData()))
                .withMethod("clusters_from_dendrogram")
                .withMethodParams(Arrays.asList(new UObject(params))));
        getWsClient().saveObjects(new SaveObjectsParams().withWorkspace(params.getOutWorkspace())
                .withObjects(Arrays.asList(new ObjectSaveData()
                .withType("KBaseFeatureValues.ClusterSet").withName(params.getOutClustersetId())
                .withData(new UObject(toSave)).withProvenance(provenance))));
        return params.getOutWorkspace() + "/" + params.getOutClustersetId();
    }

    public void evaluateClustersetQuality(EvaluateClustersetQualityParams params) throws Exception {
        throw new IllegalStateException("Not yet implemented");
    }

    public void validateMatrix(ValidateMatrixParams params) throws Exception {
        throw new IllegalStateException("Not yet implemented");
    }

    public void correctMatrix(CorrectMatrixParams params) throws Exception {
        throw new IllegalStateException("Not yet implemented");
    }

    @SuppressWarnings("unchecked")
    public MatrixDescriptor getMatrixDescriptor(GetMatrixDescriptorParams params) throws Exception {
        WorkspaceClient wsCl = getWsClient();
        ObjectData obj = wsCl.getObjectSubset(Arrays.asList(new SubObjectIdentity().withRef(
                params.getInputData()).withIncluded(Arrays.asList("data/col_ids", "data/row_ids", 
                        "genome_ref", "scale", "type","row_normalization", "col_normalization")))).get(0);
        Map<String, Object> matrix = obj.getData().asClassInstance(Map.class);
        String matrixId = obj.getInfo().getE2();
        String matrixName = obj.getInfo().getE2();
        String matrixDescription = obj.getInfo().getE2();
        String genomeId = null;
        String genomeName = null;
        int rowCount = -1;
        int colCount = -1;
        String scale = (String)matrix.get("scale");
        String type = (String)matrix.get("type");
        String rowNormalization = (String)matrix.get("row_normalization");
        String colNormalization = (String)matrix.get("col_normalization");
        Map<String, Object> data = (Map<String, Object>)matrix.get("data");
        if (data != null) {
            List<String> rowIds = (List<String>)data.get("row_ids");
            rowCount = rowIds.size();
            List<String> colIds = (List<String>)data.get("col_ids");
            colCount = colIds.size();
        } else {
            Map<String, String> meta = obj.getInfo().getE11();
            if (meta.containsKey("feature_count"))
                rowCount = Integer.parseInt(meta.get("feature_count"));
            if (meta.containsKey("condition_count"))
                colCount = Integer.parseInt(meta.get("condition_count"));
        }
        String genomeRef = (String)matrix.get("genome_ref");
        if (genomeRef != null) {
            ObjectData genomeObj = wsCl.getObjectSubset(Arrays.asList(new SubObjectIdentity().withRef(
                    genomeRef).withIncluded(Arrays.asList("scientific_name")))).get(0);
            Map<String, Object> genomeMap = genomeObj.getData().asClassInstance(Map.class);
            genomeId = genomeObj.getInfo().getE2();
            genomeName = (String)genomeMap.get("scientific_name");
        }
        return new MatrixDescriptor().withMatrixId(matrixId).withMatrixName(matrixName)
                .withMatrixDescription(matrixDescription).withGenomeId(genomeId)
                .withGenomeName(genomeName).withRowsCount((long)rowCount)
                .withColumnsCount((long)colCount).withScale(scale).withType(type)
                .withRowNormalization(rowNormalization).withColNormalization(colNormalization);
    }
    
    public static class BioMatrix {
        @JsonProperty("genome_ref")
        private java.lang.String genomeRef;
        @JsonProperty("feature_mapping")
        private Map<String, String> featureMapping;
        @JsonProperty("data")
        private FloatMatrix2D data;
        private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();
        
        @JsonProperty("genome_ref")
        public java.lang.String getGenomeRef() {
            return genomeRef;
        }

        @JsonProperty("genome_ref")
        public void setGenomeRef(java.lang.String genomeRef) {
            this.genomeRef = genomeRef;
        }

        @JsonProperty("feature_mapping")
        public Map<String, String> getFeatureMapping() {
            return featureMapping;
        }

        @JsonProperty("feature_mapping")
        public void setFeatureMapping(Map<String, String> featureMapping) {
            this.featureMapping = featureMapping;
        }

        @JsonProperty("data")
        public FloatMatrix2D getData() {
            return data;
        }

        @JsonProperty("data")
        public void setData(FloatMatrix2D data) {
            this.data = data;
        }

        @JsonAnyGetter
        public Map<java.lang.String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperties(java.lang.String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }
}
