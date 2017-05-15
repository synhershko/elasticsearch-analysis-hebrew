#!/bin/bash
set -e

echo "Releasing elasticsearch-analysis-hebrew version $1"

export HEBMORPH_LUCENE_VERSION=6.4.2
export RELEASE_PATH="./releases/elasticsearch-analysis-hebrew-$1"
mkdir -p ${RELEASE_PATH}/elasticsearch/hspell-data-files

cp plugin-descriptor.properties.template plugin-descriptor.properties
cp pom.xml.template pom.xml
sed -i '.bak' "s/ES-PLUGIN-VERSION/$1/" plugin-descriptor.properties
sed -i '.bak' "s/ES-PLUGIN-VERSION/$1/" pom.xml
sed -i '.bak' "s/HEBMORPH-LUCENE-VERSION/$HEBMORPH_LUCENE_VERSION/" pom.xml
mvn clean
mvn package

# Prepare binaries
wget "http://central.maven.org/maven2/com/code972/hebmorph/hebmorph-lucene/$HEBMORPH_LUCENE_VERSION/hebmorph-lucene-$HEBMORPH_LUCENE_VERSION.jar" -P ${RELEASE_PATH}/elasticsearch
cp "target/elasticsearch-analysis-hebrew-${1}.jar" plugin-descriptor.properties plugin-security.policy ${RELEASE_PATH}/elasticsearch

# Package open-source plugin with hspell dictionary
pushd ${RELEASE_PATH}/elasticsearch/hspell-data-files
wget https://github.com/synhershko/HebMorph/raw/master/hspell-data-files/hebrew.wgz
wget https://github.com/synhershko/HebMorph/raw/master/hspell-data-files/hebrew.wgz.desc
wget https://github.com/synhershko/HebMorph/raw/master/hspell-data-files/hebrew.wgz.prefixes
wget https://github.com/synhershko/HebMorph/raw/master/hspell-data-files/hebrew.wgz.sizes
wget https://github.com/synhershko/HebMorph/raw/master/hspell-data-files/hebrew.wgz.stems
wget https://github.com/synhershko/HebMorph/raw/master/hspell-data-files/prefixes.c
wget https://github.com/synhershko/HebMorph/raw/master/hspell-data-files/dmask.c
wget https://github.com/synhershko/HebMorph/raw/master/hspell-data-files/prefix_h.gz
popd

pushd ${RELEASE_PATH}
zip -r "elasticsearch-analysis-hebrew-$1.zip" ./elasticsearch
popd

# Package the commercial plugin
rm -r ${RELEASE_PATH}/elasticsearch/hspell-data-files
cp ./../hebmorph.dictionary/release/* ${RELEASE_PATH}/elasticsearch
cp ./../hebmorph.dictionary/hebmorph-dictionary-loader/build/libs/dictionary-loader-$HEBMORPH_LUCENE_VERSION.jar ${RELEASE_PATH}/elasticsearch

pushd ${RELEASE_PATH}
zip -r "elasticsearch-analysis-hebrew-commercial-$1.zip" ./elasticsearch
popd

# reset run
rm -r ${RELEASE_PATH}/elasticsearch
cp plugin-descriptor.properties.template plugin-descriptor.properties

# publish to bintray
pushd ${RELEASE_PATH}
curl -T elasticsearch-analysis-hebrew-$1.zip -usynhershko:$BINTRAY_API_KEY "https://api.bintray.com/content/synhershko/elasticsearch-analysis-hebrew/elasticsearch-analysis-hebrew-plugin/$1/elasticsearch-analysis-hebrew-$1.zip?publish=1"
popd
