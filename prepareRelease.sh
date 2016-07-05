#!/bin/bash
SCRIPT_NAME=$0

function usage {
  echo "usage: $SCRIPT_NAME version"
  echo "  version - semantic version number e.g. 1.1.6"
  exit 1
}

if [ $# -ne 1 ]; then
  usage
fi

USER_VERSION=$1
TIMESTAMP=`date --utc +'%Y%m%d%H%M'`
VERSION=$USER_VERSION.$TIMESTAMP
echo "Preparing release " $VERSION
cd pitrunner
mvn versions:set -DnewVersion=$VERSION
mvn clean install
cd ..
cd pitclipse-plugin
mvn tycho-versions:set-version -DnewVersion=$VERSION -Dpitrunner.version=$VERSION
mvn clean install
cd ..
