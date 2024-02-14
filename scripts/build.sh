#!/usr/bin/env bash

build () {
  mvn clean

  cd ../wisemapping-frontend
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
    buildBack
  fi
}

buildBack () {
  mvn install -Dmaven.test.skip=true -Darguments="-DskipTests"
}

deploy () {
  cd wise-webapp
  mvn deploy -Dmaven.test.skip=true -Darguments="-DskipTests -Dmaven.deploy.skip=true"
}

echo "WiseMapping Building Tool"
echo ""
echo "1 -> build"
echo "2 -> build back only"
echo "3 -> deploy"
echo "4 -> build and deploy"
echo ""

read -r -p "Build type: " choice

case $choice in

  1)
    build
  ;;

  2)
    buildBack
  ;;

  3)
    deploy
  ;;

  4)
    build
    deploy
  ;;

  *)
    echo "Unknown choice"
  ;;

esac
