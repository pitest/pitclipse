#!/usr/bin/env bash
SCRIPT_NAME=$0

REAL_PATH=`realpath $SCRIPT_NAME`
FULL_PATH=`dirname $REAL_PATH`

function usage {
  echo "usage: $SCRIPT_NAME version"
  echo "  version - semantic version number e.g. 1.1.6"
  exit 1
}

function bumpPitrunner {
  PITRUNNER_VERSION=$1
  cd $FULL_PATH/pitrunner
  mvn versions:set -DnewVersion=$PITRUNNER_VERSION && mvn clean install  
}

function bumpPitclipsePlugin {
  PITCLIPSE_VERSION=$1
  cd $FULL_PATH/pitclipse-plugin
  mvn tycho-versions:set-version -DnewVersion=$PITCLIPSE_VERSION -Dpitrunner.version=$PITCLIPSE_VERSION && mvn clean install
}

if [ $# -ne 1 ]; then
  usage
fi

USER_VERSION=$1
TIMESTAMP=$(date --utc +'%Y%m%d%H%M')
VERSION=$USER_VERSION.$TIMESTAMP
echo "Preparing release " $VERSION

bumpPitrunner $VERSION
bumpPitclipsePlugin $VERSION
