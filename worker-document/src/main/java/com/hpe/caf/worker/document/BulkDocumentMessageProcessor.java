/*
 * Copyright 2015-2017 EntIT Software LLC, a Micro Focus company.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hpe.caf.worker.document;

import com.hpe.caf.api.worker.BulkWorkerRuntime;
import com.hpe.caf.api.worker.InvalidTaskException;
import com.hpe.caf.api.worker.TaskRejectedException;
import com.hpe.caf.api.worker.WorkerResponse;
import com.hpe.caf.api.worker.WorkerTask;
import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.extensibility.BulkDocumentWorker;
import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.impl.DocumentWorkerObjectImpl;
import com.hpe.caf.worker.document.model.BatchSizeController;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.Documents;
import com.hpe.caf.worker.document.model.InputMessageProcessor;
import com.hpe.caf.worker.document.tasks.AbstractTask;
import com.hpe.caf.worker.document.util.DocumentFunctions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;

public final class BulkDocumentMessageProcessor
{
    private final ApplicationImpl application;
    private final BulkDocumentWorker bulkDocumentWorker;
    private final BulkWorkerRuntime bulkWorkerRuntime;

    private final int maxBatchSize;
    private final long maxBatchTime;
    private final boolean processSubdocumentsSeparately;

    private final List<BulkDocumentTask> bulkDocumentTasks;
    private final List<Document> documentBatch;
    private boolean isBatchClosed;
    private long batchEndTime;

    public BulkDocumentMessageProcessor(
        final ApplicationImpl application,
        final BulkDocumentWorker bulkDocumentWorker,
        final BulkWorkerRuntime bulkWorkerRuntime
    )
    {
        this.application = application;
        this.bulkDocumentWorker = bulkDocumentWorker;
        this.bulkWorkerRuntime = bulkWorkerRuntime;

        final BatchSizeController batchSizeController = application.getBatchSizeController();
        this.maxBatchSize = batchSizeController.getMaximumBatchSize();
        this.maxBatchTime = batchSizeController.getMaximumBatchTime();

        final InputMessageProcessor inputMessageProcessor = application.getInputMessageProcessor();
        this.processSubdocumentsSeparately = inputMessageProcessor.getProcessSubdocumentsSeparately();

        this.bulkDocumentTasks = new ArrayList<>();
        this.documentBatch = new ArrayList<>();
        this.isBatchClosed = false;
        this.batchEndTime = 0;
    }

    public void processTasks() throws InterruptedException
    {
        // Create the object which encapsulates the batch of documents
        final DocumentsImpl documents = new DocumentsImpl(application);

        // Attempt to process the documents
        try {
            bulkDocumentWorker.processDocuments(documents);
        } catch (final DocumentWorkerTransientException dwte) {

            // Reject all the tasks in the batch
            final TaskRejectedException tre = new TaskRejectedException("Failed to process document", dwte);
            for (final BulkDocumentTask bulkDocumentTask : bulkDocumentTasks) {
                bulkDocumentTask.getWorkerTask().setResponse(tre);
            }

            // Exit the method - all documents have been rejected
            return;
        }

        // Cycle around the tasks and set RESULT_SUCCESS responses on them
        for (final BulkDocumentTask bulkDocumentTask : bulkDocumentTasks) {

            // Get the task object
            final AbstractTask documentWorkerTask = bulkDocumentTask.getDocumentWorkerTask();

            // Create the WorkerResponse object
            final WorkerResponse workerResponse = documentWorkerTask.createWorkerResponse();

            // Set the response on the WorkerTask object
            bulkDocumentTask.getWorkerTask().setResponse(workerResponse);
        }
    }

    private final class DocumentsImpl extends DocumentWorkerObjectImpl implements Documents
    {
        public DocumentsImpl(final ApplicationImpl application)
        {
            super(application);
        }

        @Override
        public void closeBatch()
        {
            isBatchClosed = true;
        }

        @Override
        public int currentSize()
        {
            return documentBatch.size();
        }

        @Override
        public boolean isBatchClosed()
        {
            return isBatchClosed;
        }

        @Nonnull
        @Override
        public Iterator<Document> iterator()
        {
            return new DocumentsIterator();
        }

        @Nonnull
        @Override
        public Stream<Document> stream()
        {
            return StreamSupport.stream(spliterator(), false);
        }
    }

    private final class DocumentsIterator implements Iterator<Document>
    {
        /**
         * Current position of the iterator
         */
        private int pos = 0;

        @Override
        public boolean hasNext()
        {
            final int currentBatchSize = documentBatch.size();

            // Just return true if we have already retrieved the document at this position
            if (pos < currentBatchSize) {
                return true;
            }

            // If the batch is closed then don't try to add any more documents to it
            if (isBatchClosed) {
                return false;
            }

            // Get the next document for the batch
            final BulkDocumentTask bulkDocumentTask;
            if (currentBatchSize == 0) {
                // Get the first document of the batch within the batch timeframe
                final long initialEndTime = System.currentTimeMillis() + maxBatchTime;

                bulkDocumentTask = getNextBulkDocumentTask(initialEndTime);

                // Start the batch timer now that the first document has been retrieved
                batchEndTime = System.currentTimeMillis() + maxBatchTime;
            } else if (currentBatchSize >= maxBatchSize) {
                // The maximum batch size has been reached so don't attempt to retrieve any more documents
                bulkDocumentTask = null;
            } else {
                // Get the next document within the allowed timeframe
                bulkDocumentTask = getNextBulkDocumentTask(batchEndTime);
            }

            // If a document wasn't returned then close the batch
            if (bulkDocumentTask == null) {
                isBatchClosed = true;
                return false;
            }

            // Add the document task to the collection
            bulkDocumentTasks.add(bulkDocumentTask);

            // Add the documents in the task to the batch
            final Document rootDocument = bulkDocumentTask.getDocumentWorkerTask().getDocument();

            if (processSubdocumentsSeparately) {
                DocumentFunctions.documentNodes(rootDocument).forEachOrdered(documentBatch::add);
            } else {
                documentBatch.add(rootDocument);
            }

            // Return that there is another element which can be retrieved
            return true;
        }

        @Override
        public Document next()
        {
            // Check that we haven't reached the end of the batch
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            // Return the document at the current position and move the cursor on to the next position
            return documentBatch.get(pos++);
        }

        /**
         * Retrieves the next document task to be processed, or null if no task could be retrieved before the specified cut-off time.
         * <p>
         * If the thread is interrupted then it will return null immediately. Any invalid tasks encountered are skipped (after setting an
         * appropriate response on them).
         *
         * @param cutoffTime the cut-off time, specified in milliseconds since the Unix epoch
         * @return the next document to be processed
         */
        private BulkDocumentTask getNextBulkDocumentTask(final long cutoffTime)
        {
            // Get the next valid task (loop around if there are invalid messages)
            WorkerTask workerTask;
            AbstractTask documentWorkerTask;
            do {
                // Get the next worker task
                workerTask = getNextWorkerTask(cutoffTime);

                if (workerTask == null) {
                    return null;
                }

                // Confirm that the worker task is valid.
                // If it is not valid then set the response on it immediately and move on to the next one without counting it.
                documentWorkerTask = getValidDocumentWorkerTask(workerTask);

            } while (documentWorkerTask == null);

            // Create and return the new BulkDocumentTask object
            return new BulkDocumentTask(workerTask, documentWorkerTask);
        }

        /**
         * Retrieves the next task to be processed. If the next task cannot be retrieved before the specified cut-off time then null is
         * returned. If the thread is interrupted then it will return null immediately.
         *
         * @param cutoffTime the cut-off time, specified in milliseconds since the Unix epoch
         * @return the next task to be processed, or null if no task could be retrieved before the cut-off time
         */
        private WorkerTask getNextWorkerTask(final long cutoffTime)
        {
            final long maxWaitTime = cutoffTime - System.currentTimeMillis();

            try {
                return bulkWorkerRuntime.getNextWorkerTask(maxWaitTime);
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        /**
         * If the specified worker task is the correct type and version then the DocumentWorkerTask is de-serialised and returned. If
         * there is an issue with it then the appropriate response is set on the WorkerTask object and null is returned.
         *
         * @param workerTask the Worker Framework task to examine and extract the DocumentWorkerTask from
         * @return the decoded DocumentWorkerTask that was extracted from the WorkerTaskData object, or null if there was an error
         */
        private AbstractTask getValidDocumentWorkerTask(final WorkerTask workerTask)
        {
            Objects.requireNonNull(workerTask);

            try {
                return application.getInputMessageProcessor().createTask(workerTask);
            } catch (final InvalidTaskException ex) {
                workerTask.setResponse(ex);
                return null;
            } catch (final TaskRejectedException ex) {
                workerTask.setResponse(ex);
                return null;
            }
        }
    }
}
