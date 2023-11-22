#!/usr/bin/env bash

source ~/.sdkman/bin/sdkman-init.sh

sdk use java 11.0.21-tem

cd wise-webapp
mvn jetty:run-war
