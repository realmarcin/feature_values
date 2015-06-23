package us.kbase.clusterservice.test;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import us.kbase.auth.AuthService;
import us.kbase.auth.AuthToken;
import us.kbase.clusterservice.ClusterFloatRowsScikitKmeansParams;
import us.kbase.clusterservice.ClusterServiceLocalClient;

public class ClusterServiceTest {
    private File rootTempDir = null;
    private AuthToken token = null;

    @Test
    public void smallTest() throws Exception {
        File workDir = generateTempDir(rootTempDir, "cluster_", "");
        workDir.mkdirs();
        ClusterServiceLocalClient cl = new ClusterServiceLocalClient(workDir, token);
        cl.setBinDir(new File("bin"));
        List<List<Double>> values = new ArrayList<List<Double>>();
        values.add(Arrays.asList(1.0, 2.0, 3.0));
        values.add(Arrays.asList(1.2, 2.2, 3.2));
        values.add(Arrays.asList(1.1, 2.1, 3.1));
        values.add(Arrays.asList(0.9, 1.9, 2.9));
        values.add(Arrays.asList(-1.0, -2.0, -3.0));
        values.add(Arrays.asList(-1.2, -2.2, -3.2));
        values.add(Arrays.asList(-1.1, -2.1, -3.1));
        List<Long> clusterLabels = cl.clusterFloatRowsScikitKmeans(
                new ClusterFloatRowsScikitKmeansParams().withValues(values)).getClusterLabels();
        System.out.println(clusterLabels);
    }
    
    @Before
    public void prepare() throws Exception {
        rootTempDir = new File(getProp("test.temp-dir"));
        token = AuthService.login(getProp("test.user"), getProp("test.password")).getToken();
        cleanup();
    }

    public String getProp(String propName) {
        String ret = System.getProperty(propName);
        if (ret == null)
            throw new IllegalStateException("Java system property " + propName + " is not defined");
        return ret;
    }
    
    @After
    public void cleanup() throws Exception {
        if (rootTempDir != null && rootTempDir.exists())
            deleteRecursively(rootTempDir);
    }
    
    private static File generateTempDir(File parentTempDir, String prefix, String suffix) {
        long start = System.currentTimeMillis();
        while (true) {
            File dir = new File(parentTempDir, prefix + start + suffix);
            if (!dir.exists()) {
                dir.mkdirs();
                return dir;
            }
            start++;
        }
    }

    private static void deleteRecursively(File fileOrDir) {
        if (fileOrDir.isDirectory() && !Files.isSymbolicLink(fileOrDir.toPath()))
            for (File f : fileOrDir.listFiles()) 
                deleteRecursively(f);
        fileOrDir.delete();
    }

}
