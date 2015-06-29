package us.kbase.kbasefeaturevalues;

import java.io.File;
import java.net.URL;
import java.util.Map;

import us.kbase.auth.AuthToken;
import us.kbase.userandjobstate.UserAndJobStateClient;
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

    public void estimateK(EstimateKParams params) throws Exception {
    }

    public String clusterKMeans(ClusterKMeansParams params) throws Exception {
        System.out.println("In clusterKMeans: input=" + params.getInputData() + ", " +
        		"k=" + params.getK() + ", workDir=" + workDir);
        return null;
    }

    public void clusterHierarchical(ClusterHierarchicalParams params) throws Exception {
    }

    public void clustersFromDendrogram(ClustersFromDendrogramParams params) throws Exception {
    }

    public void evaluateClustersetQuality(EvaluateClustersetQualityParams params) throws Exception {
    }

    public void validateMatrix(ValidateMatrixParams params) throws Exception {
    }

    public void correctMatrix(CorrectMatrixParams params) throws Exception {
    }

}
