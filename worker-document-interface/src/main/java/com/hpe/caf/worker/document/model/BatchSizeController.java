package com.hpe.caf.worker.document.model;

import com.hpe.caf.worker.document.extensibility.BulkDocumentWorker;

/**
 * Used for controlling the sizes of the batches passed to implementations of the {@link BulkDocumentWorker} interface.
 */
public interface BatchSizeController extends DocumentWorkerObject
{
    /**
     * Gets the maximum number of documents that should be included in a batch before it is automatically closed.
     *
     * @return the maximum batch size
     */
    int getMaximumBatchSize();

    /**
     * Gets the maximum length of time (in milliseconds) that a batch should be allowed to build up before it is automatically closed.
     *
     * @return the maximum number of milliseconds that a batch should be allowed to build up for
     */
    long getMaximumBatchTime();

    /**
     * Sets the maximum number of documents that should be included in a batch before it is automatically closed.
     *
     * @param maxBatchSize the maximum number of documents to include in a batch
     */
    void setMaximumBatchSize(int maxBatchSize);

    /**
     * Sets the maximum length of time (in milliseconds) that a batch should be allowed to build up before it is automatically closed.
     * Having a maximum time ensures that batches of documents continue to be processed even when the workload is such that the maximum
     * batch size is not reached.
     *
     * @param maxBatchTime the maximum number of milliseconds to allow a batch to build up
     */
    void setMaximumBatchTime(long maxBatchTime);
}
