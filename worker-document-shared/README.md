## worker-document-shared
This module defines the Document Worker contract; the structure of the Request object that Document Workers accept, and the structure of the Response object that they return.

### Worker Request
This is an example of a worker request.  It includes a set of fields which the Document Worker may use.  Different workers may require different sets of fields to be passed to them.

    {
        "fields": {
            "TITLE":     [{"data": "A Christmas Carol"}],
            "AUTHOR":    [{"data": "Charles Dickens"}],
            "PUB_DATE":  [{"data": "December 19, 1843"}],
            "CONTENT":   [{"data": "/Documents/Books/Charles_Dickens/A_Christmas_Carol.txt", "encoding": "storage_ref"}],
            "COVER_PIC": [{"data": "QSBDaHJpc3RtYXMgQ2Fyb2wgQm9vayBDb3Zlcg==", "encoding": "base64"}]
        }
    }

The fields sent to the worker can either be strings, storage references or binary data. An example of each of these can seen above. The name of the book is passed by string, the picture of the book cover is binary data and is passed using base64, and the contents of the book is passed indirectly to the worker via a storage reference.

### Worker Response
For the sake of this example we will assume that this Document Worker is going to use the fields that have been passed to it to count the number of words in the content, the number of pages in the content, and to lookup the publishers.

This is an example response.  This response causes three new fields to be added to the document object.  The first two fields are single-value fields and the final one is a multi-value field.

    {
        "fieldChanges": {
            "WORD_COUNT": {"action": "add", "values": [{"data": "28944"}]},
            "PAGE_COUNT": {"action": "add", "values": [{"data": "116"}  ]},
            "PUBLISHER" : {"action": "add", "values": [{"data": "Chapman and Hall"}, {"data": "Elliot Stock"}]}
        }
    }
