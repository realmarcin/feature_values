package us.kbase.kbasefeaturevalues;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import us.kbase.auth.AuthToken;
import us.kbase.clusterservice.ClusterResults;
import us.kbase.clusterservice.ClusterServiceLocalClient;
import us.kbase.clusterservice.ClusterServiceRLocalClient;
import us.kbase.common.service.UObject;
import us.kbase.kbasegenomes.Feature;
import us.kbase.userandjobstate.UserAndJobStateClient;
import us.kbase.workspace.ObjectData;
import us.kbase.workspace.ObjectIdentity;
import us.kbase.workspace.ObjectSaveData;
import us.kbase.workspace.ProvenanceAction;
import us.kbase.workspace.SaveObjectsParams;
import us.kbase.workspace.SubObjectIdentity;
import us.kbase.workspace.WorkspaceClient;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

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

    public String correctMatrix(CorrectMatrixParams params) throws Exception {
        ObjectData objData = getWsClient().getObjects(Arrays.asList(
                new ObjectIdentity().withRef(params.getInputData()))).get(0);
        String inputType = objData.getInfo().getE3();
        BioMatrix matrix = objData.getData().asClassInstance(BioMatrix.class);
        String transType = params.getTransformType();
        if (transType == null || !transType.equals("missing"))
            throw new IllegalStateException("Unsupported transformation type: " + transType);
        MatrixUtil.fillMissingValues(matrix.getData());
        String outMatrixId = params.getOutMatrixId();
        if (outMatrixId == null)
            outMatrixId = objData.getInfo().getE2();
        List<ProvenanceAction> provenance = Arrays.asList(
                new ProvenanceAction().withService(KBaseFeatureValuesServer.SERVICE_NAME)
                .withServiceVer(KBaseFeatureValuesServer.SERVICE_VERSION)
                .withDescription("Correcting matrix values")
                .withInputWsObjects(Arrays.asList(params.getInputData()))
                .withMethod("correct_matrix")
                .withMethodParams(Arrays.asList(new UObject(params))));
        getWsClient().saveObjects(new SaveObjectsParams().withWorkspace(params.getOutWorkspace())
                .withObjects(Arrays.asList(new ObjectSaveData()
                .withType(inputType).withName(outMatrixId)
                .withData(new UObject(matrix)).withProvenance(provenance))));
        return params.getOutWorkspace() + "/" + outMatrixId;
    }

    public String reconnectMatrixToGenome(ReconnectMatrixToGenomeParams params) throws Exception {
        ObjectData objData = getWsClient().getObjects(Arrays.asList(
                new ObjectIdentity().withRef(params.getInputData()))).get(0);
        String inputType = objData.getInfo().getE3();
        BioMatrix matrix = objData.getData().asClassInstance(BioMatrix.class);
        Map<String, Object> genome = MatrixUtil.loadGenomeFeatures(getWsClient(), params.getGenomeRef());
        matrix.setFeatureMapping(MatrixUtil.constructFeatureMapping(matrix.getData(), genome));
        matrix.setGenomeRef(params.getGenomeRef());
        String outMatrixId = params.getOutMatrixId();
        if (outMatrixId == null)
            outMatrixId = objData.getInfo().getE2();
        List<ProvenanceAction> provenance = Arrays.asList(
                new ProvenanceAction().withService(KBaseFeatureValuesServer.SERVICE_NAME)
                .withServiceVer(KBaseFeatureValuesServer.SERVICE_VERSION)
                .withDescription("Reconnection of matrix rows to genome features")
                .withInputWsObjects(Arrays.asList(params.getInputData()))
                .withMethod("reconnect_matrix_to_genome")
                .withMethodParams(Arrays.asList(new UObject(params))));
        getWsClient().saveObjects(new SaveObjectsParams().withWorkspace(params.getOutWorkspace())
                .withObjects(Arrays.asList(new ObjectSaveData()
                .withType(inputType).withName(outMatrixId)
                .withData(new UObject(matrix)).withProvenance(provenance))));
        return params.getOutWorkspace() + "/" + outMatrixId;
    }

    @SuppressWarnings("unchecked")
    public MatrixDescriptor getMatrixDescriptor(GetMatrixDescriptorParams params) throws Exception {
        WorkspaceClient wsCl = getWsClient();
        ObjectData obj = wsCl.getObjectSubset(Arrays.asList(new SubObjectIdentity().withRef(
                params.getInputData()).withIncluded(Arrays.asList("data/col_ids", "data/row_ids", 
                        "genome_ref", "scale", "type","row_normalization", "col_normalization")))).get(0);
        BioMatrix matrix = obj.getData().asClassInstance(BioMatrix.class);
        String matrixId = obj.getInfo().getE2();
        String matrixName = obj.getInfo().getE2();
        String matrixDescription = obj.getInfo().getE2();
        String genomeId = null;
        String genomeName = null;
        int rowCount = -1;
        int colCount = -1;
        String scale = (String)matrix.getAdditionalProperties().get("scale");
        String type = (String)matrix.getAdditionalProperties().get("type");
        String rowNormalization = (String)matrix.getAdditionalProperties().get("row_normalization");
        String colNormalization = (String)matrix.getAdditionalProperties().get("col_normalization");
        FloatMatrix2D data = matrix.getData();
        if (data != null) {
            List<String> rowIds = data.getRowIds();
            rowCount = rowIds.size();
            List<String> colIds = data.getColIds();
            colCount = colIds.size();
        } else {
            Map<String, String> meta = obj.getInfo().getE11();
            if (meta.containsKey("feature_count"))
                rowCount = Integer.parseInt(meta.get("feature_count"));
            if (meta.containsKey("condition_count"))
                colCount = Integer.parseInt(meta.get("condition_count"));
        }
        String genomeRef = (String)matrix.getGenomeRef();
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
    
    
	@SuppressWarnings("unchecked")
	public MatrixStat getMatrixStat(GetMatrixStatParams params) throws Exception {

		MatrixStat matrixUI = new MatrixStat();
        WorkspaceClient wsClient = getWsClient();
		
        // Get expression matrix
		ObjectData matrixData = getExpressionMatrixObject(params.getInputData());
		ExpressionMatrix matrix = (ExpressionMatrix)  matrixData
			.getData()
			.asClassInstance(ExpressionMatrix.class);
                
        // Get genome properties
        String genomeId = null;
        String genomeName = null;
        Hashtable<String,Feature> featureId2Feature = null;
                
        if (matrix.getGenomeRef() != null) {
        	SubObjectIdentity genomeIndentity = new SubObjectIdentity()
        		.withRef( matrix.getGenomeRef() )
        		.withIncluded( Arrays.asList("id", "scientific_name", "features") );
        	
            ObjectData genomeData = wsClient
            	.getObjectSubset(Arrays.asList(genomeIndentity))
            	.get(0);
            
            Map<String, Object> genomeDataMap = (Map<String, Object>) genomeData
            	.getData()
            	.asClassInstance(Map.class);
            
            genomeId = (String) genomeDataMap.get("id");
            genomeName = (String) genomeDataMap.get("scientific_name");  
//            List<Feature> features = UObject.transformObjectToObject(genomeDataMap.get("features"), new TypeReference<List<Feature>>() {}); 
// 				Gives: java.lang.TypeNotPresentException: Type us.kbase.common.service.Tuple3 not present            
            List<Feature> features = new ArrayList<Feature>();  
            featureId2Feature = buildFeatureId2FeatureHash(features);            
        }
        
        
        // Build mtx descriptor
        MatrixDescriptor mtxDescriptor = new MatrixDescriptor()
        	.withColNormalization(matrix.getColNormalization())
        	.withColumnsCount((long) matrix.getData().getColIds().size())
        	.withGenomeId(genomeId)
        	.withGenomeName(genomeName)
        	.withMatrixDescription(matrix.getDescription())
        	.withMatrixId(matrixData.getInfo().getE2())
        	.withMatrixName(matrixData.getInfo().getE2())
        	.withRowNormalization(matrix.getRowNormalization())
        	.withRowsCount((long) matrix.getData().getRowIds().size())
        	.withScale(matrix.getScale())
        	.withType(matrix.getType());
        
        matrixUI.setMtxDescriptor(mtxDescriptor);
        
        // Build row and descriptors
        
        matrixUI.setRowDescriptors(buildRowDescriptors(matrix, featureId2Feature));
        matrixUI.setColumnDescriptors(buildColumnDescriptors(matrix));        
        
        // Collect statistics
        matrixUI.setRowStats(FloatMatrix2DUtil.getRowsStat(matrix.getData(), null, null, false));
        matrixUI.setColumnStats(FloatMatrix2DUtil.getColumnsStat(matrix.getData(), null, null, false));

		return matrixUI;
	}    
    
    private Hashtable<String, Feature> buildFeatureId2FeatureHash(
			List<Feature> features) {
    	Hashtable<String, Feature> featureId2Feature = new Hashtable<String, Feature>();
    	for(Feature f: features){
    		featureId2Feature.put(f.getId(), f);    		
    	}    	
		return featureId2Feature;
	}

	private List<ItemDescriptor> buildColumnDescriptors(ExpressionMatrix matrix) {
    	List<ItemDescriptor> descriptors = new ArrayList<ItemDescriptor>();
    	
    	// We do not have condition mapping now, so we will use just colIds...
    	List<String> colIds = matrix.getData().getColIds();
    	for(int i = 0; i < colIds.size(); i++){
    		ItemDescriptor desc = new ItemDescriptor()
    			.withDescription("")
    			.withId(colIds.get(i))
    			.withIndex((long)i)
    			.withName(colIds.get(i));
    		descriptors.add(desc);
    	}
    	
		return descriptors;
	}

	private List<ItemDescriptor> buildRowDescriptors(ExpressionMatrix matrix, Hashtable<String, Feature> featureId2Feature) {
    	List<ItemDescriptor> descriptors = new ArrayList<ItemDescriptor>();
    	
    	List<String> rowIds = matrix.getData().getRowIds();
    	for(int i = 0; i < rowIds.size(); i++){
    		
    		//TODO implement general approach to extract required properties. For now just function
    		Feature feature = featureId2Feature.get(rowIds.get(i));
    		
    		Hashtable<String,String> props = new Hashtable<String,String>();
    		if(feature != null){
        		String function = feature.getFunction();
	        	props.put("function", function != null ? function : "");	
    		}
    		    		
    		ItemDescriptor desc = new ItemDescriptor()
    			.withDescription("")
    			.withId(rowIds.get(i))
    			.withIndex((long)i)
    			.withName(rowIds.get(i))
    			.withProperties(props);
    		descriptors.add(desc);
    	}
    	
		return descriptors;
	}

	public  List<ItemStat> getMatrixRowsStat(GetMatrixItemsStatParams params) throws Exception {
        //TODO can be further optimized by getting subobjects
		System.out.println("params: " + params);
        ExpressionMatrix matrix = getExpressionMatrix(params.getInputData());
		return FloatMatrix2DUtil.getRowsStat(matrix.getData(), params.getItemIndecesFor() , params.getItemIndecesOn(), params.getFlIndecesOn() == 1);
	}	
	
	public  List<ItemStat> getMatrixColumnsStat(GetMatrixItemsStatParams params) throws Exception {
        //TODO can be further optimized by getting subobjects
		
		System.out.println("params: " + params);
        ExpressionMatrix matrix = getExpressionMatrix(params.getInputData());
		return FloatMatrix2DUtil.getColumnsStat(matrix.getData(), params.getItemIndecesFor() , params.getItemIndecesOn(), params.getFlIndecesOn() == 1);
	}	
	
	public List<ItemSetStat> getMatrixRowSetsStat(GetMatrixSetsStatParams params) throws Exception {
		List<ItemSetStat> setStats = new ArrayList<ItemSetStat>();
		
		ExpressionMatrix matrix;
		String matrixRef = "";
		for(GetMatrixSetStatParams setStatParam: params.getParams()){
			if(!matrixRef.equals(setStatParam.getInputData())){
				matrixRef = setStatParam.getInputData();
				matrix = getExpressionMatrix(matrixRef);
				
				ItemSetStat setStat = FloatMatrix2DUtil.getRowsSetStat(matrix.getData(), setStatParam);
				setStats.add(setStat);
			}
		}		
		return setStats;
	}
	
	public List<ItemSetStat> getMatrixColumnSetsStat(GetMatrixSetsStatParams params) throws Exception {
		List<ItemSetStat> setStats = new ArrayList<ItemSetStat>();
		
		ExpressionMatrix matrix;
		String matrixRef = "";
		for(GetMatrixSetStatParams setStatParam: params.getParams()){
			if(!matrixRef.equals(setStatParam.getInputData())){
				matrixRef = setStatParam.getInputData();
				matrix = getExpressionMatrix(matrixRef);
				
				ItemSetStat setStat = FloatMatrix2DUtil.getColumnsSetStat(matrix.getData(), setStatParam);
				setStats.add(setStat);
			}
		}		
		return setStats;
	}

	private ExpressionMatrix getExpressionMatrix(String mtxRef) throws Exception{
		ObjectData matrixData = getExpressionMatrixObject(mtxRef);
        	
		ExpressionMatrix matrix = (ExpressionMatrix)  matrixData
			.getData()
			.asClassInstance(ExpressionMatrix.class);
		return matrix;
	}
	
	private ObjectData getExpressionMatrixObject(String mtxRef) throws Exception{
        WorkspaceClient wsClient = getWsClient();
		ObjectIdentity mtxIndentity = new ObjectIdentity().withRef(mtxRef);
		return wsClient
        	.getObjects(Arrays.asList(mtxIndentity))
        	.get(0);		
	}	
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
