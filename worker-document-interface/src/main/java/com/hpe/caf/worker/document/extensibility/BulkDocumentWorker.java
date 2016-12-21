package com.hpe.caf.worker.document.extensibility;

import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.model.Documents;

/**
 * The BulkDocumentWorker interface is designed to allow multiple documents to be processed together.
 */
public interface BulkDocumentWorker extends DocumentWorker
{
    /**
     * Processes a batch of documents. Fields can be added or removed from each of the documents.
     *
     * @param documents the batch of documents to be processed
     * @throws InterruptedException if any thread has interrupted the current thread
     * @throws DocumentWorkerTransientException if the documents could not be processed
     */
    void processDocuments(Documents documents) throws InterruptedException, DocumentWorkerTransientException;
}
