#include<../KBaseFeatureValues.spec>

module ClusterService {

    /*
        values - two dimensional array values[row][col], where first index 
            (row) goes vertically over outer list and (col) goes horizontally 
            along each each internal list.
    */
    typedef structure {
        int k;
        KBaseFeatureValues.FloatMatrix2D input_data;
    } ClusterFloatRowsScikitKmeansParams;

    /*
        clusters - set of lists consisting of positions of rows from original
            array.
    */
    typedef structure {
        KBaseFeatureValues.AnalysisReport report;
        list<int> cluster_labels;
    } ClusterResults;

    funcdef cluster_float_rows_scikit_kmeans(
        ClusterFloatRowsScikitKmeansParams params) returns (ClusterResults);

};
