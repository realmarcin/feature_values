#install.packages("jsonlite", repos='http://cran.us.r-project.org')
library(jsonlite)

methods <- list()

methods[["ClusterServiceR.cluster_float_rows_r_kmeans"]] <- function(k, input_data) {
    #print(k)
    values <- input_data[["values"]]
    #print(values)
    row_ids <- input_data[["row_ids"]]
    #print(row_ids)
    km <- kmeans(values, k, iter.max = 1000, nstart=1000,algorithm="Lloyd")
    #print(km)
    list(cluster_labels=km[["cluster"]])
}

tryCatch({
    args <- commandArgs(trailingOnly = TRUE)
    json_file <- file(args[1])
    input <- fromJSON(json_file, flatten=TRUE, simplifyDataFrame=FALSE, simplifyVector=TRUE, simplifyMatrix=TRUE)
    method <- input[["method"]]
    params <- input[["params"]][[1]]
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
    output <- toJSON(list(version=unbox("1.1"),error=list(error=unbox(msg),name=unbox("JSONRPCError"),code=unbox(-32603),message=unbox(msg))))
    write(output, file=args[2])
}, finally = {
})
