#!/bin/bash

set -e

echo "Releasing elasticsearch-analysis-hebrew version $1"

export HEBMORPH_LUCENE_VERSION=2.4.0
export RELEASE_PATH="./releases/elasticsearch-analysis-hebrew-$1/"
mkdir -p ${RELEASE_PATH}

sed -i '.bak' "s/ES-PLUGIN-VERSION/$1/" plugin-descriptor.properties
sed -i '.bak' "s/ES-PLUGIN-VERSION/$1/" pom.xml
mvn clean
mvn package

cp "target/elasticsearch-analysis-hebrew-${1}.jar" plugin-descriptor.properties plugin-security.policy ${RELEASE_PATH}
pushd ${RELEASE_PATH}
wget "http://central.maven.org/maven2/com/code972/hebmorph/hebmorph-lucene/$HEBMORPH_LUCENE_VERSION/hebmorph-lucene-$HEBMORPH_LUCENE_VERSION.jar"
zip "elasticsearch-analysis-hebrew-$1.zip" "elasticsearch-analysis-hebrew-$1.jar" "hebmorph-lucene-$HEBMORPH_LUCENE_VERSION.jar" plugin-descriptor.properties plugin-security.policy

cp ~/packaging/* .
zip "elasticsearch-analysis-hebrew-$1-commercial.zip" "elasticsearch-analysis-hebrew-$1.jar" plugin-descriptor.properties plugin-security.policy dictionary.dict "hebmorph-lucene-commercial-$HEBMORPH_LUCENE_VERSION.jar"
popd

# reset run
cp plugin-descriptor.properties.template plugin-descriptor.properties
cp pom.xml.template pom.xml

# TODO push to bintray
