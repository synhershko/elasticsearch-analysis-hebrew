import os, json

import click
from elasticsearch import helpers, Elasticsearch

DEFAULT_INDEX_NAME = 'hebrew-texts'
es = Elasticsearch(hosts=['http://localhost:9200'])


@click.group()
def cli():
    pass


@cli.command()
@click.option('--index-name', default=DEFAULT_INDEX_NAME, help='Index name to use')
@click.option('--path', default='data', help='Data path to index')
def index_files(path, index_name):
    docs = []
    for filename in os.listdir(path):
        doc = None
        if filename.endswith(".json"):
            doc = json.load(os.path.join(path, filename))
        elif filename.endswith(".html") or filename.endswith(".htm") or filename.endswith(".txt"):
            with open(os.path.join(path, filename), 'r') as f:
                contents = f.read()
            doc = {'content': contents}

        if doc:
            print("Indexing " + os.path.join(path, filename))
            doc['filename'] = filename
            docs.append(doc)

    total, errors = helpers.bulk(es, [{
        '_op_type': 'index',
        '_index': index_name,
        '_type': 'doc',
        '_source': doc
    } for doc in docs])

    assert not errors
    print("Indexed total of %d documents" % total)


@cli.command()
@click.option('--index-name', default=DEFAULT_INDEX_NAME, help='Index name to use')
def create_index(index_name):
    es.indices.create(index_name, {
        "settings": {
            "analysis": {
                "analyzer": {
                    "hebrew_html": {
                        "type": "custom",
                        "char_filter": ["html_strip"],
                        "tokenizer": "hebrew",
                        "filter": ["niqqud", "hebrew_lemmatizer", "add_suffix"]
                    }
                }
            }
        },
        "mappings": {
            "doc": {
                "properties": {
                    "content": {
                        "analyzer": "hebrew",
                        "type": "text",
                        "term_vector": "with_positions_offsets",
                        "fields": {
                            "no_html": {
                                "analyzer": "hebrew_html",
                                "type": "text",
                                "term_vector": "with_positions_offsets"
                            }
                        }
                    }
                }
            }
        }
    })
    print("Index %s was created successfully" % index_name)


@cli.command()
@click.option('--index-name', default=DEFAULT_INDEX_NAME, help='Index name to use')
def delete_index(index_name):
    if es.indices.exists(index_name):
        es.indices.delete(index_name)
    print("Index %s no longer exists" % index_name)


@cli.command()
@click.argument('query')
@click.option('--index-name', default=DEFAULT_INDEX_NAME, help='Index name to use')
def search(query, index_name):
    print(es.search(index=index_name, body={
        '_source': ['filename'],
        'query': {'match': {'content': {'query': query, 'operator': 'and'}}},
        'highlight': {
            'fields': {
                'content': {"fragment_size": 250, "number_of_fragments": 5},
                'content.no_html': {"fragment_size": 250, "number_of_fragments": 5}
            }
        }
    }))


@cli.command()
@click.option('--index-name', default=DEFAULT_INDEX_NAME, help='Index name to use')
def index_dummy(index_name):
    docs = [{'content': 'בפברואר', 'filename': '1'}]

    total, errors = helpers.bulk(es, [{
        '_op_type': 'index',
        '_index': index_name,
        '_type': 'doc',
        '_source': doc
    } for doc in docs])

    assert not errors
    print("Indexed total of %d documents" % total)


if __name__ == '__main__':
    cli()
