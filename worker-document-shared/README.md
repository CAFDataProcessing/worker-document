## worker-document-shared
This module defines the Document Worker contract: the structure of the Request objects that Document Workers accept, and the structure of the Response objects that they return.

The structures of these Request and Response objects depend on whether a Document Worker performs "Fields Enrichment" or "Composite Document Handling".

### Fields Enrichment

#### Worker Request
This is an example of a Fields Enrichment worker request.  It includes a set of fields which the Document Worker may use.  Different workers may require different sets of fields to be passed to them.

    {
        "fields": {
            "TITLE":     [{"data": "A Christmas Carol"}],
            "AUTHOR":    [{"data": "Charles Dickens"}],
            "PUB_DATE":  [{"data": "December 19, 1843"}],
            "CONTENT":   [{"data": "/Documents/Books/Charles_Dickens/A_Christmas_Carol.txt", "encoding": "storage_ref"}],
            "COVER_PIC": [{"data": "QSBDaHJpc3RtYXMgQ2Fyb2wgQm9vayBDb3Zlcg==", "encoding": "base64"}]
        }
    }

The fields sent to the worker can either be strings, storage references or binary data. An example of each of these can be seen above. The name of the book is passed by string, the picture of the book cover is binary data and is passed using base64, and the contents of the book is passed indirectly to the worker via a storage reference.

#### Worker Response
For the sake of this example we will assume that this Document Worker is going to use the fields that have been passed to it to count the number of words in the content, the number of pages in the content, and to lookup the publishers.

This is an example response.  This response causes three new fields to be added to the document object.  The first two fields are single-value fields and the final one is a multi-value field.

    {
        "fieldChanges": {
            "WORD_COUNT": {"action": "add", "values": [{"data": "28944"}]},
            "PAGE_COUNT": {"action": "add", "values": [{"data": "116"}  ]},
            "PUBLISHER" : {"action": "add", "values": [{"data": "Chapman and Hall"}, {"data": "Elliot Stock"}]}
        }
    }


### Composite Document Handling
A "composite document" is a document that includes subdocuments.

#### Worker Request
This is an example of a Composite Document worker request. It includes not only a set of fields for the document supplied but also subdocuments of this document, each subdocument having fields of its own:

    {
        "document": {
            "reference": "/inputFolder/FW json.b.msg",
            "fields": {
                "subject": [{"data": "FW: json.b"}],
                "reference": [{"data": "/inputFolder/FW json.b.msg"}],
                "FILENAME": [{"data": "FW json.b.msg"}],
                "FAMILY_reference": [{"data": "/inputFolder/FW json.b.msg"}],
                ...
                "IS_ROOT": [{"data": "true"}]
            },
            "subdocuments": [
                {
                    "reference": "/inputFolder/FW json.b.msg:0",
                    "fields": {
                        "reference": [{"data": "/inputFolder/FW json.b.msg:0"}],
                        "CAF_SUBFILE_DEPTH": [{"data": "1"}],
                        "FILENAME": [{"data": "marathon.env"}],
                        "FAMILY_reference": [{"data": "/inputFolder/FW json.b.msg"}],
                        ...
                        "IS_ROOT": [{"data": "false"}]
                    }
                },
                {
                    "reference": "/inputFolder/FW json.b.msg:1",
                    "fields": {
                        "reference": [{"data": "/inputFolder/FW json.b.msg:1"}],
                        "CAF_SUBFILE_DEPTH": [{"data": "1"}],
                        "FILENAME": [{"data": "marathon.json.b"}],
                        "FAMILY_reference": [{"data": "/inputFolder/FW json.b.msg"}],
                        ...
                        "IS_ROOT": [{"data": "false"}]
                    }
                }
            ]
        }
    }

#### Worker Response
This is an example of a Composite Document worker response. It includes the same information recorded in the worker request (the composite document's "reference", "fields", and "subdocuments") but additionally the response contains a "changeLog" that records the changes made to the composite document. In this case, several fields have been removed from the document, several fields have been added to it, some fields have been added to the subdocument at index 1, and some fields have been added to the subdocument at index 0.

    {
        "document": {
            "reference": "/inputFolder/FW json.b.msg",
            "fields": {
                "subject": [{"data": "FW: json.b"}],
                "reference": [{"data": "/inputFolder/FW json.b.msg"}],
                "FILENAME": [{"data": "FW json.b.msg"}],
                "FAMILY_reference": [{"data": "/inputFolder/FW json.b.msg"}],
                ...
                "IS_ROOT": [{"data": "true"}]
            },
            "subdocuments": [
                {
                    "reference": "/inputFolder/FW json.b.msg:0",
                    "fields": {
                        "reference": [{"data": "/inputFolder/FW json.b.msg:0"}],
                        "CAF_SUBFILE_DEPTH": [{"data": "1"}],
                        "FILENAME": [{"data": "marathon.env"}],
                        "FAMILY_reference": [{"data": "/inputFolder/FW json.b.msg"}],
                        ...
                        "IS_ROOT": [{"data": "false"}]
                    }
                },
                {
                    "reference": "/inputFolder/FW json.b.msg:1",
                    "fields": {
                        "reference": [{"data": "/inputFolder/FW json.b.msg:1"}],
                        "CAF_SUBFILE_DEPTH": [{"data": "1"}],
                        "FILENAME": [{"data": "marathon.json.b"}],
                        "FAMILY_reference": [{"data": "/inputFolder/FW json.b.msg"}],
                        ...
                        "IS_ROOT": [{"data": "false"}]
                    }
                }
            ]
        },
        "changeLog": [
            {
                "name": "worker-familyhashing:1.0.0-12",
                "changes": [
                    {
                        "removeFields": ["WORD_COUNT", "PUBLISHER"]
                    },
                    {
                        "addFields": {
                            "IS_EMAIL": [{"data": "true"}],
                            "THREAD_ID": [{"data": "76f9c1f0860a9cc6"}],
                            "COMPARISON_HASH": [{"data": "5eddf0438a7b34b3"}],
                            ...
                        }
                    },
                    {
                        "updateSubdocument": {
                            "index": 1,
                            "reference": "/inputFolder/FW json.b.msg:1",
                            "changes": [
                                {
                                    "addFields": {
                                        "FAMILY_COMPARISON_HASH": [{"data": "5eddf0438a7b34b3"}],
                                        "COMPARISON_HASH": [{"data": "87387ac90886116"}],
                                        ...
                                    }
                                }
                            ]
                        }
                    },
                    {
                        "updateSubdocument": {
                            "index": 0,
                            "reference": "/inputFolder/FW json.b.msg:0",
                            "changes": [
                                {
                                    "addFields": {
                                        "FAMILY_COMPARISON_HASH": [{"data": "5eddf0438a7b34b3"}],
                                        "COMPARISON_HASH": [{"data": "2eabead4437f45cd"}],
                                        ...
                                    }
                                }
                            ]
                        }
                    }
                ]
            }
        ]	
    }


### Custom Document Scripts

The composite document format also supports running custom scripts when documents are being processed.

Scripts may be specified inline using the `script` key, or alternatively references to external scripts may be specified using the `storageRef` or `url` keys.

The scripting engine to use may be specified using the `engine` key.  The current version of the framework supports specifying [`NASHORN`](https://en.wikipedia.org/wiki/Nashorn_(JavaScript_engine)) or [`GRAAL_JS`](https://www.graalvm.org/).  For backwards compatibility reasons Nashorn is the default and is used if no scripting engine is specified.

    {
        "document": {
            "reference": "some-reference",
            "fields": {
                "TITLE":     [{"data": "A Christmas Carol"}],
                "AUTHOR":    [{"data": "Charles Dickens"}],
                ...
            }
        },
        "scripts": [
            {
                "name": "resetDocumentOnError.js",
                "script": "function onError(document, error) { document.getField('ERROR').add(error); }",
                "engine": "GRAAL_JS"
            },
            {
                "name": "workflow.js",
                "storageRef": "/Scripts/WorkflowScript.js"
            },
            {
                "name": "trackDocuments.js",
                "url": "http://scriptserver/trackDocuments.js",
                "engine": "NASHORN"
            }
        ]
    }
