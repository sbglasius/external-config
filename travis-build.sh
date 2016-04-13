#!/bin/bash
set -e
rm -rf *.zip

mkdir -p $HOME/.gradle
echo >> $HOME/.gradle/gradle.properties "bintrayUser=$BINTRAY_USER"
echo >> $HOME/.gradle/gradle.proeprties "bintrayKey=$BINTRAY_KEY"
echo >> $HOME/.gradle/gradle.proeprties "grailsPortalUser=$GRAILS_PORTAL_USER"
echo >> $HOME/.gradle/gradle.proeprties "grailsPortalPassword=$GRAILS_PORTAL_PASSWORD"
cat $HOME/.gradle/gradle.properties

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
rm $HOME/.gradle/gradle.properties

exit $EXIT_STATUS