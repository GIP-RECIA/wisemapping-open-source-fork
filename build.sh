#!/usr/bin/env bash

source ~/.nvm/nvm.sh
source ~/.sdkman/bin/sdkman-init.sh

mvn clean

cd ../wisemapping-frontend
nvm use

if yarn build; then
  cd ..

  mkdir -p wisemapping-open-source/wise-ui/target/wisemapping-mindplot/package/dist
  mkdir -p wisemapping-open-source/wise-ui/target/wisemapping-mindplot/package/src
  mkdir -p wisemapping-open-source/wise-ui/target/wisemapping-mindplot/package/assets
  mkdir -p wisemapping-open-source/wise-ui/target/wisemapping-mindplot/package/libraries
  cp -r wisemapping-frontend/packages/mindplot/dist/* wisemapping-open-source/wise-ui/target/wisemapping-mindplot/package/dist
  cp -r wisemapping-frontend/packages/mindplot/src/* wisemapping-open-source/wise-ui/target/wisemapping-mindplot/package/src
  cp -r wisemapping-frontend/packages/mindplot/assets/* wisemapping-open-source/wise-ui/target/wisemapping-mindplot/package/assets
  cp -r wisemapping-frontend/packages/mindplot/libraries/* wisemapping-open-source/wise-ui/target/wisemapping-mindplot/package/libraries
  cp -r wisemapping-frontend/packages/mindplot/package.json wisemapping-open-source/wise-ui/target/wisemapping-mindplot/package/package.json
  cp -r wisemapping-frontend/packages/mindplot/README.md wisemapping-open-source/wise-ui/target/wisemapping-mindplot/package/README.md


  mkdir -p wisemapping-open-source/wise-ui/target/wisemapping-webapp/package/dist
  mkdir -p wisemapping-open-source/wise-ui/target/wisemapping-webapp/package/src
  cp -r wisemapping-frontend/packages/webapp/dist/* wisemapping-open-source/wise-ui/target/wisemapping-webapp/package/dist
  cp -r wisemapping-frontend/packages/webapp/src/* wisemapping-open-source/wise-ui/target/wisemapping-webapp/package/src
  cp -r wisemapping-frontend/packages/webapp/package.json wisemapping-open-source/wise-ui/target/wisemapping-webapp/package/package.json
  cp -r wisemapping-frontend/packages/webapp/README.md wisemapping-open-source/wise-ui/target/wisemapping-webapp/package/README.md

  cd wisemapping-open-source
  sdk use java 11.0.21-tem
  mvn install -Dmaven.test.skip=true -Darguments="-DskipTests"
fi
