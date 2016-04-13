#!/bin/bash
set -e
rm -rf *.zip

mkdir -p ~/.gradle
[[ ! -z "$BINTRAY_USER" ]] && echo "bintrayUser=$BINTRAY_USER" >> ~/.gradle/gradle.properties
[[ ! -z "$BINTRAY_KEY" ]] && echo "bintrayKey=$BINTRAY_KEY" >> ~/.gradle/gradle.proeprties
[[ ! -z "$GRAILS_PORTAL_USER" ]] && echo "grailsPortalUser=$GRAILS_PORTAL_USER" >> ~/.gradle/gradle.proeprties
[[ ! -z "$GRAILS_PORTAL_PASSWORD" ]] && echo "grailsPortalPassword=$GRAILS_PORTAL_PASSWORD" >> ~/.gradle/gradle.proeprties

cat ~/.gradle/gradle.properties

./gradlew clean test assemble

filename=$(find build/libs -name "*.jar" | head -1)
filename=$(basename "$filename")

EXIT_STATUS=0
echo "Publishing archives for branch $TRAVIS_BRANCH"
if [[ -n $TRAVIS_TAG ]] || [[ $TRAVIS_BRANCH == 'master' && $TRAVIS_PULL_REQUEST == 'false' ]]; then

  echo "Publishing archives"

  if [[ -n $TRAVIS_TAG ]]; then
      ./gradlew bintrayUpload || EXIT_STATUS=$?
  else
      ./gradlew artifactoryPublish || EXIT_STATUS=$?
  fi

fi
rm ~/.gradle/gradle.properties

exit $EXIT_STATUS