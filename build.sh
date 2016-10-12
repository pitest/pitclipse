#!/usr/bin/env bash

. ./commonBuild.sh

buildModule guava-bundle && buildModule pitest-bundles && buildModule pitrunner && buildModule pitclipse-plugin
