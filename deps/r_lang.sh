if [ -z "$(which Rscript)" ]; then
  apt-get install -y r-base
fi
R -e 'if(!require(jsonlite)) install.packages("jsonlite", repos="http://cran.us.r-project.org")'
R -e 'if(!require(clValid)) install.packages("clValid", repos="http://cran.us.r-project.org")'
R -e 'if(!require(amap)) install.packages("amap", repos="http://cran.us.r-project.org")'
R -e 'if(!require(sp)) install.packages("sp", repos="http://cran.us.r-project.org")'
