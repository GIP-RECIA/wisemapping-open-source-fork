#!/usr/bin/env bash

source ~/.sdkman/bin/sdkman-init.sh

sdk use java 11.0.21-tem

cd wise-wwebapp

mvn deploy -Dmaven.test.skip=true -Darguments="-DskipTests -Dmaven.deploy.skip=true"
