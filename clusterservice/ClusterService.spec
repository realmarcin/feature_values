module ClusterService {

    /*
        values - two dimensional array values[row][col], where first index 
            (row) goes vertically over outer list and (col) goes horizontally 
            along each each internal list.
    */
    typedef structure {
        list<list<float>> values;
    } ClusterFloatRowsScikitKmeansParams;

    /*
        clusters - set of lists consisting of positions of rows from original
            array.
    */
    typedef structure {
        list<string> messages;
        list<string> warnings;
        list<string> errors;
        list<int> cluster_labels;
    } ClusterResults;

    async funcdef cluster_float_rows_scikit_kmeans(
        ClusterFloatRowsScikitKmeansParams params) returns (ClusterResults)
        authentication required;

};
