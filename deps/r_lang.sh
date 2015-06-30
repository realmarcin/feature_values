if [ -z "$(which Rscript)" ]; then
  apt-get install r-base
fi
R -e 'if(!require(jsonlite)) install.packages("jsonlite", repos="http://cran.us.r-project.org")'