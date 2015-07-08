

/*

The KBaseFeatureValues set of data types and service provides a mechanism for
representing numeric values associated with genome features and conditions, together
with some basic operations on this data.  Essentially, the data is stored as a simple
2D matrix of floating point numbers.  Currently, this is exposed as support for
expression data and single gene knockout fitness data.  (Fitness data being growth
rate relative to WT growth with the specified single gene knockout in a specified
condition).

The operations supported on this data is simple clustering of genes and clustering 
related tools.

*/
module KBaseFeatureValues {

    /* 
        The workspace ID for a Genome data object.
        @id ws KBaseGenomes.Genome
    */
    typedef string ws_genome_id;

    /* 
        The workspace ID for a ConditionSet data object (Note: ConditionSet objects
        do not yet exist - this is for now used as a placeholder).
        @id ws KBaseExperiments.ConditionSet
    */
    typedef string ws_conditionset_id;

    /*
        A simple 2D matrix of floating point numbers with labels/ids for rows and
        columns.  The matrix is stored as a list of lists, with the outer list
        containing rows, and the inner lists containing values for each column of
        that row.  Row/Col ids should be unique.

        row_ids - unique ids for rows.
        col_ids - unique ids for columns.
        values - two dimensional array indexed as: values[row][col]
        @metadata ws length(row_ids) as n_rows
        @metadata ws length(col_ids) as n_cols
    */
    typedef structure {
        list<string> row_ids;
        list<string> col_ids;
        list<list<float>> values;
    } FloatMatrix2D;

    /*
        Indicates true or false values, false = 0, true = 1
        @range [0,1]
    */
    typedef int boolean;

    /*
        A basic report object used for a variety of cases to mark informational
        messages, warnings, and errors related to processing or quality control
        checks of raw data.
    */
    typedef structure {
        string checkTypeDetected;
        string checkUsed;
        list<string> checkDescriptions;
        list<boolean> checkResults;
        list<string> messages;
        list<string> warnings;
        list<string> errors;
    } AnalysisReport;

    /*
        A wrapper around a FloatMatrix2D designed for simple matricies of Expression
        data.  Rows map to features, and columns map to conditions.  The data type 
        includes some information about normalization factors and contains
        mappings from row ids to features and col ids to conditions.

        description - short optional description of the dataset
        type - ? level, ratio, log-ratio
        scale - ? probably: raw, ln, log2, log10
        col_normalization - mean_center, median_center, mode_center, zscore
        row_normalization - mean_center, median_center, mode_center, zscore
        feature_mapping - map from row_id to feature id in the genome
        data - contains values for (feature,condition) pairs, where 
            features correspond to rows and conditions are columns
            (ie data.values[feature][condition])

        @optional description row_normalization col_normalization
        @optional genome_ref feature_mapping conditionset_ref condition_mapping report

        @metadata ws type
        @metadata ws scale
        @metadata ws row_normalization
        @metadata ws col_normalization
        @metadata ws genome_ref as Genome
        @metadata ws conditionset_ref as ConditionSet
        @metadata ws length(data.row_ids) as feature_count
        @metadata ws length(data.col_ids) as condition_count
    */
    typedef structure {

        string description;

        string type;
        string scale;
        string row_normalization;
        string col_normalization;

        ws_genome_id genome_ref;
        mapping<string, string> feature_mapping;

        ws_conditionset_id conditionset_ref;
        mapping<string, string> condition_mapping;

        FloatMatrix2D data;
        AnalysisReport report;
    } ExpressionMatrix;

    /*
        A wrapper around a FloatMatrix2D designed for simple matricies of Fitness data
        for single gene/feature knockouts.  Generally fitness is measured as growth rate
        for the knockout strain relative to wildtype.  This data type only supports
        single feature knockouts.

        description - short optional description of the dataset
        type - ? level, ratio, log-ratio
        scale - ? probably: raw, ln, log2, log10
        col_normalization - mean_center, median_center, mode_center, zscore
        row_normalization - mean_center, median_center, mode_center, zscore
        feature_mapping - map from row_id to feature id in the genome
        data - contains values for (feature,condition) pairs, where 
            features correspond to rows and conditions are columns
            (ie data.values[feature][condition])

        @optional description row_normalization col_normalization
        @optional genome_ref feature_ko_mapping conditionset_ref condition_mapping report

        @metadata ws type
        @metadata ws scale
        @metadata ws row_normalization
        @metadata ws col_normalization
        @metadata ws genome_ref as Genome
        @metadata ws length(data.row_ids) as feature_count
        @metadata ws length(data.col_ids) as condition_count

    */
    typedef structure {
        string description;

        string type;
        string scale;
        string row_normalization;
        string col_normalization;

        ws_genome_id genome_ref;
        mapping<string, string> feature_ko_mapping;

        ws_conditionset_id conditionset_ref;
        mapping<string, string> condition_mapping;

        FloatMatrix2D data;
        AnalysisReport report;
    } SingleKnockoutFitnessMatrix;

    /* 
        A workspace ID that references a Float2DMatrix wrapper data object.
        @id ws KBaseFeatureValues.ExpressionMatrix KBaseFeatureValues.SingleKnockoutFitnessMatrix
    */
    typedef string ws_matrix_id;

    /*
        Simple representation of a cluster, which maps features/conditions of the cluster to the
        row/col index in the data (0-based index).  The index is useful for fast lookup of data
        for a specified feature/condition in the cluster.
    */
    typedef mapping<string, int> labeled_cluster;

    /*
        A set of clusters, typically generated for a Float2DMatrix wrapper, such as Expression
        data or single feature knockout fitness data.

        feature_clusters - list of labeled feature clusters
        condition_clusters - (optional) list of labeled condition clusters
        feature_dendrogram - (optional) maybe output from hierchical clustering approaches
        condition_dendogram - (optional) maybe output from hierchical clustering approaches
        original_data - pointer to the original data used to make this cluster set
        report - information collected during cluster construction.

        @metadata ws original_data as source_data_ref
        @metadata ws length(feature_clusters) as n_feature_clusters
        @metadata ws length(condition_clusters) as n_condition_clusters
        @optional condition_clusters 
        @optional feature_dendrogram condition_dendrogram
        @optional original_data report
    */
    typedef structure {
        list<labeled_cluster> feature_clusters;
        list<labeled_cluster> condition_clusters;
        string feature_dendrogram;
        string condition_dendrogram;
        ws_matrix_id original_data;
        AnalysisReport report;
    } ClusterSet;


    /* 
        The workspace ID of a ClusterSet data object.
        @id ws KBaseFeatureValues.ExpressionMatrix KBaseFeatureValues.SingleKnockoutFitnessMatrix
    */
    typedef string ws_clusterset_id;


    /* note: this needs review from Marcin */
    typedef structure {
        int best_k;
        mapping <int,float> estimate_cluster_sizes;
    } EstimateKResult;

    typedef structure {
        ws_matrix_id input_matrix;
        string out_workspace;
        string out_estimate_result;
    } EstimateKParams;

    /*
        Used as an analysis step before generating clusters using K-means clustering, this method
        provides an estimate of K by [...]
    */
    funcdef estimate_k(EstimateKParams params)
        returns (string job_id) authentication required;



    typedef structure {
        int k;
        ws_matrix_id input_data;
        string out_workspace;
        string out_clusterset_id;
    } ClusterKMeansParams;

    /*
        Clusters features by K-means clustering.
    */
    funcdef cluster_k_means(ClusterKMeansParams params)
        returns (string job_id) authentication required;


    typedef structure {
        string distance_metric;
        string linkage_criteria;
        float feature_height_cutoff;
        float condition_height_cutoff;
        ws_matrix_id input_data;
        string out_workspace;
        string out_clusterset_id;
    } ClusterHierarchicalParams;

    /*
        Clusters features by hierarchical clustering.
    */
    funcdef cluster_hierarchical(ClusterHierarchicalParams params)
        returns (string job_id) authentication required;


    typedef structure {
        float feature_height_cutoff;
        float condition_height_cutoff;
        ws_clusterset_id input_data;
        string out_workspace;
        string out_clusterset_id;
    } ClustersFromDendrogramParams;

    /*
        Given a ClusterSet with a dendogram built from a hierarchical clustering
        method, this function creates new clusters by cutting the dendogram at
        a specific hieght or by some other approach.
    */
    funcdef clusters_from_dendrogram(ClustersFromDendrogramParams params)
        returns (string job_id) authentication required;


    typedef structure {
        ws_clusterset_id input_clusterset;
        string out_workspace;
        string out_report_id;
    } EvaluateClustersetQualityParams;

    /*
        Given a ClusterSet with a dendogram built from a hierarchical clustering
        method, this function creates new clusters by cutting the dendogram at
        a specific hieght or by some other approach.
    */
    funcdef evaluate_clusterset_quality(EvaluateClustersetQualityParams params)
        returns (string job_id) authentication required;


    /*
        method - optional field specifying special type of validation
            necessary for particular clustering method.
    */
    typedef structure {
        string method;
        ws_matrix_id input_data;
    } ValidateMatrixParams;

    funcdef validate_matrix(ValidateMatrixParams params)
        returns (string job_id) authentication optional;

    /*
        transform_type - type of matrix change (one of: add, multiply,
            normalize, fill in empty values, ?).
        transform_value - optional field defining volume of change if
            it's necessary for chosen transform_type.
    */
    typedef structure {
        string transform_type;
        float transform_value;
        ws_matrix_id input_data;
        string out_workspace;
        string out_matrix_id;
    } CorrectMatrixParams;

    funcdef correct_matrix(CorrectMatrixParams params)
        returns (string job_id) authentication required;

};

