# Demo

## Prerequisites

Make sure you have an Elasticsearch cluster with the Hebrew plugin installed and ready.

If not, you can build and run a Docker image (everything you need is provided in this folder as well):

```bash
export ES_VERSION=5.5.0
docker build --build-arg ES_VERSION=$ES_VERSION -t code972/elasticsearch-analysis-hebrew:v$ES_VERSION .
docker run -p 0.0.0.0:9200:9200 -t code972/elasticsearch-analysis-hebrew:v$ES_VERSION
```

Also required is Python 3 and running the packages installation (`sudo pip3 install -r requirements.txt`).

## Running

Setting up:

```bash
python3 demo.py create_index
python3 demo.py index_files --path (path, or put files in a folder called data)
```

And then searching:

```bash
➜  demo git:(master) ✗ python3 tester.py search בדיקה
{'took': 108, 'timed_out': False, '_shards': {'total': 5, 'successful': 5, 'failed': 0}, 'hits': {'total': 1, 'max_score': 0.627985, 'hits': [...]}}]}}
```

The hits array is going to show the IDs, scores and highlighted snippets for each hit.
