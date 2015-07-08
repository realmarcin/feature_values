package us.kbase.clusterservice;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import us.kbase.common.service.JsonClientException;
import us.kbase.common.service.JsonLocalClientCaller;
import us.kbase.kbasefeaturevalues.FloatMatrix2D;

/**
 * <p>Original spec-file module name: ClusterServiceR</p>
 * <pre>
 * </pre>
 */
public class ClusterServiceRLocalClient extends JsonLocalClientCaller implements ClusterServiceLocalClient {
    
    public ClusterServiceRLocalClient(File workDir) {
        super(workDir);
    }

    /**
     * <p>Original spec-file function name: cluster_k_means</p>
     * <pre>
     * Clusters features by K-means clustering.
     * </pre>
     * @param   matrix   instance of type {@link us.kbase.kbasefeaturevalues.FloatMatrix2D FloatMatrix2D}
     * @param   k   instance of Long
     * @return   instance of type {@link us.kbase.clusterservicepy.ClusterResults ClusterResults}
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public ClusterResults clusterKMeans(FloatMatrix2D matrix, Long k) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(matrix);
        args.add(k);
        TypeReference<List<ClusterResults>> retType = new TypeReference<List<ClusterResults>>() {};
        List<ClusterResults> res = jsonrpcCall("ClusterServiceR.cluster_k_means", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: estimate_k</p>
     * <pre>
     * Used as an analysis step before generating clusters using K-means 
     * clustering, this method provides an estimate of K by [...]
     * </pre>
     * @param   matrix   instance of type {@link us.kbase.kbasefeaturevalues.FloatMatrix2D FloatMatrix2D}
     * @return   parameter "cluster_number" of Long
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public Long estimateK(FloatMatrix2D matrix) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(matrix);
        TypeReference<List<Long>> retType = new TypeReference<List<Long>>() {};
        List<Long> res = jsonrpcCall("ClusterServiceR.estimate_k", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: cluster_hierarchical</p>
     * <pre>
     * Clusters features by hierarchical clustering.
     * </pre>
     * @param   matrix   instance of type {@link us.kbase.kbasefeaturevalues.FloatMatrix2D FloatMatrix2D}
     * @param   distanceMetric   instance of String
     * @param   linkageCriteria   instance of String
     * @param   heightCutoff   instance of Double
     * @param   processRows   instance of original type "boolean" (Indicates true or false values, false = 0, true = 1 @range [0,1])
     * @return   instance of type {@link us.kbase.clusterservicepy.ClusterResults ClusterResults}
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public ClusterResults clusterHierarchical(FloatMatrix2D matrix, String distanceMetric, String linkageCriteria, Double heightCutoff, Long processRows) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(matrix);
        args.add(distanceMetric);
        args.add(linkageCriteria);
        args.add(heightCutoff);
        args.add(processRows);
        TypeReference<List<ClusterResults>> retType = new TypeReference<List<ClusterResults>>() {};
        List<ClusterResults> res = jsonrpcCall("ClusterServiceR.cluster_hierarchical", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: clusters_from_dendrogram</p>
     * <pre>
     * Given a ClusterSet with a dendogram built from a hierarchical clustering
     * method, this function creates new clusters by cutting the dendogram at
     * a specific hieght or by some other approach.
     * </pre>
     * @param   dendrogram   instance of String
     * @param   heightCutoff   instance of Double
     * @return   instance of type {@link us.kbase.clusterservicepy.ClusterResults ClusterResults}
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public ClusterResults clustersFromDendrogram(String dendrogram, Double heightCutoff) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(dendrogram);
        args.add(heightCutoff);
        TypeReference<List<ClusterResults>> retType = new TypeReference<List<ClusterResults>>() {};
        List<ClusterResults> res = jsonrpcCall("ClusterServiceR.clusters_from_dendrogram", args, retType, true, false);
        return res.get(0);
    }
}
