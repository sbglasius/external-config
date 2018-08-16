#!/bin/bash
set -ex

echo "Preparing BountyCastel Security provider"
# Workaround to using openjdk7 with Gradle due to security issue:
# https://github.com/gradle/gradle/issues/2421
# as described here: https://github.com/gradle/gradle/issues/2421#issuecomment-327838985
BCPROV_FILENAME=bcprov-ext-jdk15on-158.jar
BCPROV_PATH=$HOME/.bountycastel
BCPROV_FULL_PATH=${BCPROV_PATH}/${BCPROV_FILENAME}
mkdir -p ${BCPROV_PATH}
#if [ ! -f ${BCPROV_FULL_PATH} ]; then
    curl https://bouncycastle.org/download/${BCPROV_FILENAME} -o ${BCPROV_FULL_PATH}
#fi
sudo cp ${BCPROV_FULL_PATH} /usr/lib/jvm/java-7-openjdk-amd64/jre/lib/ext
sudo perl -pi.bak -e 's/^(security\.provider\.)([0-9]+)/$1.($2+1)/ge' /etc/java-7-openjdk/security/java.security
echo "security.provider.1=org.bouncycastle.jce.provider.BouncyCastleProvider" | sudo tee -a /etc/java-7-openjdk/security/java.security
