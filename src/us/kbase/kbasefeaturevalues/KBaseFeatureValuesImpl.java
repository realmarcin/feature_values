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
    
    public void estimateK(EstimateKParams params) throws Exception {
        throw new IllegalStateException("Not yet implemented");
    }

    public String clusterKMeans(ClusterKMeansParams params) throws Exception {
        ObjectData objData = getWsClient().getObjects(Arrays.asList(
                new ObjectIdentity().withRef(params.getInputData()))).get(0);
        BioMatrix matrix = objData.getData().asClassInstance(BioMatrix.class);
        ClusterServiceLocalClient mathClient = getMathClient();
        ClusterResults res = mathClient.clusterKMeans(matrix.getData(), params.getK());
        System.out.println("Cluster labels: " + res.getClusterLabels());
        List<Map<String, Long>> featureClusters = new ArrayList<Map<String, Long>>();
        Map<Long, Map<String, Long>> labelToCluster = new LinkedHashMap<Long, Map<String, Long>>();
        ClusterSet toSave = new ClusterSet().withOriginalData(params.getInputData())
                .withFeatureClusters(featureClusters);
        for (int featurePos = 0; featurePos < res.getClusterLabels().size(); featurePos++) {
            long clusterLabel = res.getClusterLabels().get(featurePos);
            Map<String, Long> cluster = labelToCluster.get(clusterLabel);
            if (cluster == null) {
                cluster = new LinkedHashMap<String, Long>();
                labelToCluster.put(clusterLabel, cluster);
                featureClusters.add(cluster);
            }
            String featureLabel = matrix.getData().getRowIds().get(featurePos);
            cluster.put(featureLabel, (long)featurePos);
        }
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

    public void clusterHierarchical(ClusterHierarchicalParams params) throws Exception {
        throw new IllegalStateException("Not yet implemented");
    }

    public void clustersFromDendrogram(ClustersFromDendrogramParams params) throws Exception {
        throw new IllegalStateException("Not yet implemented");
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
