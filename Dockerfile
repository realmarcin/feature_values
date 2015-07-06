FROM kbase/rtmin:1.2
WORKDIR /kb
RUN git clone https://github.com/kbase/dev_container
WORKDIR /kb/dev_container/modules
RUN git clone https://github.com/kbase/jars
RUN git clone https://github.com/kbase/auth
RUN git clone https://github.com/kbase/kbapi_common
WORKDIR /kb/dev_container
RUN ./bootstrap /kb/runtime/
ENV KB_TOP /kb/dev_container
ENV KB_RUNTIME /kb/runtime/
ENV TARGET $KB_TOP
ENV DEPLOY_RUNTIME=$KB_RUNTIME
RUN make
RUN mkdir -p /kb/deployment
RUN mkdir -p /kb/deployment/bin
RUN mkdir -p /kb/deployment/lib
RUN mkdir -p /kb/deployment/services
ENV TARGET /kb/deployment
WORKDIR /kb/dev_container/modules/jars
RUN make deploy
WORKDIR /kb/dev_container/modules/auth
RUN make deploy
WORKDIR /kb/dev_container/modules/kbapi_common
RUN make deploy
######################### end of kbase/devmin:1.1
RUN apt-get update && apt-get install -y \ 
  build-essential \
  python-dev \
  python-setuptools \
  python-numpy \
  python-scipy \
  libatlas-dev \
  libatlas3gf-base
RUN pip install scikit-learn
RUN pip install scipy
RUN apt-get install -y mongodb
WORKDIR /kb/dev_container/modules
RUN mkdir feature_values
COPY . /kb/dev_container/modules/feature_values/
WORKDIR /kb/dev_container/modules/feature_values
RUN bash ./deps/integration_tests.sh
RUN bash ./deps/r_lang.sh
CMD make test
