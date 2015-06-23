module KBaseFeatureValues {

    /* 
        A workspace ID that references a Genome data object.
        @id ws KBaseGenomes.Genome
    */
    typedef string ws_genome_id;

    /* 
        A workspace ID that references a ConditionSet data object.
        @id ws KBaseExperiments.ConditionSet
    */
    typedef string ws_conditionset_id;

    /*
        row_ids - ids for rows.
        col_ids - ids for columns.
        values - two dimensional array values[row][col], where first index 
            (row) goes vertically over outer list and (col) goes horizontally 
            along each each internal list.
        @meta ws length(row_ids) as n_rows
        @meta ws length(col_ids) as n_cols
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
        @meta ws type genome_ref
        @meta ws length(data.row_ids) as feature_count
        @meta ws length(data.col_ids) as condition_count
    */
    typedef structure {
        string type;
        float scale;
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
        @meta ws type genome_ref
        @meta ws length(data.row_labels) as feature_count
        @meta ws length(data.col_labels) as condition_count

    */
    typedef structure {
        string type;
        ws_genome_id genome_ref;
        FloatMatrix2D data;
        AnalysisReport report;
    } FitnessMatrix;

    /* 
        A workspace ID that references a Genome data object.
        @id ws KBaseFeatureValues.ExpressionMatrix KBaseFeatureValues.FitnessMatrix KBaseFeatureValues.BioMatrix2D
    */
    typedef string ws_matrix_id;

    /*
        It maps labels of features/conditions to column/row positions (0-based index).
    */
    typedef mapping<string, int> labeled_cluster;

    /*
        One-dimensional cluster set.
        type - which dimension these clusters were constructed for.
        report - information collected during cluster construction.
        @optional original_data report
	  @meta ws type
    */
    typedef structure {
        string type;
        list<labeled_cluster> clusters;
        ws_matrix_id original_data;
        AnalysisReport report;
    } ClusterSet;

    typedef structure {
        string method;
        ws_matrix_id input_data;
        string out_workspace;
        string out_cluster_id;
    } ClusterFeaturesParams;

    funcdef cluster_features(ClusterFeaturesParams params) 
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

