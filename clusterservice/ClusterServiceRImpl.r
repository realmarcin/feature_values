#install.packages("jsonlite", repos='http://cran.us.r-project.org')
library(jsonlite)
library(clValid)
library(amap)

methods <- list()

methods[["ClusterServiceR.cluster_float_rows_kmeans"]] <- function(params) {
    k <- params[["k"]]
    #print(k)
    input_data <- params[["input_data"]]
    values <- input_data[["values"]]
    #print(values)
    row_ids <- input_data[["row_ids"]]
    #print(row_ids)
    col_ids <- input_data[["col_ids"]]
    #print(col_ids)
    km <- kmeans(values, k, iter.max = 1000, nstart=1000,algorithm="Lloyd")
    #print(km)
    list(cluster_labels=km[["cluster"]])
}

methods[["ClusterServiceR.estimate_k"]] <- function(matrix) {
    values <- as.matrix(matrix[["values"]])
    max_clust_num = min(c(50,nrow(values)-1))
    print(max_clust_num)
    valid <- clValid(values, nClust=c(2:max_clust_num),maxitems=nrow(values), clMethods<-c("kmeans"),validation=c("internal"))
    print(valid)
    valid
}

tryCatch({
    args <- commandArgs(trailingOnly = TRUE)
    json_file <- file(args[1])
    input <- fromJSON(json_file, flatten=TRUE, simplifyDataFrame=FALSE, 
        simplifyVector=TRUE, simplifyMatrix=TRUE)
    method <- input[["method"]]
    params <- input[["params"]]  #[[1]]
    func <- methods[[method]]
    if ( is.null(func) ) {
        stop(paste("ERROR: Function wasn't found: ", method))
    }
    ret <- do.call(func, params)
    output <- toJSON(list(version=unbox("1.1"),result=list(ret)))
    write(output, file=args[2])
}, warning = function(war) {
    print(paste("WARNING: ", war))
}, error = function(err) {
    msg <- paste("ERROR: ", err)
    print(msg)
    output <- toJSON(list(version=unbox("1.1"),error=list(error=unbox(""),
        name=unbox("JSONRPCError"),code=unbox(-32603),message=unbox(msg))))
    write(output, file=args[2])
}, finally = {
})
