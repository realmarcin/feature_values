#install.packages("jsonlite", repos='http://cran.us.r-project.org')
library(jsonlite)
library(clValid)
library(amap)
#library(sp)
library(ape)

methods <- list()

methods[["ClusterServiceR.cluster_k_means"]] <- function(matrix, k) {
    values <- matrix[["values"]]
    #row_names <- matrix[["row_ids"]]
    row_names <- c(1:length(matrix[["row_ids"]]))-1
    row.names(values) <- row_names
    values <- data.matrix(values)
    #col_ids <- matrix[["col_ids"]]
    km <- kmeans(values, k, iter.max = 1000, nstart=1000,algorithm="Lloyd")
    list(cluster_labels=km[["cluster"]])
}

methods[["ClusterServiceR.estimate_k"]] <- function(matrix) {
    values <- matrix[["values"]]
    #row_names <- matrix[["row_ids"]]
    row_names <- c(1:length(matrix[["row_ids"]]))-1
    row.names(values) <- row_names
    values <- data.matrix(values)
    max_clust_num = min(c(50,nrow(values)-1))
    valid <- clValid(values, nClust=c(2:max_clust_num), maxitems=nrow(values), 
        clMethods=c("kmeans"),validation=c("internal"))
    ret <- measures(valid, "Silhouette")[1,,1]
    best_pos <- which.max(ret)
    unbox(as.numeric(names(ret)[best_pos]))
}

methods[["ClusterServiceR.cluster_hierarchical"]] <- function(matrix, 
        distance_metric, linkage_criteria, height_cutoff,
        process_rows) {
    values <- matrix[["values"]]
    row_names <- c(1:length(matrix[["row_ids"]]))-1
    row.names(values) <- row_names
    values <- data.matrix(values)
    cor_mat <- cor(t(values))
    hcout <- hcluster(cor_mat, method="correlation")
    phylo <- as.phylo(hcout)
    newick <- write.tree(phylo, file = "")
    #print(is.ultrametric(tree))
    #print(is.binary.tree(tree))
    #print(is.rooted(tree))
    
    ret <- do.call(methods[["ClusterServiceR.clusters_from_dendrogram"]],
        list(newick, height_cutoff))
    ret
}

methods[["ClusterServiceR.clusters_from_dendrogram"]] <- function(dendrogram,
        height_cutoff) {
    hcout <- as.hclust.phylo(read.tree(text=dendrogram))
    #pdf("hcout.pdf", width=8.5, height=11)
    #plot(hcout)
    groups <- cutree(hcout, h=height_cutoff)
    names <- names(groups)
    cluster_labels <- numeric(length(groups))
    for (pos in 1:length(groups)) {
        name <- as.integer(names[pos])
        value <- as.integer(groups[pos])
        cluster_labels[name + 1] <- value
    }
    list(cluster_labels=cluster_labels, dendrogram=unbox(dendrogram))
}

tryCatch({
    args <- commandArgs(trailingOnly = TRUE)
    input <- fromJSON(args[1], flatten=TRUE, simplifyDataFrame=FALSE, 
        simplifyVector=TRUE, simplifyMatrix=TRUE)
    params <- input[["params"]]
    if (class(params) != "list") {
        input <- fromJSON(args[1], flatten=TRUE, simplifyDataFrame=FALSE, 
            simplifyVector=FALSE, simplifyMatrix=FALSE)
        params <- input[["params"]]
    }
    method <- input[["method"]]
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
