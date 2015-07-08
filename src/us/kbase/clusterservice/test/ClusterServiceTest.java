package us.kbase.clusterservice.test;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import us.kbase.clusterservice.ClusterResults;
import us.kbase.clusterservice.ClusterServicePyLocalClient;
import us.kbase.clusterservice.ClusterServiceRLocalClient;
import us.kbase.common.service.ServerException;
import us.kbase.common.service.UObject;
import us.kbase.kbasefeaturevalues.FloatMatrix2D;

public class ClusterServiceTest {
    private static File rootTempDir = null;

    @Ignore
    @Test
    public void pyTest() throws Exception {
        File workDir = generateTempDir(rootTempDir, "test_clusterservice_py1_", "");
        workDir.mkdirs();
        ClusterServicePyLocalClient cl = new ClusterServicePyLocalClient(workDir);
        cl.setBinDir(new File("bin"));
        try {
            List<Long> clusterLabels = cl.clusterKMeans(
                    getSampleMatrix(), 3L).getClusterLabels();
            System.out.println(clusterLabels);
            checkClusterLabels(clusterLabels);
        } catch (ServerException ex) {
            System.out.println(ex.getData());
            throw ex;
        }
    }

    @Test
    public void rTest() throws Exception {
        File workDir = generateTempDir(rootTempDir, "test_clusterservice_r1_", "");
        workDir.mkdirs();
        ClusterServiceRLocalClient cl = new ClusterServiceRLocalClient(workDir);
        cl.setBinDir(new File("bin"));
        FloatMatrix2D matrix = getSampleMatrix();
        try {
            long k = cl.estimateK(matrix);
            System.out.println("Estimated K: " + k);
            Assert.assertEquals(3, k);
            List<Long> clusterLabels = cl.clusterKMeans(matrix, k).getClusterLabels();
            System.out.println(clusterLabels);
            checkClusterLabels(clusterLabels);
            ClusterResults clRes = cl.clusterHierarchical(matrix, "", "", 0.5, 1L);
            String dendrogram = clRes.getDendrogram();
            System.out.println(dendrogram);
            List<Long> clusterLabels2 = cl.clustersFromDendrogram(dendrogram, 0.2).getClusterLabels();
            System.out.println(clusterLabels2);
            checkClusterLabels(clusterLabels2);
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
        values.add(Arrays.asList(13.0, 2.0, 3.0));
        values.add(Arrays.asList(10.9, 1.95, 2.9));
        values.add(Arrays.asList(2.45, 13.4, 4.4));
        values.add(Arrays.asList(2.5, 11.5, 3.55));
        values.add(Arrays.asList(-1.05, -2.0, -14.0));
        values.add(Arrays.asList(-1.2, -2.25, -13.2));
        values.add(Arrays.asList(-1.1, -2.1, -15.15));
        return new FloatMatrix2D().withValues(values)
                .withRowIds(Arrays.asList("g:1", "g,2", "g-3", "g(4", "g)5", "g;6", "g\"7"))
                .withColIds(Arrays.asList("c1", "c2", "c3"));
    }

    @BeforeClass
    public static void prepare() throws Exception {
        rootTempDir = new File(getProp("test.temp-dir"));
        if (!rootTempDir.exists())
            rootTempDir.mkdirs();
        //token = AuthService.login(getProp("test.user"), getProp("test.password")).getToken();
        //cleanup();
        System.out.println("test.temp-dir: " + rootTempDir);
        for (File dir : rootTempDir.listFiles()) {
            if (dir.isDirectory() && dir.getName().startsWith("test_clusterservice_"))
                try {
                    deleteRecursively(dir);
                } catch (Exception e) {
                    System.out.println("Can not delete directory [" + dir.getName() + "]: " + e.getMessage());
                }
        }
    }

    private static String getProp(String propName) {
        String ret = System.getProperty(propName);
        if (ret == null)
            throw new IllegalStateException("Java system property " + propName + " is not defined");
        return ret;
    }

    @After
    public void cleanup() throws Exception {
        //if (rootTempDir != null && rootTempDir.exists())
        //    deleteRecursively(rootTempDir);
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

    public static void main(String[] args) throws Exception {
        //String json = "{\"type\":\"list\",\"attributes\":{\"names\":{\"type\":\"character\",\"attributes\":{},\"value\":[\"merge\",\"height\",\"order\",\"labels\",\"method\",\"call\",\"dist.method\"]},\"class\":{\"type\":\"character\",\"attributes\":{},\"value\":[\"hclust\"]}},\"value\":[{\"type\":\"integer\",\"attributes\":{\"dim\":{\"type\":\"integer\",\"attributes\":{},\"value\":[6,2]}},\"value\":[-1,-4,-5,-7,-3,4,-2,1,-6,3,2,5]},{\"type\":\"double\",\"attributes\":{},\"value\":[0,0,0,0,1.11022302e-16,2]},{\"type\":\"integer\",\"attributes\":{},\"value\":[7,5,6,3,4,1,2]},{\"type\":\"character\",\"attributes\":{},\"value\":[\"g1\",\"g2\",\"g3\",\"g4\",\"g5\",\"g6\",\"g7\"]},{\"type\":\"character\",\"attributes\":{},\"value\":[\"complete\"]},{\"type\":\"language\",\"attributes\":{},\"value\":[\"hcluster(x = cor_mat, method = \\\"correlation\\\")\"]},{\"type\":\"character\",\"attributes\":{},\"value\":[\"correlation\"]}]}";
        String json = "{\"type\":\"list\",\"attributes\":{\"members\":{\"type\":\"integer\",\"attributes\":{},\"value\":[7]},\"midpoint\":{\"type\":\"double\",\"attributes\":{},\"value\":[2.8125]},\"height\":{\"type\":\"double\",\"attributes\":{},\"value\":[2]},\"class\":{\"type\":\"character\",\"attributes\":{},\"value\":[\"dendrogram\"]}},\"value\":[{\"type\":\"list\",\"attributes\":{\"members\":{\"type\":\"integer\",\"attributes\":{},\"value\":[4]},\"midpoint\":{\"type\":\"double\",\"attributes\":{},\"value\":[0.875]},\"height\":{\"type\":\"double\",\"attributes\":{},\"value\":[3.4779149e-07]}},\"value\":[{\"type\":\"integer\",\"attributes\":{\"members\":{\"type\":\"integer\",\"attributes\":{},\"value\":[1]},\"height\":{\"type\":\"double\",\"attributes\":{},\"value\":[0]},\"label\":{\"type\":\"character\",\"attributes\":{},\"value\":[\"g2\"]},\"leaf\":{\"type\":\"logical\",\"attributes\":{},\"value\":[true]}},\"value\":[2]},{\"type\":\"list\",\"attributes\":{\"members\":{\"type\":\"integer\",\"attributes\":{},\"value\":[3]},\"midpoint\":{\"type\":\"double\",\"attributes\":{},\"value\":[0.75]},\"height\":{\"type\":\"double\",\"attributes\":{},\"value\":[3.99645309e-08]}},\"value\":[{\"type\":\"integer\",\"attributes\":{\"members\":{\"type\":\"integer\",\"attributes\":{},\"value\":[1]},\"height\":{\"type\":\"double\",\"attributes\":{},\"value\":[0]},\"label\":{\"type\":\"character\",\"attributes\":{},\"value\":[\"g1\"]},\"leaf\":{\"type\":\"logical\",\"attributes\":{},\"value\":[true]}},\"value\":[1]},{\"type\":\"list\",\"attributes\":{\"members\":{\"type\":\"integer\",\"attributes\":{},\"value\":[2]},\"midpoint\":{\"type\":\"double\",\"attributes\":{},\"value\":[0.5]},\"height\":{\"type\":\"double\",\"attributes\":{},\"value\":[9.50970414e-11]}},\"value\":[{\"type\":\"integer\",\"attributes\":{\"label\":{\"type\":\"character\",\"attributes\":{},\"value\":[\"g3\"]},\"members\":{\"type\":\"integer\",\"attributes\":{},\"value\":[1]},\"height\":{\"type\":\"double\",\"attributes\":{},\"value\":[0]},\"leaf\":{\"type\":\"logical\",\"attributes\":{},\"value\":[true]}},\"value\":[3]},{\"type\":\"integer\",\"attributes\":{\"label\":{\"type\":\"character\",\"attributes\":{},\"value\":[\"g4\"]},\"members\":{\"type\":\"integer\",\"attributes\":{},\"value\":[1]},\"height\":{\"type\":\"double\",\"attributes\":{},\"value\":[0]},\"leaf\":{\"type\":\"logical\",\"attributes\":{},\"value\":[true]}},\"value\":[4]}]}]}]},{\"type\":\"list\",\"attributes\":{\"members\":{\"type\":\"integer\",\"attributes\":{},\"value\":[3]},\"midpoint\":{\"type\":\"double\",\"attributes\":{},\"value\":[0.75]},\"height\":{\"type\":\"double\",\"attributes\":{},\"value\":[3.4779149e-07]}},\"value\":[{\"type\":\"integer\",\"attributes\":{\"members\":{\"type\":\"integer\",\"attributes\":{},\"value\":[1]},\"height\":{\"type\":\"double\",\"attributes\":{},\"value\":[0]},\"label\":{\"type\":\"character\",\"attributes\":{},\"value\":[\"g6\"]},\"leaf\":{\"type\":\"logical\",\"attributes\":{},\"value\":[true]}},\"value\":[6]},{\"type\":\"list\",\"attributes\":{\"members\":{\"type\":\"integer\",\"attributes\":{},\"value\":[2]},\"midpoint\":{\"type\":\"double\",\"attributes\":{},\"value\":[0.5]},\"height\":{\"type\":\"double\",\"attributes\":{},\"value\":[9.50970414e-11]}},\"value\":[{\"type\":\"integer\",\"attributes\":{\"label\":{\"type\":\"character\",\"attributes\":{},\"value\":[\"g5\"]},\"members\":{\"type\":\"integer\",\"attributes\":{},\"value\":[1]},\"height\":{\"type\":\"double\",\"attributes\":{},\"value\":[0]},\"leaf\":{\"type\":\"logical\",\"attributes\":{},\"value\":[true]}},\"value\":[5]},{\"type\":\"integer\",\"attributes\":{\"label\":{\"type\":\"character\",\"attributes\":{},\"value\":[\"g7\"]},\"members\":{\"type\":\"integer\",\"attributes\":{},\"value\":[1]},\"height\":{\"type\":\"double\",\"attributes\":{},\"value\":[0]},\"leaf\":{\"type\":\"logical\",\"attributes\":{},\"value\":[true]}},\"value\":[7]}]}]}]}";
        System.out.println(UObject.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(UObject.getMapper().readValue(json, Map.class)));
    }
}


