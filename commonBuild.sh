#!/usr/bin/env bash

ROOT_DIR=$PWD

function buildModule() {
  MODULE=$1
  MVNARGS=$2

  cd $ROOT_DIR/$MODULE && mvn clean install $2
  cd $ROOT_DIR
}
