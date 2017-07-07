# Hebrew analyzer for Elasticsearch

Powered by HebMorph (https://github.com/synhershko/HebMorph) and licensed under the AGPL3

[![](https://travis-ci.org/synhershko/elasticsearch-analysis-hebrew.svg?branch=master)](https://travis-ci.org/synhershko/elasticsearch-analysis-hebrew) [ ![Download](https://api.bintray.com/packages/synhershko/elasticsearch-analysis-hebrew/elasticsearch-analysis-hebrew-plugin/images/download.svg) ](https://bintray.com/synhershko/elasticsearch-analysis-hebrew/elasticsearch-analysis-hebrew-plugin/_latestVersion)

## Installation

First, install the plugin by invoking the command which fits your elasticsearch version (older versions can be found at the bottom):

```
./bin/elasticsearch-plugin install https://bintray.com/synhershko/elasticsearch-analysis-hebrew/download_file?file_path=elasticsearch-analysis-hebrew-5.3.0.zip
```

For earlier versions (2.x and before) the installation looks a bit different:

```
./bin/plugin install https://bintray.com/synhershko/elasticsearch-analysis-hebrew/download_file?file_path=elasticsearch-analysis-hebrew-2.4.2
```

During installation, you may be prompted for additional permissions:

```
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@     WARNING: plugin requires additional permissions     @
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
* java.io.FilePermission /var/lib/hebmorph/dictionary.dict read
* java.io.FilePermission /var/lib/hspell-data-files read
* java.io.FilePermission /var/lib/hspell-data-files/* read
* java.lang.RuntimePermission accessClassInPackage.sun.reflect.generics.reflectiveObjects
See http://docs.oracle.com/javase/8/docs/technotes/guides/security/permissions.html
for descriptions of what these permissions allow and the associated risks.

Continue with installation? [y/N]y
```

This is normal - please confirm by typing y and hitting Enter.

Then be sure to restart the ElasticSearch service.

## Dictionaries

This plugin uses dictionary files for it's operation. The open-source version is using hspell data files. In the 5.x versions, the dictionaries are bundled in the plugin download itself.

For earlier versions, you will need to obtain the Hebrew dictionary files yourself. The open-sourced hspell files can be downloaded here: https://github.com/synhershko/HebMorph/tree/master/hspell-data-files. Download the entire folder and copy it to be either in the plugin's folder (meaning, `plugins/analysis-hebrew/hspell-data-files`) or under `/var/lib/hspell-data-files`.

Elasticsearch can also be configured to load the dictionary from another folder, this is done by adding the following line to elasticsearch.yml file:

```
    hebrew.dict.path: /PATH/TO/HSPELL/FOLDER
```

You will also need to edit `plugin-security.policy` accordingly.

The dictionary used in by the commercial verion follows a similar pattern.

You can confirm installation by launching elasticsearch and seeing the following in the logs:

```
[2017-03-22T15:43:05,927][INFO ][c.c.e.HebrewAnalysisPlugin] Defaulting to HSpell dictionary loader
[2017-03-22T15:43:07,751][INFO ][c.c.e.HebrewAnalysisPlugin] Trying to load hspell from path plugins/analysis-hebrew/hspell-data-files/
[2017-03-22T15:43:07,751][INFO ][c.c.e.HebrewAnalysisPlugin] Dictionary 'hspell' loaded successfully from path plugins/analysis-hebrew/hspell-data-files/
```

The easiest way to make sure the plugin is installed correctly is to request `/_hebrew/check-word/בדיקה` on your server (for example: browse to http://localhost:9200/_hebrew/check-word/בדיקה). If it loads, it means everything is set up and you are good to go.

## Commercial

Hebmorph is released open-sourced, alongside with hspell dictionary files. The Commercial option will grant you further support in making Hebrew search even better, and it comes with a proprietary dictionary. For more information, check out http://code972.com/hebmorph.

## Usage

Use "hebrew" as analyzer name for fields containing Hebrew text

Query using "hebrew_query" or "hebrew_query_light" to enable exact matches support. "hebrew_exact" analyzer is available for query_string / match queries to be searched exact without lemma expansion.

Because Hebrew uses quote marks to mark acronyms, it is recommended to use the match family queries and not query_string. This is the official recommendation anyway. This plugin does not currently ship with a QueryParser implementation that can be used to power query_string queries.

Here is a sample Sense / Console syntax demonstrating usage of the analyzers in this plugin:

```
GET /_hebrew/check-word/בדיקה

PUT test-hebrew
{
    "mappings": {
        "test": {
            "properties": {
                "content": {
                    "type": "text",
                    "analyzer": "hebrew"
                }
            }
        }
    }
}

PUT test-hebrew/test/1
{
    "content": "בדיקות"
}

POST test-hebrew/_search
{
    "query": {
        "match": {
           "content": "בדיקה"
        }
    }
}
```

## Older Versions

Elasticsearch versions 1.4.0 - 1.7.3:

```
    bin/plugin --install analysis-hebrew --url https://bintray.com/artifact/download/synhershko/elasticsearch-analysis-hebrew/elasticsearch-analysis-hebrew-1.7.zip
```

Even older versions:

~/elasticsearch-0.90.11$ bin/plugin --install analysis-hebrew --url https://bintray.com/artifact/download/synhershko/elasticsearch-analysis-hebrew/elasticsearch-analysis-hebrew-1.0.zip

~/elasticsearch-1.0.0$ bin/plugin --install analysis-hebrew --url https://bintray.com/artifact/download/synhershko/elasticsearch-analysis-hebrew/elasticsearch-analysis-hebrew-1.2.zip

~/elasticsearch-1.2.1$ bin/plugin --install analysis-hebrew --url https://bintray.com/artifact/download/synhershko/elasticsearch-analysis-hebrew/elasticsearch-analysis-hebrew-1.4.zip

~/elasticsearch-1.3.2$ bin/plugin --install analysis-hebrew --url https://bintray.com/artifact/download/synhershko/elasticsearch-analysis-hebrew/elasticsearch-analysis-hebrew-1.5.zip

## License

AGPL3, see LICENSE
