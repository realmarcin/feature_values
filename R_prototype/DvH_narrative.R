rm(list=ls())

library(RColorBrewer)
library("gplots")
library("ggplot2")
library("clValid")

###missing data imputation function
missfxn=function(vec){
  mean <- mean(vec,na.rm=TRUE)
  vec[is.na(vec)]=mean
  vec[is.nan(vec)]=mean
  if(is.na(mean)) {
    cat("Error imputed mean is na ", mean, vec, "\n", which(is.na(mean)))
  }
  vec
}

setwd("~/Documents/KBase/clustering/example_DvH/")

###use specified random seed for reproducibility
set.seed(403)

date()

###LOAD DATA
data <- read.table("~/Documents/KBase/clustering/example_DvH/DvH_select.txt",sep="\t",header=T,row.names=1)

date()

dim <- dim(data)
print(dim)
head(data)

###convert to matrix for performance
logratios_median <- as.matrix(data)

##impute missing values
logratios_median <- apply(logratios_median,2,missfxn)

date()

###validation to assess number of clusters
valid <- clValid(logratios_median, nClust=c(2:50),maxitems=3569, clMethods=c("kmeans"),validation=c("internal"))#,metric=c("correlation"))#,method=c("complete")

save(valid, file="validR.Rdata")

pdf("validR.pdf", width=8.5, height=11)
plot(valid)
dev.off(2)
#silhouette max is X clusters val Y

measures <- measures(valid)
###transform cluster number indices to array indices (since no data for K=1)
clust_range <- c(5:50) - 1
###use Silhouette Width by default
maxclustvalid <- max(measures[3,clust_range,])
###force to use first cluster number
maxclust <- which(measures[3,,] == maxclustvalid)[1]


date()


numclusters <- 10
prefix <- paste("kmeans_",numclusters,sep="")

save.image()

date()
###Run clustering
km <- kmeans(logratios_median, numclusters, iter.max = 100, nstart=100,algorithm="Lloyd")

date()
save.image()

###sort dataset based on clusters
clusts <- km$cluster
clusters <-c()
clusters_ind <- vector("list", max(clusts))
rnames <- c()
rowseps <- c()
sortmat <- c()
for(i in 1:max(km$cluster)) {
  if(length(rowseps) > 0) {
    rowseps <- c(rowseps, rowseps[length(rowseps)] + length(which(km$cluster == i) ))
  } else {
    rowseps <- length(which(km$cluster == i))
  }
  index <- which(km$cluster == i)
  index <- sort(index)
  rnames <- c(rnames, row.names(logratios_median)[index])
  sortmat <- rbind(sortmat, logratios_median[index,])
  
  clusters <- c(clusters, paste(paste(index,collapse=","),"/",sep=""))
  
  clusters_ind[[i]] <- index
}

mypalette <- rev(brewer.pal(3, "Blues"))
mypalette <- c(mypalette, brewer.pal(9, "YlOrBr"))

###save svg ordered heatmap
pdf("data_rowkmeans_sort.pdf", width=8.5, height=11)
heatmap.2(as.matrix(sortmat), dendrogram="none",Rowv=NULL, Colv=NULL, trace="none",col=mypalette, cexRow=0.25, cexCol=0.5, rowsep=rev(rowseps),scale="none")
dev.off(2)

date()
save.image()

