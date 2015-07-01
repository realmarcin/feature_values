package us.kbase.clusterservice.test;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import us.kbase.clusterservice.ClusterFloatRowsKmeansParams;
import us.kbase.clusterservice.ClusterServicePyLocalClient;
import us.kbase.clusterservice.ClusterServiceRLocalClient;
import us.kbase.common.service.ServerException;
import us.kbase.kbasefeaturevalues.FloatMatrix2D;

public class ClusterServiceTest {
    private File rootTempDir = null;

    @Test
    public void pyTest() throws Exception {
        File workDir = generateTempDir(rootTempDir, "py_clusters_1_", "");
        workDir.mkdirs();
        ClusterServicePyLocalClient cl = new ClusterServicePyLocalClient(workDir);
        cl.setBinDir(new File("bin"));
        try {
            List<Long> clusterLabels = cl.clusterFloatRowsKmeans(
                    new ClusterFloatRowsKmeansParams().withInputData(
                            getSampleMatrix()).withK(3L)).getClusterLabels();
            System.out.println(clusterLabels);
            checkClusterLabels(clusterLabels);
        } catch (ServerException ex) {
            System.out.println(ex.getData());
            throw ex;
        }
    }

    @Test
    public void rTest() throws Exception {
        File workDir = generateTempDir(rootTempDir, "r_clusters_1_", "");
        workDir.mkdirs();
        ClusterServiceRLocalClient cl = new ClusterServiceRLocalClient(workDir);
        cl.setBinDir(new File("bin"));
        try {
            List<Long> clusterLabels = cl.clusterFloatRowsKmeans(
                    new ClusterFloatRowsKmeansParams().withInputData(
                            getSampleMatrix()).withK(3L)).getClusterLabels();
            System.out.println(clusterLabels);
            checkClusterLabels(clusterLabels);
        } catch (ServerException ex) {
            System.out.println(ex.getData());
            throw ex;
        }
    }

    private static void checkClusterLabels(List<Long> labels) throws Exception {
        String errMsg = "Unexpected labels: " + labels;
        Assert.assertEquals(errMsg, 7, labels.size());
        long c1 = labels.get(0);
        Assert.assertEquals(errMsg, c1, (long)labels.get(1));
        long c2 = labels.get(2);
        Assert.assertEquals(errMsg, c2, (long)labels.get(3));
        long c3 = labels.get(4);
        Assert.assertEquals(errMsg, c3, (long)labels.get(5));
        Assert.assertEquals(errMsg, c3, (long)labels.get(6));
    }
    
    private static FloatMatrix2D getSampleMatrix() {
        List<List<Double>> values = new ArrayList<List<Double>>();
        values.add(Arrays.asList(1.0, 2.0, 3.0));
        values.add(Arrays.asList(0.9, 1.9, 2.9));
        values.add(Arrays.asList(1.4, 2.4, 3.4));
        values.add(Arrays.asList(1.5, 2.5, 3.5));
        values.add(Arrays.asList(-1.0, -2.0, -3.0));
        values.add(Arrays.asList(-1.2, -2.2, -3.2));
        values.add(Arrays.asList(-1.1, -2.1, -3.1));
        return new FloatMatrix2D().withValues(values)
                .withRowIds(Arrays.asList("g1", "g2", "g3", "g4", "g5", "g6", "g7"))
                .withColIds(Arrays.asList("c1", "c2", "c3"));
    }
    
    @Before
    public void prepare() throws Exception {
        rootTempDir = new File(getProp("test.temp-dir"));
        //token = AuthService.login(getProp("test.user"), getProp("test.password")).getToken();
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
