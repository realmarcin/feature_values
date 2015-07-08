package us.kbase.clusterservice;

import java.io.IOException;

import us.kbase.common.service.JsonClientException;
import us.kbase.kbasefeaturevalues.FloatMatrix2D;

public interface ClusterServiceLocalClient {

    public ClusterResults clusterKMeans(FloatMatrix2D matrix, Long k) throws IOException, JsonClientException;
    
    public Long estimateK(FloatMatrix2D matrix) throws IOException, JsonClientException;
    
    public ClusterResults clusterHierarchical(FloatMatrix2D matrix, String distanceMetric, String linkageCriteria, Double heightCutoff, Long processRows) throws IOException, JsonClientException;
    
    public ClusterResults clustersFromDendrogram(String dendrogram, Double heightCutoff) throws IOException, JsonClientException;
}
