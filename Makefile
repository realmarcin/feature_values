KB_TOP ?= /kb/dev_container
TARGET ?= /kb/deployment
DIR = $(shell pwd)
LOCAL_BIN = $(DIR)/bin
BIN = $(TARGET)/bin
SERVICE_NAME = KBaseFeatureValues
SUB_SERVICE_LOCAL_DIR = $(DIR)/clusterservice
SUB1_SERVICE_NAME = ClusterServicePy
SUB1_ASYNC_JOB_SCRIPT_FILE = run_ClusterServicePy_async_job.sh
SUB2_SERVICE_NAME = ClusterServiceR
SUB2_ASYNC_JOB_SCRIPT_FILE = run_ClusterServiceR_async_job.sh
ANT = ant
TESTCFG ?= test.cfg
SERVICE_DIR = $(TARGET)/services/$(SERVICE_NAME)
SUB_SERVICE_DIR = $(SERVICE_DIR)/clusterservice
KB_RUNTIME ?= /kb/runtime
JAVA_HOME ?= $(KB_RUNTIME)/java
SERVICE_PORT = 8082
MAX_MEMORY_MB = 4000
KB_PYTHON_PATH ?= $(shell find $(KB_TOP)/modules -maxdepth 2 -name lib -type d | xargs | sed -e 's/ /:/g')

all: compile

compile:
	mkdir -p $(LOCAL_BIN)
	echo '#!/bin/bash' > $(LOCAL_BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	echo 'export KB_TOP=$(KB_TOP)' >> $(LOCAL_BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	echo 'export KB_RUNTIME=$(KB_RUNTIME)' >> $(LOCAL_BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	echo 'export PATH=$$KB_RUNTIME/bin:$$KB_TOP/bin:$$PATH' >> $(LOCAL_BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	echo 'export KB_PYTHON_PATH=$(KB_PYTHON_PATH)' >> $(LOCAL_BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	echo 'export PYTHONPATH=$(TARGET)/lib:$$KB_PYTHON_PATH:$$PYTHONPATH' >> $(LOCAL_BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	echo 'cd $(SUB_SERVICE_LOCAL_DIR)' >> $(LOCAL_BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	echo 'python $(SUB1_SERVICE_NAME)Server.py $$1 $$2 $$3' >> $(LOCAL_BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	chmod a+x $(LOCAL_BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	echo '#!/bin/bash' > $(LOCAL_BIN)/$(SUB2_ASYNC_JOB_SCRIPT_FILE)
	echo 'export KB_TOP=$(KB_TOP)' >> $(LOCAL_BIN)/$(SUB2_ASYNC_JOB_SCRIPT_FILE)
	echo 'export KB_RUNTIME=$(KB_RUNTIME)' >> $(LOCAL_BIN)/$(SUB2_ASYNC_JOB_SCRIPT_FILE)
	echo 'export PATH=$$KB_RUNTIME/bin:$$KB_TOP/bin:$$PATH' >> $(LOCAL_BIN)/$(SUB2_ASYNC_JOB_SCRIPT_FILE)
	echo 'cd $(SUB_SERVICE_LOCAL_DIR)' >> $(LOCAL_BIN)/$(SUB2_ASYNC_JOB_SCRIPT_FILE)
	echo 'Rscript $(SUB2_SERVICE_NAME)Impl.r $$1 $$2' >> $(LOCAL_BIN)/$(SUB2_ASYNC_JOB_SCRIPT_FILE)
	chmod a+x $(LOCAL_BIN)/$(SUB2_ASYNC_JOB_SCRIPT_FILE)
	$(ANT) compile -Djardir=$(DIR)/../jars/lib/jars -Dbindir=$(LOCAL_BIN) -Djava.home=$(JAVA_HOME)

deploy: deploy-client deploy-service deploy-scripts

undeploy:
	@echo "Nothing to undeploy"

deploy-client:
	@echo "No deployment for client"

deploy-service: deploy-scripts
	cp $(DIR)/service/jetty.xml $(SERVICE_DIR)/
	mkdir -p $(SERVICE_DIR)/webapps
	rm $(SERVICE_DIR)/webapps/*.war
	cp $(DIR)/dist/$(SERVICE_NAME).war $(SERVICE_DIR)/webapps/root.war
	cp $(DIR)/deploy.cfg $(SERVICE_DIR)/
	echo '#!/bin/bash' > $(SERVICE_DIR)/start_service
	echo 'export JAVA_HOME=$(JAVA_HOME)' >> $(SERVICE_DIR)/start_service
	echo 'export PATH=$(JAVA_HOME)/bin:$$PATH' >> $(SERVICE_DIR)/start_service
	echo 'JARS=$(TARGET)/lib/jars' >> $(SERVICE_DIR)/start_service
	echo 'if [ -z "$$KB_DEPLOYMENT_CONFIG" ]; then' >> $(SERVICE_DIR)/start_service
	echo '    export KB_DEPLOYMENT_CONFIG=$(TARGET)/deployment.cfg' >> $(SERVICE_DIR)/start_service
	echo 'fi' >> $(SERVICE_DIR)/start_service
	echo 'cd $(SERVICE_DIR)' >> $(SERVICE_DIR)/start_service
	echo 'java -cp $$JARS/jetty/jetty-start-7.0.0.jar:$$JARS/jetty/jetty-all-7.0.0.jar:$$JARS/servlet/servlet-api-2.5.jar -Xmx$(MAX_MEMORY_MB)m -DKB_DEPLOYMENT_CONFIG=$$KB_DEPLOYMENT_CONFIG -Djetty.port=$(SERVICE_PORT) org.eclipse.jetty.start.Main jetty.xml >out.txt 2>err.txt & pid=$$!' >> $(SERVICE_DIR)/start_service
	echo 'echo $$pid > $(SERVICE_DIR)/pid.txt' >> $(SERVICE_DIR)/start_service
	chmod a+x $(SERVICE_DIR)/start_service
	echo '#!/bin/bash' > $(SERVICE_DIR)/stop_service
	echo 'PIDFILE=$(SERVICE_DIR)/pid.txt' >> $(SERVICE_DIR)/stop_service
	echo 'THEPID=$$(cat $$PIDFILE)' >> $(SERVICE_DIR)/stop_service
	echo 'echo "Killing PID $$THEPID..."' >> $(SERVICE_DIR)/stop_service
	echo 'kill $$THEPID' >> $(SERVICE_DIR)/stop_service
	echo 'rm $$PIDFILE' >> $(SERVICE_DIR)/stop_service
	chmod a+x $(SERVICE_DIR)/stop_service

deploy-scripts: check-deps
	mkdir -p $(SERVICE_DIR)
	cp -r $(SUB_SERVICE_LOCAL_DIR) $(SERVICE_DIR)/
	echo '#!/bin/bash' > $(BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	echo 'export KB_TOP=$(TARGET)' >> $(BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	echo 'export KB_RUNTIME=$(KB_RUNTIME)' >> $(BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	echo 'export PATH=$$KB_RUNTIME/bin:$$KB_TOP/bin:$$PATH' >> $(BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	echo 'export PYTHONPATH=$$KB_TOP/lib:$$PYTHONPATH' >> $(BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	echo 'cd $(SUB_SERVICE_DIR)' >> $(BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	echo 'python $(SUB1_SERVICE_NAME)Server.py $$1 $$2 $$3' >> $(BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	chmod a+x $(BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	echo '#!/bin/bash' > $(BIN)/$(SUB2_ASYNC_JOB_SCRIPT_FILE)
	echo 'export KB_TOP=$(TARGET)' >> $(BIN)/$(SUB2_ASYNC_JOB_SCRIPT_FILE)
	echo 'export KB_RUNTIME=$(KB_RUNTIME)' >> $(BIN)/$(SUB1_ASYNC_JOB_SCRIPT_FILE)
	echo 'export PATH=$$KB_RUNTIME/bin:$$KB_TOP/bin:$$PATH' >> $(BIN)/$(SUB2_ASYNC_JOB_SCRIPT_FILE)
	echo 'cd $(SUB_SERVICE_DIR)' >> $(BIN)/$(SUB2_ASYNC_JOB_SCRIPT_FILE)
	echo 'Rscript $(SUB2_SERVICE_NAME)Impl.r $$1 $$2' >> $(BIN)/$(SUB2_ASYNC_JOB_SCRIPT_FILE)
	chmod a+x $(BIN)/$(SUB2_ASYNC_JOB_SCRIPT_FILE)
	$(ANT) deploy -Djardir=$(TARGET)/lib/jars -Dbindir=$(BIN) -Djava.home=$(JAVA_HOME)

test: compile test-client test-service test-scripts

test-client:
	@echo "No tests for client"

test-service:
	@echo "No tests for service"

test-scripts: check-deps
	test/cfg_to_runner.py $(TESTCFG) ""
	test/run_tests.sh

check-deps:
	echo "Checking dependencies..."
	bash $(DIR)/deps/r_lang.sh

clean:
	@echo "No clean is necessary"
