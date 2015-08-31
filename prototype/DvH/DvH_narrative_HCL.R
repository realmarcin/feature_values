rm(list=ls())

library(RColorBrewer)
library("gplots")
library("ggplot2")
library("clValid")
library("amap")

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

setwd("./")

###use specified random seed for reproducibility
set.seed(403)

date()

###LOAD DATA
data <- read.table("./DvH_select.txt",sep="\t",header=T,row.names=1)

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
valid <- clValid(logratios_median, nClust=c(2:200),maxitems=3569, clMethods=c("hierarchical"),,metric=c("correlation"),validation=c("internal"))#)#,method=c("complete")

save(valid, file="validR.Rdata")

pdf("validR.pdf", width=8.5, height=11)
plot(valid)
dev.off(2)
#silhouette max is X clusters val Y

measures <- measures(valid)
###transform cluster number indices to array indices (since no data for K=1)
clust_range <- c(100:200) - 1
###use Silhouette Width by default
maxclustvalid <- max(measures[3,clust_range,])
###force to use first cluster number
maxclust <- which(measures[3,,] == maxclustvalid)[1]


date()


numclusters <- maxclust[1]
prefix <- paste("hcl_",numclusters,sep="")

save.image()

date()
###Run clustering
hclout <- hcluster(logratios_median, numclusters, iter.max = 100, nstart=100,algorithm="Lloyd")

date()
save.image()

