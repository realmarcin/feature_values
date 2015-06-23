package us.kbase.kbasefeaturevalues;

import java.io.File;
import us.kbase.auth.AuthToken;
import us.kbase.common.service.JsonServerMethod;
import us.kbase.common.service.JsonServerServlet;
import us.kbase.common.service.JsonServerSyslog;

//BEGIN_HEADER
//END_HEADER

/**
 * <p>Original spec-file module name: KBaseFeatureValues</p>
 * <pre>
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
     * <p>Original spec-file function name: cluster_features</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link us.kbase.kbasefeaturevalues.ClusterFeaturesParams ClusterFeaturesParams}
     * @return   parameter "job_id" of String
     */
    @JsonServerMethod(rpc = "KBaseFeatureValues.cluster_features")
    public String clusterFeatures(ClusterFeaturesParams params, AuthToken authPart) throws Exception {
        String returnVal = null;
        //BEGIN cluster_features
        //END cluster_features
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: validate_matrix</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link us.kbase.kbasefeaturevalues.ValidateMatrixParams ValidateMatrixParams}
     * @return   instance of type {@link us.kbase.kbasefeaturevalues.AnalysisReport AnalysisReport}
     */
    @JsonServerMethod(rpc = "KBaseFeatureValues.validate_matrix", authOptional=true)
    public AnalysisReport validateMatrix(ValidateMatrixParams params, AuthToken authPart) throws Exception {
        AnalysisReport returnVal = null;
        //BEGIN validate_matrix
        //END validate_matrix
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: correct_matrix</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link us.kbase.kbasefeaturevalues.CorrectMatrixParams CorrectMatrixParams}
     */
    @JsonServerMethod(rpc = "KBaseFeatureValues.correct_matrix")
    public void correctMatrix(CorrectMatrixParams params, AuthToken authPart) throws Exception {
        //BEGIN correct_matrix
        //END correct_matrix
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            new KBaseFeatureValuesServer().startupServer(Integer.parseInt(args[0]));
        } else {
            System.out.println("Usage: <program> <server_port>");
            System.out.println("   or: <program> <context_json_file> <output_json_file> <token>");
            return;
        }
    }
}
