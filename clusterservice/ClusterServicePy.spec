#include<../KBaseFeatureValues.spec>

module ClusterServicePy {

    /*
        clusters - set of lists consisting of positions of rows from original
            array.
    */
    typedef structure {
        KBaseFeatureValues.AnalysisReport report;
        list<int> cluster_labels;
    } ClusterResults;

    /*
        Clusters features by K-means clustering.
    */
    funcdef cluster_k_means(KBaseFeatureValues.FloatMatrix2D matrix,
         int k) returns (ClusterResults);

    /*
        Used as an analysis step before generating clusters using K-means 
        clustering, this method provides an estimate of K by [...]
    */
    funcdef estimate_k(KBaseFeatureValues.FloatMatrix2D matrix) 
        returns (int cluster_number);

    /*
        Clusters features by hierarchical clustering.
    */
    funcdef cluster_hierarchical(KBaseFeatureValues.FloatMatrix2D matrix, 
        string distance_metric, string linkage_criteria,
        float feature_height_cutoff, float condition_height_cutoff)
        returns (ClusterResults);

    /*
        Given a ClusterSet with a dendogram built from a hierarchical clustering
        method, this function creates new clusters by cutting the dendogram at
        a specific hieght or by some other approach.
    */
    funcdef clusters_from_dendrogram(float feature_height_cutoff,
        float condition_height_cutoff) returns (ClusterResults);

};
