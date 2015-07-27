

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
        id_to_pos - simple representation of a cluster, which maps features/conditions of the cluster to the
        row/col index in the data (0-based index).  The index is useful for fast lookup of data
        for a specified feature/condition in the cluster.
        @optional meancor msec
    */
    typedef structure { 
        mapping<string, int> id_to_pos;
        float meancor;
        float msec;
    } labeled_cluster;

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
        int min_k;
        int max_k;
        int max_iter;
        int random_seed;
        int neighb_size;
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
        int n_start;
        int max_iter;
        int random_seed;
        string algorithm;
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

    typedef structure {
        string version;
        string status;
        string startup_time;
        string giturl;
        string branch;
        string commit;
        string deployment_cfg_path;
        mapping<string, string> safe_configuration;
    } ServiceStatus;

    funcdef status() returns (ServiceStatus);
    
    
	/*******************************************
	* data API: data transfer objects (DTOs) *
	******************************************/    
	
	/*
		[PSN; Jul 22, 2015]
		Introduction:
		
		Data transfer objects (DTO) are needed to package data to readliy vizualize data by UI widgets.
		The methods bellow are aimed to operate on expression matrices, but later we should have similar methods
		to work with fitness data, etc. Thus, methods can be data specific, but output cna be designed in generic way,
		since approcahes to visualize thes types of data are similar. In the very end all of them are based on Float2DMatrix.
		
		But we probably can think more broadly... Float2DMatrix is jsut another complex subtype of a Collection. So, speaking about 
		output data, let us think in terms of Collections, Items, etc...  
		
		On the other hand,  functions and params should be data specific, since we do not support polymorphism. Thus functions and params 
		will have "Matrix" in their names. 
	
		In relation to the ExpressionMatrix and Float2DMatrix, Item can be either row (feature) or column (condtion).	
	
	*/
	
	
	
	
	/*
		Basic information about a particular item in a collection. 
		
    	index - index of the item
    	id - id of the item
    	name - name of the item
    	description - description of the item			
    	properties - additinal proerties: key - property type, value - value. For instance, if item represents a feature, the property type can be a type feature annotation in a genome, like 'function', 'strand', etc	
		
		$$ DTO $$
	*/
	
	typedef structure{
    	int index;
    	string id;
    	string name;
    	string description;
    	mapping<string,string> properties;
	} ItemDescriptor;
		
    /*
		Statistics for a given item in a collection (defined by index) , calculated on the associated vector of values. Typical example is 2D matrix: item is a given row, and correposnding values from all columns
		is an associated vector.   
    	
    	In relation to ExpressionMatrix we can think about a gene (defined by row index in Float2DMatrix) and a vector of expression values across all (or a subset of) conditions. In this case, index_for - index 
    	of a row representing a gene in the Float2DMatrix,  indeces_on - indeces of columns represnting a set of conditions on which we want to calculate statistics. 
    	 
    	index_for - index of the item in a collection for which all statitics is collected
    	indeces_on - indeces of items in the associated vector on which the statistics is calculated
    	size - number of elements in the associated vector
    	avg - mean value for a given item across all elements in the associated vector 
    	min - min value for a given item across all elements in the associated vector 
    	max - max value for a given item across all elements in the associated vector 
    	std - std value for a given item across all elements in the associated vector 
    	missing_values - number of missing values for a given item across all elements in the associated vector 
    	
		$$ DTO $$
    */
    typedef structure{
    	int index_for;
    	list<int> indeces_on;
    	int size;
    	float avg;
    	float min;
    	float max;
    	float std;
    	int missing_values;
    } ItemStat;	
    

	
    /*
		Same as ItemStat, but for a set of Items. Actually it can be modeled as a list<ItemStat>, but sometimes we might need set of sets, and it becomes complicated...
		
    	In relation to ExpressionMatrix, this type can be used to build a sparklines acorss all conditions for a collection of genes. 
    	In this case: indeces_for - indeces of columns representing all (or a subset of) conditions,  indeces_on - indeces of rows representing genes.    	

    	indeces_for - index of the item in a collection for which all statitics is collected
    	indeces_on - indeces of items in the associated vector on which the statistics is calculated
    	size - number of elements defined by indeces_on (expected to be the same for all items defined by indeces_for)
    	avgs - mean values for each item defined by indeces_for across all elements defined by indeces_on 
    	mins - min values for each item defined by indeces_for across all elements defined by indeces_on 
    	maxs - max values for each item defined by indeces_for across all elements defined by indeces_on 
    	stds - std values for each item defined by indeces_for across all elements defined by indeces_on 
    	missing_values - number of missing values for each item defined by indeces_for across all elements defined by indeces_on 
    	
		$$ DTO $$
    */	
    typedef structure{
    	list<int> indeces_for;
    	list<int> indeces_on;
    	
    	int size;    	
		list<float> avgs;	
		list<float> mins;	
		list<float> maxs;
		list<float> stds;
		list<int>	missing_values;
    } SetStat;

	
 	 /*
		To represent a pairwise matrix with sprecalculated statistics. 
		It can be used to represent pairwise correlation for a set of genes. 
	*/		
	typedef structure{
		list<int> indeces;
		int size;
		list<list<float>> values;
		list<float> avgs;	
		list<float> mins;	
		list<float> maxs;
		list<float> stds;
		list<int>	missing_values;
		
	} PairwiseMatrixStat;
	
 	 /*
		General info about matrix, including genome name that needs to be extracted from the genome object
	*/	
  	
	typedef structure{
		string matrix_id;
		string matrix_name;
		string matrix_description;
		string genome_id;
		string genome_name;
		int rows_count;
		int columns_count;
		string scale;
		string type;
		string row_normalization;
		string col_normalization;
	} MatrixDescriptor;
	
 	/*
		All info required for visualization of Matrix (ExpressionMatrix) object in the Matrix Viewer
	*/		
	typedef structure{
		MatrixDescriptor mtx_descriptor;
		list<ItemDescriptor> row_descriptors;
		list<ItemDescriptor> column_descriptors;
		list<ItemStat> row_stats;
		list<ItemStat> column_stats;
	} MatrixUI;
	
	
 	/*
		All info required for visualization of Matrix (ExpressionMatrix) object in the Matrix Viewer
	*/		
	typedef structure{
	
		MatrixDescriptor mtx_descriptor;
		list<ItemDescriptor> feature_set_descriptors;
		list<ItemDescriptor> condition_descriptors;
		
		SetStat feature_set_stat;
		SetStat mtx_set_stat;
		PairwiseMatrixStat feature_set_correlation;
	} MatrixFeatureSetUI;
	
	
	
	/*******************************************
	* data API: parameters and functions      *
	******************************************/    
	
	
    /*
    	Parameters to get basic properties for items from the Float2D type of matrices. 
    	To work uniformly with rows and columns, the type of item ('row' or 'column') should be provided.
    	
    	input_data - worskapce reference to the ExpressionMatrix object (later we should allow to work with other Float2DMatrix-like matrices, e.g. fitness)
    	item_type - type of the items: can be either 'row' or 'column'
    	item_indeces - indeces of items for whch descriptors should be built. Either item_indeces or item_ids should be provided. If both are provided, item_indeces will be used.
    	item_ids - ids of items for whch descriptors should be built. Either item_indeces or item_ids should be provided. If both are provided, item_indeces will be used.
    	requested_property_types - list of property types to be populated for each item. Currently supported property types are: 'function'      	
    */  	
	typedef structure{
        ws_matrix_id input_data;        
    	string item_type;
    	list<int> item_indeces;
    	list<string> item_ids;
    	list<string> requested_property_types;
	} GetMatrixItemDescriptorsParams;
	
	
    funcdef get_matrix_item_descriptors(GetMatrixItemDescriptorsParams) 
    	returns (list<ItemDescriptor>) authentication required;
    		
	
    /*
    	Parameters to get statics for a set of items from the Float2D type of matrices. 
    	
    	input_data - worskapce reference to the ExpressionMatrix object (later we should allow to work with other Float2DMatrix-like matrices, e.g. fitness)
    	item_indeces_for - indeces of items for whch statistics should be calculated 
    	item_indeces_on - indeces of items on whch statistics should be calculated
    	fl_indeces_on - defines whether the indeces_on should be populated in ItemStat objects. The default value = 0. 
    	    	
    */       
    typedef structure{
        ws_matrix_id input_data;
    	list<int> item_indeces_for;
    	list<int> item_indeces_on;
    	boolean fl_indeces_on;
    } GetMatrixItemsStatParams;
            
    funcdef get_matrix_rows_stat(GetMatrixItemsStatParams) 
    	returns (list<ItemStat>) authentication required;
    	
    funcdef get_matrix_columns_stat(GetMatrixItemsStatParams) 
    	returns (list<ItemStat>) authentication required;
    
    /*
		Another version of parameters to get statistics for a set of items from the Float2D type of matrices. 
		This version is more flexible and will be later used to retrieve set of sets (we need to think about optimization).
		  
    	
    	input_data - worskapce reference to the ExpressionMatrix object (later we should allow to work with other Float2DMatrix-like matrices, e.g. fitness)
    	item_indeces_for - indeces of items for wich statistics should be calculated 
    	item_indeces_on - indeces of items on wich statistics should be calculated
    	fl_indeces_on - defines whether the indeces_on should be populated in SetStat objects. The default value = 0. 
    	fl_indeces_for - defines whether the indeces_for should be populated in SetStat objects. The default value = 0.
    	 
    	fl_avgs - defines whether the avgs should be populated in SetStat objects. The default value = 0. 
    	fl_mins - defines whether the mins should be populated in SetStat objects. The default value = 0. 
    	fl_maxs - defines whether the maxs should be populated in SetStat objects. The default value = 0. 
    	fl_stds - defines whether the stds should be populated in SetStat objects. The default value = 0. 
    	fl_missing_values - defines whether the missing_values should be populated in SetStat objects. The default value = 0. 
    */       
    typedef structure{
        ws_matrix_id input_data;
        
    	list<int> item_indeces_for;
    	list<int> item_indeces_on;
        
        boolean fl_indeces_on;
        boolean fl_indeces_for;
        boolean fl_avgs;
        boolean fl_mins;
        boolean fl_maxs;
        boolean fl_stds;
        boolean fl_missing_values;
        
    } GetMatrixSetStatParams;

    /*
		Parameters to retrieve statistics for set of sets. 
		
		In relation to ExpressionMatrix, these parameters can be used to retrive sparklines for several gene clusters generated on the 
		same ExpressionMatrix in one call.  
		
		params - list of params to retrive statistics for a set of items from the Float2D type of matrices.
	*/
    typedef structure{
		list<GetMatrixSetStatParams> params;
    } GetMatrixSetsStatParams;

    
    funcdef get_matrix_row_sets_stat(GetMatrixSetsStatParams)
    	returns (list<SetStat>) authentication required;
    
    funcdef get_matrix_column_sets_stat(GetMatrixSetsStatParams)
    	returns (list<SetStat>) authentication required;


    /*
		Another version of parameters to get statistics for a set of items from the Float2D type of matrices. 
		This version is more flexible and will be later used to retrieve set of sets (we need to think about optimization).
		      	
    	input_data - worskapce reference to the ExpressionMatrix object (later we should allow to work with other Float2DMatrix-like matrices, e.g. fitness)
    	item_indeces_for - indeces of items for whch statistics should be calculated 
    	item_indeces_on - indeces of items on whch statistics should be calculated
    	fl_indeces_on - defines whether the indeces_on should be populated in SetStat objects. The default value = 0. 
    	fl_indeces_for - defines whether the indeces_for should be populated in SetStat objects. The default value = 0.
    	 
    	fl_avgs - defines whether the avgs should be populated in SetStat objects. The default value = 0. 
    	fl_mins - defines whether the mins should be populated in SetStat objects. The default value = 0. 
    	fl_maxs - defines whether the maxs should be populated in SetStat objects. The default value = 0. 
    	fl_stds - defines whether the stds should be populated in SetStat objects. The default value = 0. 
    	fl_missing_values - defines whether the missing_values should be populated in SetStat objects. The default value = 0. 
    */        
    typedef structure{
        ws_matrix_id input_data;
    	list<int> item_indeces;
        boolean fl_row_ids;
        boolean fl_col_ids;
        
    } GetMatrixItemsCorrelationParams;
    
    funcdef get_matrix_rows_correlation(GetMatrixItemsCorrelationParams)
    	returns (PairwiseMatrixStat) authentication required;

    funcdef get_matrix_columns_correlation(GetMatrixItemsCorrelationParams)
    	returns (PairwiseMatrixStat) authentication required;

   /*
		Parameters to retrieve MatrixDescriptor		
	*/        
    typedef structure{
        ws_matrix_id input_data;        
    } GetMatrixDescriptorParams;    
    funcdef get_matrix_descriptor(GetMatrixDescriptorParams)
    	returns (MatrixDescriptor) authentication required;
    	    
    	    
   /*
		Parameters to retrieve MatrixUI		
	*/        
    typedef structure{
        ws_matrix_id input_data;        
    } GetMatrixUIParams;    
    funcdef get_matrix_ui(GetMatrixUIParams)
    	returns (MatrixUI) authentication required;


   /*
		Parameters to retrieve FeatureSetUI		
		Either mtx_id and feature_indeces (or feature_ids), or cluster_set_id and cluster_index should be indicated
	*/        
    typedef structure{
        ws_matrix_id mtx_id;        
        list<int> feature_indeces;
        list<string> feature_ids;

        ws_clusterset_id cluster_set_id;
        int cluster_index;        
    } GetMatrixFeatureSetUIParams;    
    funcdef get_matrix_featureset_ui(GetMatrixFeatureSetUIParams)
    	returns (MatrixFeatureSetUI) authentication required;


};

