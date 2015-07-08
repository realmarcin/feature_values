#BEGIN_HEADER
import sklearn.cluster as cl
#END_HEADER


class ClusterServicePy:
    '''
    Module Name:
    ClusterServicePy

    Module Description:
    
    '''

    ######## WARNING FOR GEVENT USERS #######
    # Since asynchronous IO can lead to methods - even the same method -
    # interrupting each other, you must be *very* careful when using global
    # state. A method could easily clobber the state set by another while
    # the latter method is running.
    #########################################
    #BEGIN_CLASS_HEADER
    #END_CLASS_HEADER

    # config contains contents of config file in a hash or None if it couldn't
    # be found
    def __init__(self, config):
        #BEGIN_CONSTRUCTOR
        #END_CONSTRUCTOR
        pass

    def cluster_k_means(self, ctx, matrix, k):
        # ctx is the context object
        # return variables are: returnVal
        #BEGIN cluster_k_means
        values = matrix['values']
        k_means = cl.KMeans(init='k-means++', n_clusters=k, n_init=10)
        k_means.fit(values)
        k_means_labels = k_means.labels_.tolist()
        returnVal = {'cluster_labels': k_means_labels}
        #END cluster_k_means

        # At some point might do deeper type checking...
        if not isinstance(returnVal, dict):
            raise ValueError('Method cluster_k_means return value ' +
                             'returnVal is not type dict as required.')
        # return the results
        return [returnVal]

    def estimate_k(self, ctx, matrix):
        # ctx is the context object
        # return variables are: cluster_number
        #BEGIN estimate_k
        #END estimate_k

        # At some point might do deeper type checking...
        if not isinstance(cluster_number, int):
            raise ValueError('Method estimate_k return value ' +
                             'cluster_number is not type int as required.')
        # return the results
        return [cluster_number]

    def cluster_hierarchical(self, ctx, matrix, distance_metric, linkage_criteria, height_cutoff, process_rows):
        # ctx is the context object
        # return variables are: returnVal
        #BEGIN cluster_hierarchical
        #END cluster_hierarchical

        # At some point might do deeper type checking...
        if not isinstance(returnVal, dict):
            raise ValueError('Method cluster_hierarchical return value ' +
                             'returnVal is not type dict as required.')
        # return the results
        return [returnVal]

    def clusters_from_dendrogram(self, ctx, dendrogram, height_cutoff):
        # ctx is the context object
        # return variables are: returnVal
        #BEGIN clusters_from_dendrogram
        #END clusters_from_dendrogram

        # At some point might do deeper type checking...
        if not isinstance(returnVal, dict):
            raise ValueError('Method clusters_from_dendrogram return value ' +
                             'returnVal is not type dict as required.')
        # return the results
        return [returnVal]
