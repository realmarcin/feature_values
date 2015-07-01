package us.kbase.clusterservice;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import us.kbase.common.service.JsonClientException;
import us.kbase.common.service.JsonLocalClientCaller;

/**
 * <p>Original spec-file module name: ClusterServicePy</p>
 * <pre>
 * </pre>
 */
public class ClusterServicePyLocalClient extends JsonLocalClientCaller implements ClusterServiceLocalClient {
    
    public ClusterServicePyLocalClient(File workDir) {
        super(workDir);
    }

    /**
     * <p>Original spec-file function name: cluster_float_rows_kmeans</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link us.kbase.clusterservice.ClusterFloatRowsKmeansParams ClusterFloatRowsKmeansParams}
     * @return   instance of type {@link us.kbase.clusterservice.ClusterResults ClusterResults}
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public ClusterResults clusterFloatRowsKmeans(ClusterFloatRowsKmeansParams params) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(params);
        TypeReference<List<ClusterResults>> retType = new TypeReference<List<ClusterResults>>() {};
        List<ClusterResults> res = jsonrpcCall("ClusterServicePy.cluster_float_rows_kmeans", args, retType, true, false);
        return res.get(0);
    }
}
