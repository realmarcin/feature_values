


module KBaseFeatureValues {

    /* 
        A workspace ID that references a Genome data object.
        @id ws KBaseGenomes.Genome
    */
    typedef string ws_genome_id;

    /* 
        A workspace ID that references a ConditionSet data object (Note: does not exist yet,
        this is a placeholder).
        @id ws KBaseExperiments.ConditionSet
    */
    typedef string ws_conditionset_id;

    /*
        row_ids - unique ids for rows.
        col_ids - unique ids for columns.
        values - two dimensional array values[row][col], where first index 
            (row) goes vertically over outer list and (col) goes horizontally 
            along each each internal list.
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
        This object is created during upload of matrices.
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
        type - ? level, ratio, log-ratio
        scale - ? probably: raw, loge, log2, log10
        col_normalization - mean_center, median_center, mode_center, zscore
        row_normalization - mean_center, median_center, mode_center, zscore
        data - contains values for (feature,condition) pairs, where 
            features correspond to columns and conditions are rows.
        @optional genome_ref feature_mapping conditionset_ref condition_mapping report
        @metadata ws type genome_ref
        @metadata ws length(data.row_ids) as feature_count
        @metadata ws length(data.col_ids) as condition_count
    */
    typedef structure {
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
        data - contains values for (feature,condition) pairs, where 
            features correspond to columns and conditions are rows.
        @optional genome_ref report
        @metadata ws type genome_ref
        @metadata ws length(data.row_labels) as feature_count
        @metadata ws length(data.col_labels) as condition_count

    */
    typedef structure {
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
        It maps labels of features/conditions to column/row positions (0-based index).
    */
    typedef mapping<string, int> labeled_cluster;

    /*
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
        A workspace ID that references a ClusterSet data object.
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

    funcdef estimate_k(EstimateKParams params)
        returns (string job_id) authentication required;



    typedef structure {
        int k;
        ws_matrix_id input_data;
        string out_workspace;
        string out_clusterset_id;
    } ClusterKMeansParams;

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

    funcdef cluster_hierarchical(ClusterHierarchicalParams params)
        returns (string job_id) authentication required;


    typedef structure {
        float feature_height_cutoff;
        float condition_height_cutoff;
        ws_matrix_id input_data;
        string out_workspace;
        string out_clusterset_id;
    } ClustersFromDendrogramParams;

    funcdef clusters_from_dendrogram(ClustersFromDendrogramParams params)
        returns (string job_id) authentication required;


    typedef structure {
        ws_clusterset_id input_clusterset;
        string out_workspace;
        string out_report_id;
    } EvaluateClustersetQualityParams;

    funcdef evaluate_clusterset_quality(EvaluateClustersetQualityParams params)
        returns (string job_id) authentication required;



    /*
    Generic approach probably won't work 
    typedef structure {
        string method;
        ws_matrix_id input_data;
        string out_workspace;
        string out_cluster_id;
    } ClusterFeaturesParams;

    funcdef cluster_features(ClusterFeaturesParams params) 
        returns (string job_id) authentication required;*/

    /*
        method - optional field specifying special type of validation
            necessary for particular clustering method.
    */
    typedef structure {
        string method;
        ws_matrix_id input_data;
    } ValidateMatrixParams;

    funcdef validate_matrix(ValidateMatrixParams params)
        returns (AnalysisReport) authentication optional;

    /*
        transform_type - type of matrix change (one of: add, multiply,
            normalize, fill in empty values, ?).
        transform_value - optional field defining volume of change if
            it’s necessary for chosen transform_type.
    */
    typedef structure {
        string transform_type;
        float transform_value;
        ws_matrix_id input_data;
        string out_workspace;
        string out_matrix_id;
    } CorrectMatrixParams;

    funcdef correct_matrix(CorrectMatrixParams params)
        returns () authentication required;

};

