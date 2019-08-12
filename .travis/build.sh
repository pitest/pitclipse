#!/bin/bash

# IF the variable is set, we can run Sonar analysis.
# Indeed, that means that the build is performed on a branch of the
# pitest/pitclipse repository and that Travis CI allows us to use
# encrypted variables (e.g. Sonar's token)
if [ "${SONAR_SCANNER_HOME}" != "" ]; then
    COMMAND="mvn verify -P jacoco sonar:sonar";

# ELSE we cannot run Sonar analysis because the branch is coming
# from a fork and Travis CI does not allow use to use encrypted variables.
else
    COMMAND="mvn verify -P jacoco";
fi

echo ${COMMAND}
${COMMAND}