package us.kbase.kbasefeaturevalues.transform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import us.kbase.auth.AuthService;
import us.kbase.auth.AuthToken;
import us.kbase.common.service.UObject;
import us.kbase.kbasefeaturevalues.ExpressionMatrix;
import us.kbase.kbasefeaturevalues.FloatMatrix2D;
import us.kbase.workspace.ObjectIdentity;
import us.kbase.workspace.WorkspaceClient;

public class ExpressionUploader {
    private static Pattern tabDiv = Pattern.compile(Pattern.quote("\t"));
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        Args parsedArgs = new Args();
        CmdLineParser parser = new CmdLineParser(parsedArgs);
        parser.setUsageWidth(100);
        if (args.length == 0 || (args.length == 1 && (args[0].equals("-h") || 
                args[0].equals("--help")))) {
            parser.parseArgument();
            showUsage(parser, null, System.out);
            return;
        }
        try {
            parser.parseArgument(args);
        } catch (CmdLineException ex) {
            String message = ex.getMessage();
            showUsage(parser, message);
            return;
        }
        Map<String, Object> genome = null;
        if (parsedArgs.goName != null) {
            String user = System.getProperty("test.user");
            String pwd = System.getProperty("test.pwd");
            String tokenString = System.getenv("KB_AUTH_TOKEN");
            AuthToken token = tokenString == null ? AuthService.login(user, pwd).getToken() :
                new AuthToken(tokenString);
            WorkspaceClient cl = getWsClient(parsedArgs.wsUrl, token);
            UObject genomeObj = cl.getObjects(Arrays.asList(new ObjectIdentity().withRef(
                    parsedArgs.wsName + "/" + parsedArgs.goName))).get(0).getData();
            genome = genomeObj.asClassInstance(Map.class);
        }
        File inputFile = null;
        StringBuilder fileList = new StringBuilder();
        for (File f : parsedArgs.inDir.listFiles()) {
            if (!f.isFile())
                continue;
            if (f.getName().endsWith(".txt") || f.getName().endsWith(".tsv")) {
                inputFile = f;
                break;
            }
            if (fileList.length() > 0)
                fileList.append(", ");
            fileList.append(f.getName());
        }
        if (inputFile == null)
            throw new IllegalStateException("Input file with extention .txt or .tsv was not " +
            		"found among: " + fileList);
        ExpressionMatrix matrix = parseMicrobsOnlineData(new BufferedReader(new FileReader(
                inputFile)), parsedArgs.fmtType, parsedArgs.fillMissingValues, genome);
        System.out.println(matrix);
    }
    
    private static WorkspaceClient getWsClient(String wsUrl, AuthToken token) throws Exception {
        WorkspaceClient wsClient = new WorkspaceClient(new URL(wsUrl), token);
        wsClient.setAuthAllowedForHttp(true);
        return wsClient;
    }

    private static void showUsage(CmdLineParser parser, String message) {
        showUsage(parser, message, System.err);
    }
    
    private static void showUsage(CmdLineParser parser, String message, PrintStream out) {
        if (message != null)
            out.println(message);
        out.println("Program uploads expression data in different formats.");
        out.println("Usage: <program> [options...]");
        out.println("   Or: <program> {-h|--help}  - to see this help");
        parser.printUsage(out);
    }

    public static ExpressionMatrix parseMicrobsOnlineData(BufferedReader br, String formatType, 
            boolean fillMissingValues, Map<String, Object> genome) throws Exception {
        MOState state = MOState.init;
        Map<String, String> colNameToId = new LinkedHashMap<String, String>();
        List<String> colIds = new ArrayList<String>();
        List<String> rowIds = new ArrayList<String>();
        List<List<Double>> values = new ArrayList<List<Double>>();
        for (int pos = 0; ; pos++) {
            String line = br.readLine();
            if (line == null)
                break;
            switch (state) {
            case init:
                if (line.startsWith("^") && line.endsWith("Experiment MetaData")) {
                    state = MOState.condHeader;
                } else {
                    throw new IllegalStateException("Unexpected line for [" + state + "] state: " + line);
                }
                break;
            case condHeader:
                if (line.startsWith("!Log Level")) {
                    state = MOState.condBlock;
                } else {
                    throw new IllegalStateException("Unexpected line for [" + state + "] state: " + line);
                }
                break;
            case condBlock:
                if (line.startsWith("^") && line.endsWith("Data Section")) {
                    state = MOState.featHeader;
                } else if (line.startsWith("!")) {
                    String[] parts = tabDiv.split(line.substring(1));
                    String colName = parts[0] + " (" + parts[1] + ")";
                    String colId = parts[2];
                    if (!colId.contains(parts[1]))
                        colId += " (" + parts[1] + ")";
                    colNameToId.put(colName, colId);
                } else {
                    throw new IllegalStateException("Unexpected line for [" + state + "] state: " + line);
                }
                break;
            case featHeader:
                if (line.startsWith("!Locus Id\tSystematic_Name\t")) {
                    String[] parts = tabDiv.split(line);
                    for (int i = 2; i < parts.length; i++) {
                        String colName = parts[i];
                        String colId = colNameToId.get(colName);
                        if (colId == null)
                            throw new IllegalStateException("Id wasn't found for column with name: " + colName);
                        colIds.add(colId);
                    }
                    state = MOState.featBlock;
                } else {
                    throw new IllegalStateException("Unexpected line for [" + state + "] state: " + line);
                }
                break;
            case featBlock:
                String[] parts = tabDiv.split(line, -1);
                int partCount = parts.length;
                if (partCount > colIds.size() + 2 && parts[partCount - 1].isEmpty())
                    partCount--;
                if (partCount != colIds.size() + 2)
                    throw new IllegalStateException("Feature row contain " + parts.length + " != " +  
                            (colIds.size() + 2) + " cells at line " + (pos + 1) + ": " + line);
                String rowId = parts[1];
                int rowPos = rowIds.size();
                rowIds.add(rowId);
                if (values.size() != rowPos)
                    throw new IllegalStateException("Internal inconsistency: " + rowPos + " != " + values.size());
                List<Double> row = new ArrayList<Double>();
                values.add(row);
                for (int i = 0; i < colIds.size(); i++) {
                    String exprValueText = parts[2 + i];
                    Double exprVallue = exprValueText.isEmpty() ? null : Double.parseDouble(exprValueText);
                    row.add(exprVallue);
                }                    
                break;
            default:
                throw new IllegalStateException("Unsupported state: " + state);
            }
        }
        br.close();
        Map<String, String> featureMapping = null;
        if (genome != null) {
            featureMapping = new LinkedHashMap<String, String>();
            List<Object> features = (List<Object>)genome.get("features");
            System.out.println(features.size());
            System.out.println(features.get(0));
        }
        FloatMatrix2D matrix = new FloatMatrix2D().withValues(values).withColIds(colIds)
                .withRowIds(rowIds);
        ExpressionMatrix ret = new ExpressionMatrix().withType("log-ratio").withScale("1.0")
                .withData(matrix).withFeatureMapping(featureMapping);
        return ret;
    }
    
    public static class Args {
        @Option(name="-ws", aliases={"--workspace_service_url"}, usage="Workspace service URL", metaVar="<ws-url>")
        String wsUrl;
        
        @Option(name="-wn", aliases={"--workspace_name"}, usage="Workspace name", metaVar="<ws-name>")
        String wsName;

        @Option(name="-on", aliases={"--object_name"}, usage="Object name", metaVar="<obj-name>")
        String objName;

        @Option(name="-of", aliases={"--output_file_name"}, usage="Output file name", metaVar="<out-file>")
        String outFile;

        @Option(name="-id", aliases={"--input_directory"}, usage="Input directory", metaVar="<in-dir>")
        File inDir;

        @Option(name="-wd", aliases={"--working_directory"}, usage="Working directory", metaVar="<work-dir>")
        File workDir;

        @Option(name="-im", aliases={"--input_mapping"}, usage="Input mapping", metaVar="<in-map>")
        String inMap;

        @Option(name="-ft", aliases={"--format_type"}, usage="Format type", metaVar="<fmt-type>")
        String fmtType;

        @Option(name="-gn", aliases={"--genome_object_name"}, usage="Input mapping", metaVar="<gnm-name>")
        String goName;
        
        @Option(name="-fm", aliases={"--fill_missing_values"}, usage="Fill missing values (boolean, default value is false)")
        boolean fillMissingValues = false;        
    }
    
    enum MOState {
        init, condHeader, condBlock, featHeader, featBlock;
    };
}
