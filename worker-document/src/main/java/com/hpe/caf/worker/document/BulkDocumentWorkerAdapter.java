package com.hpe.caf.worker.document;

import com.hpe.caf.worker.document.extensibility.BulkDocumentWorker;
import com.hpe.caf.api.worker.BulkWorker;
import com.hpe.caf.api.worker.BulkWorkerRuntime;
import com.hpe.caf.worker.document.impl.ApplicationImpl;

/**
 * This class allows implementations of the BulkDocumentWorker class can be used with the Worker Framework.
 */
public final class BulkDocumentWorkerAdapter extends DocumentWorkerAdapter implements BulkWorker
{
    /**
     * This is the actual implementation of the worker.<p>
     * This class is adapting its interface so that it can be used with the bulk methods of the Worker Framework.
     */
    private final BulkDocumentWorker bulkDocumentWorker;

    public BulkDocumentWorkerAdapter(final ApplicationImpl application, final BulkDocumentWorker bulkDocumentWorker)
    {
        super(application, bulkDocumentWorker);
        this.bulkDocumentWorker = bulkDocumentWorker;
    }

    @Override
    public void processTasks(BulkWorkerRuntime runtime) throws InterruptedException
    {
        final BulkDocumentMessageProcessor messageProcessor
            = new BulkDocumentMessageProcessor(application, bulkDocumentWorker, runtime);

        messageProcessor.processTasks();
    }
}
