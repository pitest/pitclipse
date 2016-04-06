#!/bin/bash
VERSION=$1
cd pitrunner
mvn versions:set -DnewVersion=$VERSION
mvn clean install
cd ..
cd pitclipse-plugin
mvn tycho-versions:set-version -DnewVersion=$VERSION -Dpitrunner.version=$VERSION
mvn clean install
cd ..
