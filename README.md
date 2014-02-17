Hebrew analyzer for Elasticsearch, powered by HebMorph

Install: ~/elasticsearch-0.90.11$ bin/plugin --install analysis-hebrew --url http://dl.bintray.com/synhershko/HebMorph/elasticsearch-analysis-hebrew-1.0.zip


Use "hebrew" as analyzer name for fields containing Hebrew text

Query using "hebrew_query" or "hebrew_query_light" to enable exact matches support. "hebrew_exact" analyzer is available for query_string / match queries to be searched exact without lemma expansion.

More documentation coming soon
