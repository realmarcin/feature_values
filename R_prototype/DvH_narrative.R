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
valid <- clValid(logratios_median, nClust=c(2:200),maxitems=3569, clMethods=c("kmeans"),validation=c("internal"))#,metric=c("correlation"))#,method=c("complete")

save(valid, file="validR.Rdata")

pdf("validR.pdf", width=8.5, height=11)
plot(valid)
dev.off(2)
#silhouette max is X clusters val Y

measures <- measures(valid)
###transform cluster number indices to array indices (since no data for K=1)
#clust_range <- c(100:200) - 1 #=183
clust_range <- c(50:150) - 1#=118
###use Silhouette Width by default
maxclustvalid <- max(measures[3,clust_range,])
###force to use first cluster number
maxclust <- which(measures[3,,] == maxclustvalid)[1]


date()


numclusters <- maxclust[1]
prefix <- paste("kmeans_",numclusters,sep="")

save.image()

date()
###Run clustering
###we should be able to reduce the 1000 parameter settings below and still get it to work
#km <- kmeans(logratios_median, numclusters, iter.max = 200, nstart=200,algorithm="Lloyd")
###this is what I used to reproduce exactly the published example, it takes a while
km <- kmeans(logratios_median, numclusters, iter.max = 1000, nstart=1000,algorithm="Lloyd")


date()
save.image()


###reference example and checking 
sahr_regulon <- c("DVU3371","DVU0997","DVU2448","DVU2449","DVU0606","DVU0607")

sahr_indices <- match(sahr_regulon,names( km$cluster))

maxmatch <- max(table(km$cluster[sahr_indices ]))/length(sahr_indices)
print(maxmatch)


###sort clusters by mean pairwise correlation
meancor <- c()
for(i in 1:max(km$cluster)) {
  clust_ind <- which(km$cluster == i)
  cors <- cor(t(logratios_median[clust_ind,]), use="pairwise.complete.obs",method="pearson")
  print(dim(cors))
  AbCorC <- mean(cors[lower.tri(cors, diag=FALSE)])
  meancor <- c(meancor, AbCorC)
}

print(sort(meancor))

#95/118 = 81% = top 20


###sort clusters by mean intergenic distance
tabdata <- read.csv("./882.tab",header=T,sep="\t")
meandist <- c()
for(i in 1:max(km$cluster)) {
  
  clust_ind <- which(km$cluster == i)
  cur_ids <- names( km$cluster)[clust_ind]
  
  tabindA <- match(cur_ids, tabdata[,8])
  tabindB <- match(cur_ids, tabdata[,1])
  tabind <- c(tabindA, tabindB)
  tabind <- tabind[!is.na(tabind)]
  
  starts <- tabdata[tabind,5]
  
  curdists <- c()
  for(a in 1:length(starts)) {
    for(b in 1:length(starts)) {   
      if(b > a) {
      print(paste(a,"_",b,abs(starts[a]-starts[b])))
      curdists <- c(curdists, abs(starts[a]-starts[b]))
      }
    }
  }  
  meandist <- c(meandist, mean(curdists))
}

print(sort(meandist))
#meandist[80]
#which(sort(meandist) == 1429796)
#[1] 105
#105/118 = 89% = top 13


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
pdf("data_rowkmeans_sort_k118.pdf", width=8.5, height=11)
heatmap.2(as.matrix(sortmat), dendrogram="none",Rowv=NULL, Colv=NULL, trace="none",col=mypalette, cexRow=0.25, cexCol=0.5, rowsep=rev(rowseps),scale="none")
dev.off(2)

date()
save.image()

