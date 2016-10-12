#!/usr/bin/env bash

. ./commonBuild.sh

buildModule guava-bundle && buildModule pitest-bundles && buildModule jbehave-osgi && buildModule pitrunner && buildModule pitclipse-plugin "-Pui-test"
