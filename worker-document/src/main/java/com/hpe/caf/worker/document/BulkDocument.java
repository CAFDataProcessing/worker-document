package com.hpe.caf.worker.document;

import com.hpe.caf.api.worker.WorkerTask;
import com.hpe.caf.worker.document.impl.DocumentImpl;

public final class BulkDocument
{
    private final WorkerTask workerTask;
    private final DocumentImpl document;

    public BulkDocument(final WorkerTask workerTask, final DocumentImpl document)
    {
        this.workerTask = workerTask;
        this.document = document;
    }

    public WorkerTask getWorkerTask()
    {
        return workerTask;
    }

    public DocumentImpl getDocument()
    {
        return document;
    }
}
