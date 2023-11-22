#!/usr/bin/env bash

source ~/.nvm/nvm.sh
source ~/.sdkman/bin/sdkman-init.sh

# Frontend
cd ../wisemapping-frontend
nvm install
npm i -g yarn
yarn

# Backend
sdk install java 11.0.21-tem
sdk use java 11.0.21-tem
