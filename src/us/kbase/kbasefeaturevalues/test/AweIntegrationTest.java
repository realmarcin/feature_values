package us.kbase.kbasefeaturevalues.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import us.kbase.auth.AuthException;
import us.kbase.auth.AuthService;
import us.kbase.auth.AuthToken;
import us.kbase.common.service.JsonClientCaller;
import us.kbase.common.service.ServerException;
import us.kbase.common.service.Tuple7;
import us.kbase.common.service.UObject;
import us.kbase.common.service.UnauthorizedException;
import us.kbase.common.utils.ProcessHelper;
import us.kbase.kbasefeaturevalues.ClusterHierarchicalParams;
import us.kbase.kbasefeaturevalues.ClusterKMeansParams;
import us.kbase.kbasefeaturevalues.ClusterSet;
import us.kbase.kbasefeaturevalues.ClustersFromDendrogramParams;
import us.kbase.kbasefeaturevalues.EstimateKParams;
import us.kbase.kbasefeaturevalues.EstimateKResult;
import us.kbase.kbasefeaturevalues.ExpressionMatrix;
import us.kbase.kbasefeaturevalues.FloatMatrix2D;
import us.kbase.kbasefeaturevalues.KBaseFeatureValuesClient;
import us.kbase.kbasefeaturevalues.KBaseFeatureValuesServer;
import us.kbase.kbasefeaturevalues.ServiceStatus;
import us.kbase.userandjobstate.UserAndJobStateClient;
import us.kbase.workspace.CreateWorkspaceParams;
import us.kbase.workspace.ObjectData;
import us.kbase.workspace.ObjectIdentity;
import us.kbase.workspace.ObjectSaveData;
import us.kbase.workspace.SaveObjectsParams;
import us.kbase.workspace.WorkspaceClient;
import us.kbase.workspace.WorkspaceIdentity;

public class AweIntegrationTest {
    private static AuthToken token = null;
    private static KBaseFeatureValuesClient client = null;
    private static File workDir = null;
    private static File mongoDir = null;
    //private static File shockDir = null;
    private static File aweServerDir = null;
    private static File aweClientDir = null;
    private static File fvServiceDir = null;
    private static Server fvService = null;
    private static String testWsName = null;
    
    private static int mongoInitWaitSeconds = 240;
    private static int aweServerInitWaitSeconds = 60;
    private static int fvJobWaitSeconds = 60;

    
    @BeforeClass
    public static void beforeClass() throws Exception {
        workDir = prepareWorkDir("integration");
        File scriptFile = new File(workDir, "check_deps.sh");
        writeFileLines(Arrays.asList(
                "#!/bin/bash",
                "bash " + new File("./deps/integration_tests.sh").getCanonicalPath()
                ), scriptFile);
        ProcessHelper.cmd("bash", scriptFile.getCanonicalPath()).exec(workDir);
        //
        mongoDir = new File(workDir, "mongo");
        //shockDir = new File(workDir, "shock");
        aweServerDir = new File(workDir, "awe_server");
        aweClientDir = new File(workDir, "awe_client");
        fvServiceDir = new File(workDir, "fv_service");
        File binDir = new File("bin");
        int mongoPort = startupMongo(System.getProperty("test.mongod.path"), mongoDir);
        //int shockPort = startupShock(System.getProperty("shock.path"), shockDir, mongoPort);
        File aweBinDir = new File("./test/deps/bin").getCanonicalFile();
        int awePort = startupAweServer(findAweBinary(aweBinDir, "awe-server"), aweServerDir, mongoPort);
        fvService = startupFVService(fvServiceDir, binDir, awePort);
        int jobServicePort = fvService.getConnectors()[0].getLocalPort();
        startupAweClient(findAweBinary(aweBinDir, "awe-client"), aweClientDir, awePort, binDir);
        token = getToken();
        client = new KBaseFeatureValuesClient(new URL("http://localhost:" + jobServicePort), token);
        client.setIsInsecureHttpConnectionAllowed(true);
        String machineName = java.net.InetAddress.getLocalHost().getHostName();
        machineName = machineName == null ? "nowhere" : machineName.toLowerCase().replaceAll("[^\\dA-Za-z_]|\\s", "_");
        long suf = System.currentTimeMillis();
        WorkspaceClient wscl = getWsClient();
        Exception error = null;
        for (int i = 0; i < 5; i++) {
            testWsName = "test_feature_values_" + machineName + "_" + suf;
            try {
                wscl.createWorkspace(new CreateWorkspaceParams().withWorkspace(testWsName));
                error = null;
                break;
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                error = ex;
            }
        }
        if (error != null)
            throw error;
    }
    
    private static String findAweBinary(File dir, String program) throws Exception {
        if (new File(dir, program).exists())
            return new File(dir, program).getAbsolutePath();
        return program;
    }
    
    @AfterClass
    public static void afterClass() throws Exception {
        if (fvService != null) {
            try {
                fvService.stop();
                System.out.println(fvServiceDir.getName() + " was stopped");
            } catch (Exception ignore) {}
        }
        killPid(aweClientDir);
        killPid(aweServerDir);
        //killPid(shockDir);
        killPid(mongoDir);
        try {
            if (testWsName != null) {
                getWsClient().deleteWorkspace(new WorkspaceIdentity().withWorkspace(testWsName));
                //System.out.println("Test workspace " + testWsName + " was deleted");
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Test
    public void testServiceStatus() throws Exception {
        ServiceStatus status = client.status();
        Assert.assertEquals(KBaseFeatureValuesServer.SERVICE_VERSION, status.getVersion());
        Assert.assertEquals("OK", status.getStatus());
        assertNotEmpty(status.getStartupTime());
        assertNotEmpty(status.getGiturl());
        assertNotEmpty(status.getBranch());
        assertNotEmpty(status.getCommit());
        assertNotEmpty(status.getDeploymentCfgPath());
        Assert.assertTrue(status.getSafeConfiguration().size() > 0);
    }
    
    private static void assertNotEmpty(String text) throws Exception {
        Assert.assertNotNull(text);
        Assert.assertTrue(text.trim().length() > 0);
    }
    
    @Test
    public void testClusterKMeans() throws Exception {
        WorkspaceClient wscl = getWsClient();
        String exprObjName = "expression1";
        String estimObjName = "estimate1";
        String clustObj1Name = "clusters1";
        String clustObj2Name = "clusters2";
        String clustObj3Name = "clusters3";
        ExpressionMatrix data = new ExpressionMatrix().withType("log-ratio").withScale("1.0")
                .withData(getSampleMatrix());
        wscl.saveObjects(new SaveObjectsParams().withWorkspace(testWsName).withObjects(Arrays.asList(
                new ObjectSaveData().withName(exprObjName).withType("KBaseFeatureValues.ExpressionMatrix")
                .withData(new UObject(data)))));
        /////////////// estimate K /////////////////
        String jobId1 = client.estimateK(new EstimateKParams().withInputMatrix(testWsName + "/" + 
                exprObjName).withOutWorkspace(testWsName).withOutEstimateResult(estimObjName));
        waitForJob(jobId1);
        ObjectData res1 = wscl.getObjects(Arrays.asList(new ObjectIdentity().withWorkspace(testWsName)
                .withName(estimObjName))).get(0);
        EstimateKResult estKRes = res1.getData().asClassInstance(EstimateKResult.class);
        long k = estKRes.getBestK();
        System.out.println("Best K: " + k);
        System.out.println("Cluster count qualities: " + estKRes.getEstimateClusterSizes());
        /////////////// K-means /////////////////
        String jobId2 = client.clusterKMeans(new ClusterKMeansParams().withInputData(testWsName + "/" + 
                exprObjName).withK(k).withOutWorkspace(testWsName).withOutClustersetId(clustObj1Name));
        waitForJob(jobId2);
        ObjectData res2 = wscl.getObjects(Arrays.asList(new ObjectIdentity().withWorkspace(testWsName)
                .withName(clustObj1Name))).get(0);
        ClusterSet clSet2 = res2.getData().asClassInstance(ClusterSet.class);
        System.out.println("K-means: " + clSet2.getFeatureClusters());
        /////////////// Hierarchikal /////////////////
        String jobId3 = client.clusterHierarchical(new ClusterHierarchicalParams().withInputData(testWsName + "/" + 
                exprObjName).withFeatureHeightCutoff(0.5).withOutWorkspace(testWsName).withOutClustersetId(clustObj2Name));
        waitForJob(jobId3);
        ObjectData res3 = wscl.getObjects(Arrays.asList(new ObjectIdentity().withWorkspace(testWsName)
                .withName(clustObj2Name))).get(0);
        ClusterSet clSet3 = res3.getData().asClassInstance(ClusterSet.class);
        System.out.println("Hierarchical: " + clSet3.getFeatureClusters());
        System.out.println("Hierarchical: " + clSet3.getFeatureDendrogram());
        /////////////// From dendrogram /////////////////
        String jobId4 = client.clustersFromDendrogram(new ClustersFromDendrogramParams().withInputData(testWsName + "/" + 
                clustObj2Name).withFeatureHeightCutoff(0.2).withOutWorkspace(testWsName).withOutClustersetId(clustObj3Name));
        waitForJob(jobId4);
        ObjectData res4 = wscl.getObjects(Arrays.asList(new ObjectIdentity().withWorkspace(testWsName)
                .withName(clustObj3Name))).get(0);
        ClusterSet clSet4 = res4.getData().asClassInstance(ClusterSet.class);
        System.out.println("From dendrogram: " + clSet4.getFeatureClusters());        
    }

    private static FloatMatrix2D getSampleMatrix() {
        List<List<Double>> values = new ArrayList<List<Double>>();
        values.add(Arrays.asList(13.0, 2.0, 3.0));
        values.add(Arrays.asList(10.9, 1.95, 2.9));
        values.add(Arrays.asList(2.45, 13.4, 4.4));
        values.add(Arrays.asList(2.5, 11.5, 3.55));
        values.add(Arrays.asList(-1.05, -2.0, -14.0));
        values.add(Arrays.asList(-1.2, -2.25, -13.2));
        values.add(Arrays.asList(-1.1, -2.1, -15.15));
        return new FloatMatrix2D().withValues(values)
                .withRowIds(Arrays.asList("g1", "g2", "g3", "g4", "g5", "g6", "g7"))
                .withColIds(Arrays.asList("c1", "c2", "c3"));
    }
    
    private static WorkspaceClient getWsClient() throws UnauthorizedException, IOException,
            MalformedURLException {
        WorkspaceClient wscl = new WorkspaceClient(new URL(getWsUrl()), token);
        wscl.setAuthAllowedForHttp(true);
        return wscl;
    }

    /*@Test
    public void testBadCall() throws Exception {
        String notNumber = "abc_xyz";
        String jobId = client.runJob(new RunJobParams().withMethod("SmallTest.parseInt").withParams(Arrays.asList(new UObject(notNumber))));
        try {
            waitForJob(client, jobId, new TypeReference<List<Integer>>() {});
            Assert.fail("Method is expected to produce an error");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage(), ex.getMessage().contains("NumberFormatException") &&
                    ex.getMessage().contains(notNumber));
        }
    }*/

    private static String getWsUrl() {
        return systemGetProperty("test." + KBaseFeatureValuesServer.CONFIG_PARAM_WS_URL);
    }

    private static String getUjsUrl() {
        return systemGetProperty("test." + KBaseFeatureValuesServer.CONFIG_PARAM_UJS_URL);
    }
    
    private void waitForJob(String jobId) throws Exception {
        UserAndJobStateClient jscl = new UserAndJobStateClient(new URL(getUjsUrl()), token);
        jscl.setAllSSLCertificatesTrusted(true);
        jscl.setIsInsecureHttpConnectionAllowed(true);
        for (int i = 0; i < fvJobWaitSeconds; i++) {
            Thread.sleep(1000);
            Tuple7<String, String, String, Long, String, Long, Long> data = jscl.getJobStatus(jobId);
            //System.out.println("Job status: " + data);
            Long complete = data.getE6();
            Long wasError = data.getE7();
            if (complete == 1L) {
                if (wasError == 0L) {
                    return;
                } else {
                    String error = jscl.getDetailedError(jobId);
                    throw new IllegalStateException(error);
                }
            }
        }
        throw new IllegalStateException("Job wasn't finished in " + fvJobWaitSeconds + " seconds");
    }

    private static AuthToken getToken() throws AuthException, IOException {
        String user = systemGetProperty("test.user");
        String password = systemGetProperty("test.password");
        AuthToken token = AuthService.login(user, password).getToken();
        return token;
    }
    
    private static int startupMongo(String mongodExePath, File dir) throws Exception {
        if (mongodExePath == null)
            mongodExePath = "mongod";
        if (!dir.exists())
            dir.mkdirs();
        File dataDir = new File(dir, "data");
        dataDir.mkdir();
        File logFile = new File(dir, "mongodb.log");
        int port = findFreePort();
        File configFile = new File(dir, "mongod.conf");
        writeFileLines(Arrays.asList(
                "dbpath=" + dataDir.getAbsolutePath(),
                "logpath=" + logFile.getAbsolutePath(),
                "logappend=true",
                "port=" + port,
                "bind_ip=127.0.0.1"
                ), configFile);
        File scriptFile = new File(dir, "start_mongo.sh");
        writeFileLines(Arrays.asList(
                "#!/bin/bash",
                "cd " + dir.getAbsolutePath(),
                mongodExePath + " --config " + configFile.getAbsolutePath() + " >out.txt 2>err.txt & pid=$!",
                "echo $pid > pid.txt"
                ), scriptFile);
        ProcessHelper.cmd("bash", scriptFile.getCanonicalPath()).exec(dir);
        boolean ready = false;
        for (int n = 0; n < mongoInitWaitSeconds; n++) {
            Thread.sleep(1000);
            if (logFile.exists()) {
                if (grep(readFileLines(logFile), "waiting for connections on port " + port).size() > 0) {
                    ready = true;
                    break;
                }
            }
        }
        if (!ready) {
            if (logFile.exists())
                for (String l : readFileLines(logFile))
                    System.err.println("MongoDB log: " + l);
            throw new IllegalStateException("MongoDB couldn't startup in " + mongoInitWaitSeconds + " seconds");
        }
        System.out.println(dir.getName() + " was started up");
        return port;
    }

    /*private static int startupShock(String shockExePath, File dir, int mongoPort) throws Exception {
        if (shockExePath == null)
            shockExePath = "shock-server";
        if (!dir.exists())
            dir.mkdirs();
        File dataDir = new File(dir, "data");
        dataDir.mkdir();
        File logsDir = new File(dir, "logs");
        logsDir.mkdir();
        File siteDir = new File(dir, "site");
        siteDir.mkdir();
        int port = findFreePort();
        File configFile = new File(dir, "shock.cfg");
        writeFileLines(Arrays.asList(
                "[Address]",
                "api-ip=0.0.0.0",
                "api-port=" + port,
                "[Admin]",
                "email=shock-admin@kbase.us",
                "[Anonymous]",
                "read=true",
                "write=true",
                "create-user=false",
                "[Auth]",
                "globus_token_url=https://nexus.api.globusonline.org/goauth/token?grant_type=client_credentials",
                "globus_profile_url=https://nexus.api.globusonline.org/users",
                "[Mongodb]",
                "hosts=localhost:" + mongoPort,
                "database=ShockDBtest",
                "[Mongodb-Node-Indices]",
                "id=unique:true",
                "[Paths]",
                "data=" + dataDir.getAbsolutePath(),
                "logs=" + logsDir.getAbsolutePath(),
                "site=" + siteDir.getAbsolutePath()
                ), configFile);
        File scriptFile = new File(dir, "start_shock.sh");
        writeFileLines(Arrays.asList(
                "#!/bin/bash",
                "cd " + dir.getAbsolutePath(),
                shockExePath + " --conf " + configFile.getAbsolutePath() + " >out.txt 2>err.txt & pid=$!",
                "echo $pid > pid.txt"
                ), scriptFile);
        ProcessHelper.cmd("bash", scriptFile.getCanonicalPath()).exec(dir);
        Exception err = null;
        int waitSec = 30;
        for (int n = 0; n < waitSec; n++) {
            Thread.sleep(1000);
            try {
                BasicShockClient client = new BasicShockClient(new URL("http://localhost:" + port));
                String jsonData = "{\"key\":\"value\"}";
                InputStream is = new ByteArrayInputStream(jsonData.getBytes());
                ShockNode node = client.addNode(new TreeMap<String, Object>(), is, "test.json", "json");
                is.close();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                client.getFile(node.getId(), os);
                os.close();
                String retJson = new String(os.toByteArray());
                if (jsonData.equals(retJson)) {
                    err = null;
                    break;
                } else {
                    err = new Exception("Shock response doesn't match expected data: " + retJson);
                }
            } catch (Exception ex) {
                err = ex;
            }
        }
        if (err != null) {
            File errorFile = new File(logsDir, "error.log");
            if (errorFile.exists())
                for (String l : readFileLines(errorFile))
                    System.err.println("Shock error: " + l);
            throw new IllegalStateException("Shock couldn't startup in " + waitSec + " seconds (" + err.getMessage() + ")", err);
        }
        System.out.println(dir.getName() + " was started up");
        return port;
    }*/

    private static int startupAweServer(String aweServerExePath, File dir, int mongoPort) throws Exception {
        if (aweServerExePath == null) {
            aweServerExePath = "awe-server";
        }
        if (!dir.exists())
            dir.mkdirs();
        File dataDir = new File(dir, "data");
        dataDir.mkdir();
        File logsDir = new File(dir, "logs");
        logsDir.mkdir();
        File siteDir = new File(dir, "site");
        siteDir.mkdir();
        File awfDir = new File(dir, "awfs");
        awfDir.mkdir();
        int port = findFreePort();
        File configFile = new File(dir, "awe.cfg");
        writeFileLines(Arrays.asList(
                "[Admin]",
                "email=shock-admin@kbase.us",
                "users=",
                "[Anonymous]",
                "read=true",
                "write=true",
                "delete=true",
                "cg_read=false",
                "cg_write=false",
                "cg_delete=false",
                "[Args]",
                "debuglevel=0",
                "[Auth]",
                "globus_token_url=https://nexus.api.globusonline.org/goauth/token?grant_type=client_credentials",
                "globus_profile_url=https://nexus.api.globusonline.org/users",
                "client_auth_required=false",
                "[Directories]",
                "data=" + dataDir.getAbsolutePath(),
                "logs=" + logsDir.getAbsolutePath(),
                "site=" + siteDir.getAbsolutePath(),
                "awf=" + awfDir.getAbsolutePath(),
                "[Mongodb]",
                "hosts=localhost:" + mongoPort,
                "database=AWEDB",
                "[Mongodb-Node-Indices]",
                "id=unique:true",
                "[Ports]",
                "site-port=" + findFreePort(),
                "api-port=" + port
                ), configFile);
        File scriptFile = new File(dir, "start_awe_server.sh");
        writeFileLines(Arrays.asList(
                "#!/bin/bash",
                "cd " + dir.getAbsolutePath(),
                aweServerExePath + " --conf " + configFile.getAbsolutePath() + " >out.txt 2>err.txt & pid=$!",
                "echo $pid > pid.txt"
                ), scriptFile);
        ProcessHelper.cmd("bash", scriptFile.getCanonicalPath()).exec(dir);
        Exception err = null;
        for (int n = 0; n < aweServerInitWaitSeconds; n++) {
            Thread.sleep(1000);
            try {
                InputStream is = new URL("http://localhost:" + port + "/job/").openStream();
                ObjectMapper mapper = new ObjectMapper();
                @SuppressWarnings("unchecked")
                Map<String, Object> ret = mapper.readValue(is, Map.class);
                if (ret.containsKey("limit") && ret.containsKey("total_count")) {
                    err = null;
                    break;
                } else {
                    err = new Exception("AWE server response doesn't match expected data: " + 
                            mapper.writeValueAsString(ret));
                }
            } catch (Exception ex) {
                err = ex;
            }
        }
        if (err != null) {
            File errorFile = new File(new File(logsDir, "server"), "error.log");
            if (errorFile.exists())
                for (String l : readFileLines(errorFile))
                    System.err.println("AWE server error: " + l);
            throw new IllegalStateException("AWE server couldn't startup in " + aweServerInitWaitSeconds + 
                    " seconds (" + err.getMessage() + ")", err);
        }
        System.out.println(dir.getName() + " was started up");
        return port;
    }

    @SuppressWarnings("unchecked")
    private static int startupAweClient(String aweClientExePath, File dir, int aweServerPort, 
            File binDir) throws Exception {
        if (aweClientExePath == null) {
            aweClientExePath = "awe-client";
        }
        if (!dir.exists())
            dir.mkdirs();
        File dataDir = new File(dir, "data");
        dataDir.mkdir();
        File logsDir = new File(dir, "logs");
        logsDir.mkdir();
        File workDir = new File(dir, "work");
        workDir.mkdir();
        int port = findFreePort();
        File configFile = new File(dir, "awec.cfg");
        writeFileLines(Arrays.asList(
                "[Directories]",
                "data=" + dataDir.getAbsolutePath(),
                "logs=" + logsDir.getAbsolutePath(),
                "[Args]",
                "debuglevel=0",
                "[Client]",
                "workpath=" + workDir.getAbsolutePath(),
                "supported_apps=" + KBaseFeatureValuesServer.AWE_CLIENT_SCRIPT_NAME,
                "serverurl=http://localhost:" + aweServerPort + "/",
                "group=kbase",
                "name=kbase-client",
                "auto_clean_dir=false",
                "worker_overlap=false",
                "print_app_msg=true",
                "clientgroup_token=",
                "pre_work_script=",
                "pre_work_script_args="
                ), configFile);
        File scriptFile = new File(dir, "start_awe_client.sh");
        writeFileLines(Arrays.asList(
                "#!/bin/bash",
                "cd " + dir.getAbsolutePath(),
                "export PATH=" + binDir.getAbsolutePath() + ":$PATH",
                aweClientExePath + " --conf " + configFile.getAbsolutePath() + " >out.txt 2>err.txt & pid=$!",
                "echo $pid > pid.txt"
                ), scriptFile);
        ProcessHelper.cmd("bash", scriptFile.getCanonicalPath()).exec(dir);
        Exception err = null;
        int waitSec = 30;
        for (int n = 0; n < waitSec; n++) {
            Thread.sleep(1000);
            try {
                InputStream is = new URL("http://localhost:" + aweServerPort + "/client/").openStream();
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> ret = mapper.readValue(is, Map.class);
                if (ret.containsKey("data") && 
                        ((List<Object>)ret.get("data")).size() > 0) {
                    err = null;
                    break;
                } else {
                    err = new Exception("AWE client response doesn't match expected data: " + 
                            mapper.writeValueAsString(ret));
                }
            } catch (Exception ex) {
                err = ex;
            }
        }
        if (err != null) {
            File errorFile = new File(new File(logsDir, "client"), "error.log");
            if (errorFile.exists())
                for (String l : readFileLines(errorFile))
                    System.err.println("AWE client error: " + l);
            throw new IllegalStateException("AWE client couldn't startup in " + waitSec + 
                    " seconds (" + err.getMessage() + ")", err);
        }
        System.out.println(dir.getName() + " was started up");
        return port;
    }

    private static Server startupFVService(File dir, File binDir, int awePort) throws Exception {
        Log.setLog(new Logger() {
            @Override
            public void warn(String arg0, Object arg1, Object arg2) {}
            @Override
            public void warn(String arg0, Throwable arg1) {}
            @Override
            public void warn(String arg0) {}
            @Override
            public void setDebugEnabled(boolean arg0) {}
            @Override
            public boolean isDebugEnabled() {
                return false;
            }
            @Override
            public void info(String arg0, Object arg1, Object arg2) {}
            @Override
            public void info(String arg0) {}
            @Override
            public String getName() {
                return null;
            }
            @Override
            public Logger getLogger(String arg0) {
                return this;
            }
            @Override
            public void debug(String arg0, Object arg1, Object arg2) {}
            @Override
            public void debug(String arg0, Throwable arg1) {}
            @Override
            public void debug(String arg0) {}
        });
        if (!dir.exists())
            dir.mkdirs();
        File configFile = new File(dir, "deploy.cfg");
        int port = findFreePort();
        writeFileLines(Arrays.asList(
                "[" + KBaseFeatureValuesServer.SERVICE_NAME + "]",
                KBaseFeatureValuesServer.CONFIG_PARAM_SCRATCH + "=" + dir.getAbsolutePath(),
                KBaseFeatureValuesServer.CONFIG_PARAM_UJS_URL + "=" + getUjsUrl(),
                KBaseFeatureValuesServer.CONFIG_PARAM_SHOCK_URL + "=" + 
                        systemGetProperty("test." + KBaseFeatureValuesServer.CONFIG_PARAM_SHOCK_URL),
                KBaseFeatureValuesServer.CONFIG_PARAM_AWE_URL + "=http://localhost:" + awePort + "/",
                KBaseFeatureValuesServer.CONFIG_PARAM_CLIENT_BIN_DIR + "=" + binDir.getAbsolutePath(),
                KBaseFeatureValuesServer.CONFIG_PARAM_WS_URL + "=" + getWsUrl()
                ), configFile);
        /*File jobServiceCLI = new File(dir, KBaseFeatureValuesServer.AWE_CLIENT_SCRIPT_NAME);
        writeFileLines(Arrays.asList(
                "#!/bin/bash",
                "java -cp " + System.getProperty("java.class.path") + 
                    " us.kbase.kbasefeaturevalues.KBaseFeatureValuesScript $1"
                ), jobServiceCLI);
        ProcessHelper.cmd("chmod", "a+x", jobServiceCLI.getAbsolutePath()).exec(dir);*/
        System.setProperty("KB_DEPLOYMENT_CONFIG", configFile.getAbsolutePath());
        Server jettyServer = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        jettyServer.setHandler(context);
        context.addServlet(new ServletHolder(new KBaseFeatureValuesServer()),"/*");
        jettyServer.start();
        Exception err = null;
        JsonClientCaller caller = new JsonClientCaller(new URL("http://localhost:" + port + "/"));
        int waitSec = 30;
        for (int n = 0; n < waitSec; n++) {
            Thread.sleep(1000);
            try {
                caller.jsonrpcCall("Unknown", new ArrayList<String>(), null, false, false);
            } catch (ServerException ex) {
                if (ex.getMessage().contains("Can not find method [Unknown] in server class")) {
                    err = null;
                    break;
                } else {
                    err = ex;
                }
            } catch (Exception ex) {
                err = ex;
            }
        }
        if (err != null)
            throw new IllegalStateException("KBaseFeatureValues service couldn't startup in " + 
                    waitSec + " seconds (" + err.getMessage() + ")", err);
        System.out.println(dir.getName() + " was started up");
        return jettyServer;
    }

    private static void killPid(File dir) {
        if (dir == null)
            return;
        try {
            File pidFile = new File(dir, "pid.txt");
            if (pidFile.exists()) {
                String pid = readFileLines(pidFile).get(0).trim();
                ProcessHelper.cmd("kill", pid).exec(dir);
                System.out.println(dir.getName() + " was stopped");
            }
        } catch (Exception ignore) {}
    }
    
    private static void writeFileLines(List<String> lines, File targetFile) throws IOException {
        PrintWriter pw = new PrintWriter(targetFile);
        for (String l : lines)
            pw.println(l);
        pw.close();
    }

    private static List<String> readFileLines(File f) throws IOException {
        List<String> ret = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(f));
        while (true) {
            String l = br.readLine();
            if (l == null)
                break;
            ret.add(l);
        }
        br.close();
        return ret;
    }

    private static List<String> grep(List<String> lines, String substring) {
        List<String> ret = new ArrayList<String>();
        for (String l : lines)
            if (l.contains(substring))
                ret.add(l);
        return ret;
    }
    
    private static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {}
        throw new IllegalStateException("Can not find available port in system");
    }
    
    private static String systemGetProperty(String name) {
        String ret = System.getProperty(name);
        if (ret == null)
            throw new IllegalStateException("Parameter " + name + " is not defined " +
            		"in test configuration");
        return ret;
    }
    
    private static File prepareWorkDir(String testName) throws IOException {
        File tempDir = new File(systemGetProperty("test.temp-dir")).getCanonicalFile();
        if (!tempDir.exists())
            tempDir.mkdirs();
        for (File dir : tempDir.listFiles()) {
            if (dir.isDirectory() && dir.getName().startsWith("test_" + testName + "_"))
                try {
                    deleteRecursively(dir);
                } catch (Exception e) {
                    System.out.println("Can not delete directory [" + dir.getName() + "]: " + e.getMessage());
                }
        }
        File workDir = new File(tempDir, "test_" + testName + "_" + System.currentTimeMillis());
        if (!workDir.exists())
            workDir.mkdir();
        return workDir;
    }
    
    private static void deleteRecursively(File fileOrDir) {
        if (fileOrDir.isDirectory() && !Files.isSymbolicLink(fileOrDir.toPath()))
            for (File f : fileOrDir.listFiles()) 
                deleteRecursively(f);
        fileOrDir.delete();
    }
}
