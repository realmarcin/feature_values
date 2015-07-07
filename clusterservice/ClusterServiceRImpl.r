#install.packages("jsonlite", repos='http://cran.us.r-project.org')
library(jsonlite)
library(clValid)
library(amap)
#library(sp)

methods <- list()

methods[["ClusterServiceR.cluster_k_means"]] <- function(matrix, k) {
    values <- matrix[["values"]]
    #row_ids <- matrix[["row_ids"]]
    #col_ids <- matrix[["col_ids"]]
    km <- kmeans(values, k, iter.max = 1000, nstart=1000,algorithm="Lloyd")
    list(cluster_labels=km[["cluster"]])
}

methods[["ClusterServiceR.estimate_k"]] <- function(matrix) {
    values <- matrix[["values"]]
    row_names <- matrix[["row_ids"]]
    row.names(values) <- row_names
    values <- data.matrix(values)
    max_clust_num = min(c(50,nrow(values)-1))
    valid <- clValid(values, nClust=c(2:max_clust_num), maxitems=nrow(values), clMethods=c("kmeans"),validation=c("internal"))
    ret <- measures(valid, "Silhouette")[1,,1]
    best_pos <- which.max(ret)
    unbox(as.numeric(names(ret)[best_pos]))
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
