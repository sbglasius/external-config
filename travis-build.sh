#!/usr/bin/env bash
set -e
echo "TRAVIS_TAG          : $TRAVIS_TAG"
echo "TRAVIS_BRANCH       : $TRAVIS_BRANCH"
echo "TRAVIS_PULL_REQUEST : $TRAVIS_PULL_REQUEST"
echo "Publishing archives for branch $TRAVIS_BRANCH"

./gradlew clean check install --stacktrace

EXIT_STATUS=0
# Only publish if the branch is on master, and it is not a PR
if [[ -n "${TRAVIS_TAG}" ]] || [[ "${TRAVIS_BRANCH}" == 'master' && "${TRAVIS_PULL_REQUEST}" == "false" ]]; then
  echo "Publishing archives for branch $TRAVIS_BRANCH"
  if [[ -n ${TRAVIS_TAG} ]]; then
    echo "Pushing build to Bintray"
    ./gradlew bintrayUpload -PpluginVersion=${TRAVIS_TAG}  || EXIT_STATUS=$?
  else
    echo "Publishing snapshot to OJO"
    ./gradlew artifactoryPublish || EXIT_STATUS=$?
  fi
fi
exit ${EXIT_STATUS}
