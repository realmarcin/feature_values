package us.kbase.kbasefeaturevalues;

import us.kbase.auth.AuthToken;
import us.kbase.common.service.JsonServerMethod;
import us.kbase.common.service.JsonServerServlet;

//BEGIN_HEADER
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import us.kbase.common.service.UObject;
import us.kbase.common.utils.AweUtils;
import us.kbase.common.utils.TextUtils;
import us.kbase.userandjobstate.InitProgress;
import us.kbase.userandjobstate.UserAndJobStateClient;
//END_HEADER

/**
 * <p>Original spec-file module name: KBaseFeatureValues</p>
 * <pre>
 * The KBaseFeatureValues set of data types and service provides a mechanism for
 * representing numeric values associated with genome features and conditions, together
 * with some basic operations on this data.  Essentially, the data is stored as a simple
 * 2D matrix of floating point numbers.  Currently, this is exposed as support for
 * expression data and single gene knockout fitness data.  (Fitness data being growth
 * rate relative to WT growth with the specified single gene knockout in a specified
 * condition).
 * The operations supported on this data is simple clustering of genes and clustering 
 * related tools.
 * </pre>
 */
public class KBaseFeatureValuesServer extends JsonServerServlet {
    private static final long serialVersionUID = 1L;

    //BEGIN_CLASS_HEADER
    public static final String SERVICE_NAME = "KBaseFeatureValues";
    public static final String CONFIG_PARAM_SCRATCH = "scratch";
    public static final String CONFIG_PARAM_WS_URL = "ws.url";
    public static final String CONFIG_PARAM_UJS_URL = "ujs.url";
    public static final String CONFIG_PARAM_SHOCK_URL = "shock.url";
    public static final String CONFIG_PARAM_AWE_URL = "awe.url";
    public static final String CONFIG_PARAM_CLIENT_BIN_DIR = "client.bin.dir";
    public static final String CONFIG_PARAM_CLIENT_WORK_DIR = "client.work.dir";
    public static final String AWE_CLIENT_SCRIPT_NAME = "awe_" + SERVICE_NAME + "_run_job.sh";
    
    private UserAndJobStateClient getUjsClient(AuthToken auth) throws Exception {
        String ujsUrl = config.get(CONFIG_PARAM_UJS_URL);
        if (ujsUrl == null)
            throw new IllegalStateException("Parameter '" + CONFIG_PARAM_UJS_URL +
                    "' is not defined in configuration");
        UserAndJobStateClient ret = new UserAndJobStateClient(new URL(ujsUrl), auth);
        ret.setIsInsecureHttpConnectionAllowed(true);
        return ret;
    }
    
    private String getAweUrl() throws Exception {
        String aweUrl = config.get(CONFIG_PARAM_AWE_URL);
        if (aweUrl == null)
            throw new IllegalStateException("Parameter '" + CONFIG_PARAM_AWE_URL +
                    "' is not defined in configuration");
        return aweUrl;
    }
    
    private String runAweJob(AuthToken authPart, Object... params) throws Exception {
        StackTraceElement[] st = new Exception().getStackTrace();
        String methodName = null;
        String className = getClass().getName();
        for (int firstPos = 0; firstPos < st.length; firstPos++) {
            if (st[firstPos].getMethodName().equals("runAweJob") || 
                    !st[firstPos].getClassName().equals(className))
                continue;
            methodName = st[firstPos].getMethodName();
            break;
        }
        Map<String, Object> args = new LinkedHashMap<String, Object>();
        UserAndJobStateClient ujsClient = getUjsClient(authPart);
        String jobId = ujsClient.createAndStartJob(authPart.toString(), "queued", 
                "AWE job for " + SERVICE_NAME + "." + methodName, 
                new InitProgress().withPtype("none"), null);
        args.put("method", methodName);
        args.put("params", Arrays.asList(params));
        args.put("config", config);
        args.put("jobid", jobId);
        String argsHex = TextUtils.stringToHex(UObject.getMapper().writeValueAsString(args));
        String aweJobId = AweUtils.runTask(getAweUrl(), SERVICE_NAME, methodName, argsHex, 
                AWE_CLIENT_SCRIPT_NAME, authPart.toString());
        //System.out.println("AWE job id: " + aweJobId);
        return jobId;
    }
    
    //END_CLASS_HEADER

    public KBaseFeatureValuesServer() throws Exception {
        super("KBaseFeatureValues");
        //BEGIN_CONSTRUCTOR
        //END_CONSTRUCTOR
    }

    /**
     * <p>Original spec-file function name: estimate_k</p>
     * <pre>
     * Used as an analysis step before generating clusters using K-means clustering, this method
     * provides an estimate of K by [...]
     * </pre>
     * @param   params   instance of type {@link us.kbase.kbasefeaturevalues.EstimateKParams EstimateKParams}
     * @return   parameter "job_id" of String
     */
    @JsonServerMethod(rpc = "KBaseFeatureValues.estimate_k")
    public String estimateK(EstimateKParams params, AuthToken authPart) throws Exception {
        String returnVal = null;
        //BEGIN estimate_k
        returnVal = runAweJob(authPart, params);
        //END estimate_k
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: cluster_k_means</p>
     * <pre>
     * Clusters features by K-means clustering.
     * </pre>
     * @param   params   instance of type {@link us.kbase.kbasefeaturevalues.ClusterKMeansParams ClusterKMeansParams}
     * @return   parameter "job_id" of String
     */
    @JsonServerMethod(rpc = "KBaseFeatureValues.cluster_k_means")
    public String clusterKMeans(ClusterKMeansParams params, AuthToken authPart) throws Exception {
        String returnVal = null;
        //BEGIN cluster_k_means
        try {
            returnVal = runAweJob(authPart, params);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        //END cluster_k_means
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: cluster_hierarchical</p>
     * <pre>
     * Clusters features by hierarchical clustering.
     * </pre>
     * @param   params   instance of type {@link us.kbase.kbasefeaturevalues.ClusterHierarchicalParams ClusterHierarchicalParams}
     * @return   parameter "job_id" of String
     */
    @JsonServerMethod(rpc = "KBaseFeatureValues.cluster_hierarchical")
    public String clusterHierarchical(ClusterHierarchicalParams params, AuthToken authPart) throws Exception {
        String returnVal = null;
        //BEGIN cluster_hierarchical
        returnVal = runAweJob(authPart, params);
        //END cluster_hierarchical
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: clusters_from_dendrogram</p>
     * <pre>
     * Given a ClusterSet with a dendogram built from a hierarchical clustering
     * method, this function creates new clusters by cutting the dendogram at
     * a specific hieght or by some other approach.
     * </pre>
     * @param   params   instance of type {@link us.kbase.kbasefeaturevalues.ClustersFromDendrogramParams ClustersFromDendrogramParams}
     * @return   parameter "job_id" of String
     */
    @JsonServerMethod(rpc = "KBaseFeatureValues.clusters_from_dendrogram")
    public String clustersFromDendrogram(ClustersFromDendrogramParams params, AuthToken authPart) throws Exception {
        String returnVal = null;
        //BEGIN clusters_from_dendrogram
        returnVal = runAweJob(authPart, params);
        //END clusters_from_dendrogram
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: evaluate_clusterset_quality</p>
     * <pre>
     * Given a ClusterSet with a dendogram built from a hierarchical clustering
     * method, this function creates new clusters by cutting the dendogram at
     * a specific hieght or by some other approach.
     * </pre>
     * @param   params   instance of type {@link us.kbase.kbasefeaturevalues.EvaluateClustersetQualityParams EvaluateClustersetQualityParams}
     * @return   parameter "job_id" of String
     */
    @JsonServerMethod(rpc = "KBaseFeatureValues.evaluate_clusterset_quality")
    public String evaluateClustersetQuality(EvaluateClustersetQualityParams params, AuthToken authPart) throws Exception {
        String returnVal = null;
        //BEGIN evaluate_clusterset_quality
        returnVal = runAweJob(authPart, params);
        //END evaluate_clusterset_quality
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: validate_matrix</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link us.kbase.kbasefeaturevalues.ValidateMatrixParams ValidateMatrixParams}
     * @return   parameter "job_id" of String
     */
    @JsonServerMethod(rpc = "KBaseFeatureValues.validate_matrix", authOptional=true)
    public String validateMatrix(ValidateMatrixParams params, AuthToken authPart) throws Exception {
        String returnVal = null;
        //BEGIN validate_matrix
        returnVal = runAweJob(authPart, params);
        //END validate_matrix
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: correct_matrix</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link us.kbase.kbasefeaturevalues.CorrectMatrixParams CorrectMatrixParams}
     * @return   parameter "job_id" of String
     */
    @JsonServerMethod(rpc = "KBaseFeatureValues.correct_matrix")
    public String correctMatrix(CorrectMatrixParams params, AuthToken authPart) throws Exception {
        String returnVal = null;
        //BEGIN correct_matrix
        returnVal = runAweJob(authPart, params);
        //END correct_matrix
        return returnVal;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: <program> <server_port>");
            return;
        }
        new KBaseFeatureValuesServer().startupServer(Integer.parseInt(args[0]));
    }
}
