package us.kbase.clusterservice;

import java.io.IOException;

import us.kbase.common.service.JsonClientException;

public interface ClusterServiceLocalClient {

    public ClusterResults clusterFloatRowsKmeans(ClusterFloatRowsKmeansParams params) throws IOException, JsonClientException;
    
}
