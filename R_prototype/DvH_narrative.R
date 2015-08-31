library(RColorBrewer)
#library("gplots")
library("ggplot2")
library("clValid")

setwd("~/Documents/KBase/clustering/example_DvH/")
source("~/Documents/java/MAK/src/DataMining_R/Miner.R")

data <- read.table("~/Documents/KBase/clustering/example_DvH/DvH_select.txt",sep="\t",header=T,row.names=1)

dim <- dim(data)
print(dim)
head(data)

logratios_median <- as.matrix(data)

logratios_median <- apply(logratios_median,2,missfxn)
logratios_median_C <- t(apply(logratios_median,1,missfxn))

###validation to assess number of clusters
valid <- clValid(logratios_median, nClust=c(2:50),maxitems=3569, clMethods=c("kmeans"),validation=c("internal"))#,metric=c("correlation"))#,method=c("complete")

validC <- clValid(t(logratios_median_C), nClust=c(2:50),maxitems=673,clMethods=c("kmeans"),validation=c("internal"))#,metric=c("correlation")#,method=c("complete")

save(valid, file="validR.Rdata")
save(validC, file="validC.Rdata")

pdf("valid.pdf", width=8.5, height=11)
plot(valid)
dev.off(2)
#silhouette max is X clusters val Y

pdf("validC.pdf", width=8.5, height=11)
plot(validC)
dev.off(2)
#silhouette max is X clusters for > 2, val Y

numclusters <- 10
numclusters_C <- 8
prefix <- paste("kmeans_",numclusters,sep="")

km <- kmeans(logratios_median, numclusters, iter.max = 1000, nstart=1000,algorithm="Lloyd")
kmC <- kmeans(t(logratios_median),numclusters_C, iter.max = 1000, nstart=1000)

###order matrix according to clusters
sortmat <- mat.or.vec(1,1)
rnames <- mat.or.vec(0,0)

dim <- dim(logratios_median)
MSEs <- c()
MSERs <- c()
MSECs <- c()

clusts <- km$cluster
clusters <-c()
clusters_ind <- vector("list", max(clusts))

rowseps <- c()
dataonlyall_mat <- as.matrix(logratios_median)
for(i in 1:max(km$cluster)) {
  #print(i)
  if(length(rowseps) > 0) {
    #print(paste("sum",length(which(km$cluster == i) ), rowseps[length(rowseps)], ( rowseps[length(rowseps)] + length(which(km$cluster == i) ))))
    rowseps <- c(rowseps, rowseps[length(rowseps)] + length(which(km$cluster == i) ))
  } else {
    rowseps <- length(which(km$cluster == i))
  }
  index <- which(km$cluster == i)
  #print(index)
  index <- sort(index)
  #print(index)           
  #print(row.names(logratios_median)[index])
  rnames <- c(rnames, row.names(logratios_median)[index])
  #print(logratios_median[index,])
  sortmat <- rbind(sortmat, logratios_median[index,])
  
  
  MSE <- ExpCritI(dataonlyall_mat,index,1:dim[2],1,1,0)
  MSER <- ExpCritI(dataonlyall_mat,index,1:dim[2],1,2,0)
  MSEC <- ExpCritI(dataonlyall_mat,index,1:dim[2],1,3,0)
  print(paste(i,MSER,1/(MSER+1),sep=" "))
  
  MSEs <- c(MSEs, 1/(MSE+1))
  MSERs <- c(MSERs, 1/(MSER+1))
  MSECs <- c(MSECs, 1/(MSEC+1))
  
  
  clusters <- c(clusters, paste(paste(index,collapse=","),"/",sep=""))
  
  clusters_ind[[i]] <- index
  
}
sortmat <- sortmat[-1,]
row.names(sortmat) <- rnames




###save MSE values
names(MSEs) <- c(1:numclusters)
MSEs_sort <- sort(MSEs)
write.table(MSEs_sort, paste(prefix,"_clusterMSE.txt",sep=""),sep="\t",row.names=F,col.names=F)
names(MSERs) <- c(1:numclusters)
MSERs_sort <- sort(MSERs)
write.table(MSERs_sort, paste(prefix,"_clusterMSER.txt",sep=""),sep="\t",row.names=F,col.names=F)
names(MSECs) <- c(1:numclusters)
MSECs_sort <- sort(MSECs)
write.table(MSECs_sort, paste(prefix,"_clusterMSEC.txt",sep=""),sep="\t",row.names=F,col.names=F)

write.table(as.character(clusters), paste(prefix,"_clusters.txt",sep=""),sep="\t")





sortmat2 <- mat.or.vec(1,1)
cnames <- mat.or.vec(0,0)
colseps <- c()
for(i in 1:max(kmC$cluster)) {
  #print(i)  
  if(length(colseps) > 0) {
    #print(paste("sum",length(which(km$cluster == i) ), rowseps[length(rowseps)], ( rowseps[length(rowseps)] + length(which(km$cluster == i) ))))
    colseps <- c(colseps, colseps[length(colseps)] + length(which(kmC$cluster == i) ))
  } else {
    colseps <- length(which(kmC$cluster == i))
  }
  index <- which(kmC$cluster == i)
  #print(index)
  index <- sort(index)
  #print(index)           
  print(colnames(sortmat)[index])
  cnames <- c(cnames, colnames(sortmat)[index])
  #print(sortmat[,index])
  sortmat2 <- cbind(sortmat2, sortmat[,index])
}
sortmat2 <- sortmat2[,-1]
colnames(sortmat2) <- cnames

mypalette <- rev(brewer.pal(3, "Blues"))
mypalette <- c(mypalette, brewer.pal(9, "YlOrBr"))
pdf("logratios_median_kmeans_sort.pdf", width=8.5, height=11)
heatmap.2(as.matrix(sortmat2), dendrogram="none",Rowv=NULL, Colv=NULL, trace="none",col=mypalette, cexRow=0.25, cexCol=0.5, rowsep=rev(rowseps),colsep=rev(colseps),scale="none")#,scale="column"
dev.off(2)

heatmap.2(as.matrix(sortmat2), dendrogram="none",trace="none", cexRow=0.25, cexCol=0.5)#,scale="column"


clusts <- km$cluster
dim <- dim(logratios_median)
MSEs <- c()
MSERs <- c()
MSECs <- c()
clusters <-c()
clusters_ind <- vector("list", max(clusts))

for(i in 1:max(clusts)) {
  index <- which(clusts == i)
  write.table(index, paste(prefix,"_cluster_",i,".txt",sep=""),sep="\t",row.names=F,col.names=F)
  
  write.table(row.names(logratios_median)[index], paste(prefix,"_cluster_",i,"_genenames.txt",sep=""),sep="\t",row.names=F,col.names=F)
  
  curdata <- logratios_median[index,]
  row.names(curdata) <- row.names(logratios_median)[index]
  write.table(curdata, paste(prefix,"_cluster_",i,"_data.txt",sep=""),sep="\t",row.names=T,col.names=T)
  
  #pdf(paste(prefix,i,"_line.pdf",sep=""), width=8.5, height=11)
  #plot(t(curdata), type="l")
  #dev.off(2)
  
  dimnow <- dim(curdata)
  
  genesar <- c()
  linesize <- c()
  alphas <- rep(0.7, (dimnow[1]+1)*dimnow[2])#
  colors <- rep("orange",(dimnow[1])*dimnow[2])
  names <- c()
  
  
  for(g in 1:dimnow[1]) {
    linesize <- c(linesize, rep(0.02, dimnow[2]))
    names <- c(names, rep(row.names(curdata)[g], dimnow[2]))
    add <- rep(row.names(curdata)[g], dimnow[2])
    genesar <- c(genesar, add)
  }
  linesize <- c(linesize,rep(0.2, dimnow[2]))#1) #
  genesar <- c(genesar, rep("mean", dimnow[2]))
  colors <- c(colors,rep("blue", dimnow[2]))
  names <- c(names, rep("mean", dimnow[2]))
  
  names(linesize) <-names
  names(alphas) <- names
  names(colors) <-names
  
  curdatamean <- rbind(curdata, colMeans(curdata))
  
  MSE <- ExpCritI(as.matrix(logratios_median),index,1:dim[2],1,1,0)
  MSER <- ExpCritI(as.matrix(logratios_median),index,1:dim[2],1,2,0)
  MSEC <- ExpCritI(as.matrix(logratios_median),index,1:dim[2],1,3,0)war
  print(paste(i,MSER,1/(MSER+1),sep=" "))
  
  MSEs <- c(MSEs, 1/(MSE+1))
  MSERs <- c(MSERs, 1/(MSER+1))
  MSECs <- c(MSECs, 1/(MSEC+1))
  
  clusters <- c(clusters, paste(paste(index,collapse=","),"/",sep=""))
  
  clusters_ind[[i]] <- index
}


names(MSEs) <- c(1:numclusters)
MSEs_sort <- sort(MSEs)
write.table(MSEs_sort, paste(prefix,"_clusterMSE.txt",sep="\t"),sep="\t",row.names=F,col.names=F)
names(MSERs) <- c(1:numclusters)
MSERs_sort <- sort(MSERs)
write.table(MSERs_sort, paste(prefix,"_clusterMSER.txt",sep="\t"),sep="\t",row.names=F,col.names=F)
names(MSECs) <- c(1:numclusters)
MSECs_sort <- sort(MSECs)
write.table(MSECs_sort, paste(prefix,"_clusterMSEC.txt",sep="\t"),sep="\t",row.names=F,col.names=F)

write.table(clusters, paste(prefix,"_clusters.txt",sep="\t"),sep="\t",row.names=F,col.names=F)




pvals <- c()
pvalsR <- c()
pvalsC <- c()
counts <- c()
num <- 10000 
for(i in 1:length(MSERs)) {
  print(i)
  rgenes_l <- vector("list", num)
  count <- 0
  countR <- 0
  countC <- 0
  for(j in 1:num) {
    rgenes_l[[i]] <- sample.int(dim[1],size=length(clusters_ind[[i]]),replace=F)
    #print(rgenes)
    MSE <- ExpCritI(as.matrix(logratios_median),rgenes_l[[i]],c(1,2,3),1,1,0)          
    MSER <- ExpCritI(as.matrix(logratios_median),rgenes_l[[i]],c(1,2,3),1,2,0)
    MSEC <- ExpCritI(as.matrix(logratios_median),rgenes_l[[i]],c(1,2,3),1,3,0)
    #print(MSER)
    
    crits<- 1/ (MSE +1)
    critsR <- 1/ (MSER +1)
    critsC <- 1/ (MSEC +1)
    
    if(MSEs[i] < crits) {
      count <- count+1
    }
    if(MSERs[i] < critsR) {
      countR <- countR+1
    }
    if(MSECs[i] < critsC) {
      countC <- countC+1
    }
  } 
  pvals <- c(pvals, count/num)
  pvalsR <- c(pvalsR, countR/num)
  pvalsC <- c(pvalsC, countC/num)
  
  counts <- rbind(counts, c(count, countR, countC))
}
write.table(cbind(MSEs,pvals), paste(prefix,"_MSE_pvals.txt",sep="\t"),sep="\t",row.names=F,col.names=F)
write.table(cbind(MSERs,pvalsR), paste(prefix,"_MSER_pvals.txt",sep="\t"),sep="\t",row.names=F,col.names=F)
write.table(cbind(MSECs,pvalsC), paste(prefix,"_MSEC_pvals.txt",sep="\t"),sep="\t",row.names=F,col.names=F)

write.table(sortmat, paste(prefix,".txt",sep="\t"),sep="\t",row.names=F,col.names=F)



###global raw data clustering

pdf("data_correlation.pdf", width=8.5, height=11)
heatmap.2(as.matrix(t(logratios_median)), trace="none",distfun=function(x) as.dist(1-cor(t(x))))
dev.off(2)

pdf("data_euclidean.pdf", width=8.5, height=11)
heatmap.2(as.matrix(t(logratios_median)), trace="none")
dev.off(2)



###2D HCL starting points
Imax=100
Imin=10
Jmax=50
Jmin=10


library(amap)
library(Hmisc)


nbs=allpossibleInitial(data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=1)
write.table(nbs, file="CRISPRi_STARTS_noabs_C.txt", sep="\t")

nbs=allpossibleInitial(data,Imin,Imax,Jmin,Jmax,"correlation",useAbs=0, isCol=0)
write.table(nbs, file="CRISPRi_STARTS_noabs_R.txt", sep="\t")




