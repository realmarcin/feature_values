package us.kbase.kbasefeaturevalues;

import us.kbase.auth.AuthToken;
import us.kbase.common.service.JsonServerMethod;
import us.kbase.common.service.JsonServerServlet;

//BEGIN_HEADER
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
