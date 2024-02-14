#!/usr/bin/env bash

source ~/.nvm/nvm.sh
source ~/.sdkman/bin/sdkman-init.sh

# Frontend
cd ../wisemapping-frontend
nvm install
npm i -g yarn
yarn

# Backend
cd ../wisemapping-open-source
if ! sdk env; then
  sdk env install
fi
