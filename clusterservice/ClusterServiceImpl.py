#BEGIN_HEADER
import sklearn.cluster as cl
#END_HEADER


class ClusterService:
    '''
    Module Name:
    ClusterService

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

    def cluster_float_rows_scikit_kmeans(self, ctx, params):
        # ctx is the context object
        # return variables are: returnVal
        #BEGIN cluster_float_rows_scikit_kmeans
        matrix = params['input_data']['values']
        k_means = cl.KMeans(init='k-means++', n_clusters=params['k'], n_init=10)
        k_means.fit(matrix)
        k_means_labels = k_means.labels_
        returnVal = {'cluster_labels': k_means_labels}
        #END cluster_float_rows_scikit_kmeans

        # At some point might do deeper type checking...
        if not isinstance(returnVal, dict):
            raise ValueError('Method cluster_float_rows_scikit_kmeans return value ' +
                             'returnVal is not type dict as required.')
        # return the results
        return [returnVal]
