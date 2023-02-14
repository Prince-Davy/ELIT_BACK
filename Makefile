.PHONY: list clean findsecbugs

.envfile:
  ifneq (,$(wildcard ./.env))
      include .env
      export
  endif

qbuildlocal: .envfile qbuild

testenvlocal: .envfile testenv

testenv: ## Launch testenv with variables
# docker-compose -f docker-compose.testenv.yml build
	docker-compose -f app.testenv.yml up
	docker-compose -f app.testenv.yml down

list:
	@$(MAKE) -pRrq -f $(lastword $(MAKEFILE_LIST)) : 2>/dev/null | awk -v RS= -F: '/^# File/,/^# Finished Make data base/ {if ($$1 !~ "^[#.]") {print $$1}}' | sort | egrep -v -e '^[^[:alnum:]]' -e '^$@$$' | xargs


verifyquality: ## Lint Code
	mvn -ntp checkstyle:check pmd:check 

clean: ## Clean Up temporary files
	rm -Rf .m2/*
	rm -Rf tests_auto/log/*
	rm -Rf target/*

qbuild: ## My comment
	docker-compose -f app.qbuild.yml down
	docker-compose -f app.qbuild.yml build
	docker-compose -f app.qbuild.yml up --remove-orphans
	docker-compose -f app.qbuild.yml down

trivy: ## Scan Application Docker Image
	docker save ${registry_url}/${stack_name}/${service_name}:${releaseversion} -o ${stack_name}.tar
	docker run --rm -v ${PWD}:/code sogetilabsfrance.azurecr.io/lge-trans/lge-devsectools trivy -f json -o target/trivy-results.json -exit-code 1 --severity MEDIUM,HIGH,CRITICAL --input ./${stack_name}.tar
	rm ./${stack_name}.tar

findsecbugs: ## Scan the application code for security issues
## return 0 we do not fail on this
	mkdir target
	docker run --rm -v ${PWD}:/code sogetilabsfrance.azurecr.io/lge-trans/lge-devsectools findsecbugs.sh -sortByClass -onlyAnalyze com.sogeti.- -html -effort:max -projectName ${stack_name} -output target/findsecbugs.html target/*.jar && return 0

dependency-check: ## Scan Application dependencies for CVE see https://jeremylong.github.io/DependencyCheck/dependency-check-cli/arguments.html
## Download here: https://dl.bintray.com/jeremy-long/owasp/dependency-check-5.2.4-release.zip
	docker run --rm -v ${PWD}:/code sogetilabsfrance.azurecr.io/lge-trans/lge-devsectools dependency-check.sh --project ${stack_name} --scan . -f "ALL" --enableExperimental --disableAssembly  --exclude "**/target/**"  --exclude "**/json-server/**" 	--out ./target/dependency-check

doxygen:
	docker run --rm -v ${PWD}:/code sogetilabsfrance.azurecr.io/lge-trans/lge-quality  doxygen.sh  ./  ./target

up: ## Launch Application
	docker-compose up --remove-orphans -d

down: ## Stop Application
	docker-compose down -v

buildandrun: ## rebuild and run
	mvn clean install spring-boot:run -DskipTests

help: ## Display Makefile Rules
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

.DEFAULT_GOAL := help
