#!/bin/bash

# IF the variables are set, we can run Sonar analysis.
# Indeed, that means that the build is performed on a branch of the
# pitest/pitclipse repository (or an internal PR) and that Travis CI allows us to use
# encrypted variables (e.g. Sonar's token)
if [ "${SONAR_SCANNER_HOME}" != "" && "${TRAVIS_REPO_SLUG}" = "pitest/pitclipse" ]; then
    COMMAND="mvn verify -P jacoco sonar:sonar";
# ELSE we cannot run Sonar analysis because the branch is coming
# from a fork and Travis CI does not allow use to use encrypted variables.
else
    COMMAND="mvn verify -P jacoco";
fi

echo ${COMMAND}
${COMMAND}
