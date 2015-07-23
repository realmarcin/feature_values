#install.packages("jsonlite", repos='http://cran.us.r-project.org')
library(jsonlite)
library(clValid)
library(amap)
#library(sp)
library(ape)

calc_cluster_props = function(logratios_median, cluster, ret) {
    ###mean pairwise correlation
    meancor <- c()
    for(i in 1:max(cluster)) {
        clust_ind <- which(cluster == i)
        cors <- cor(t(logratios_median[clust_ind,]), use="pairwise.complete.obs",method="pearson")
        AbCorC <- mean(cors[lower.tri(cors, diag=FALSE)])
        meancor <- c(meancor, AbCorC)
    }

    ###MSEC
    MSECs <- c()
    for(i in 1:max(cluster)) {
        clust_ind <- which(cluster == i)
        curdata <- logratios_median[clust_ind,]
        MSEall <- median((curdata-median(curdata))^2)
        cmeans <- colMeans(curdata,na.rm=TRUE)
        csds <- apply(curdata,2,sd,na.rm=TRUE)
        MSEC <- median((sweep(curdata,2,cmeans))^2/MSEall)
        MSECs <- c(MSECs, MSEC)
    }

    ret[["meancor"]] <- meancor
    ret[["msecs"]] <- MSECs
    ret
}

clusters_from_dendrogram = function(values, dendrogram, height_cutoff) {
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
    calc_cluster_props(values, cluster_labels, list(cluster_labels=cluster_labels, 
        dendrogram=unbox(dendrogram)))
}

methods <- list()

methods[["ClusterServiceR.cluster_k_means"]] <- function(matrix, k, n_start, 
        max_iter, random_seed) {
    if (is.null(n_start))
        n_start <- 1000
    if (is.null(max_iter))
        max_iter <- 1000
    values <- matrix[["values"]]
    #row_names <- matrix[["row_ids"]]
    row_names <- c(1:length(matrix[["row_ids"]]))-1
    row.names(values) <- row_names
    values <- data.matrix(values)
    #col_ids <- matrix[["col_ids"]]
    if (!is.null(random_seed))
        set.seed(random_seed)
    km <- kmeans(values, k, iter.max = max_iter, nstart=n_start, algorithm="Lloyd")
    calc_cluster_props(values, km[["cluster"]], list(cluster_labels=km[["cluster"]]))
}

methods[["ClusterServiceR.estimate_k"]] <- function(matrix, min_k, max_k, 
        max_iter, random_seed) {
    if (is.null(min_k))
        min_k <- 2
    if (is.null(max_k))
        max_k <- 200
    if (is.null(max_iter))
        max_iter <- 100
    values <- matrix[["values"]]
    #row_names <- matrix[["row_ids"]]
    row_names <- c(1:length(matrix[["row_ids"]]))-1
    row.names(values) <- row_names
    values <- data.matrix(values)
    max_clust_num = min(c(max_k,nrow(values)-1))
    if (!is.null(random_seed))
        set.seed(random_seed)
    valid <- clValid(values, nClust=c(min_k:max_clust_num), maxitems=nrow(values), 
        clMethods=c("kmeans"),validation=c("internal"), iter.max = max_iter)
    ret <- measures(valid, "Silhouette")[1,,1]
    ret_names <- names(ret)
    best_pos <- which.max(ret)
    cluster_count_qualities<-list()
    for (pos in 1:length(ret)) {
        cluster_count <- unbox(ret_names[pos])
        quality <- unbox(ret[pos])
        cluster_count_qualities[[cluster_count]] <- quality
    }
    list(best_k=unbox(as.numeric(ret_names[best_pos])), estimate_cluster_sizes=cluster_count_qualities)
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
    clusters_from_dendrogram(values, newick, height_cutoff)
}

methods[["ClusterServiceR.clusters_from_dendrogram"]] <- function(
        matrix, dendrogram, height_cutoff) {
    values <- matrix[["values"]]
    row_names <- c(1:length(matrix[["row_ids"]]))-1
    row.names(values) <- row_names
    values <- data.matrix(values)
    clusters_from_dendrogram(values, dendrogram, height_cutoff);
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
