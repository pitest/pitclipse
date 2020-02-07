#!/bin/bash

COMMAND="mvn verify -P jacoco";

echo ${COMMAND}
${COMMAND}
