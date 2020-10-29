/*
 * Copyright 2016-2020 Micro Focus or one of its affiliates.
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

        // Cycle around the tasks and set the responses on them
        for (final BulkDocumentTask bulkDocumentTask : bulkDocumentTasks) {

            // Get the task objects
            final AbstractTask documentWorkerTask = bulkDocumentTask.getDocumentWorkerTask();
            final WorkerTask workerTask = bulkDocumentTask.getWorkerTask();

            try {
                // Raise the onAfterProcessDocument and onAfterProcessTask events
                for (final Document document : bulkDocumentTask.getDocuments()) {
                    documentWorkerTask.raiseAfterProcessDocumentEvent(document);
                }

                documentWorkerTask.raiseAfterProcessTaskEvent();

                // Unload the scripts
                documentWorkerTask.unloadScripts();

                // Create the WorkerResponse object
                final WorkerResponse workerResponse = documentWorkerTask.createWorkerResponse();

                // Set the response on the WorkerTask object
                workerTask.setResponse(workerResponse);
            } catch (final DocumentWorkerTransientException ex) {

                // Reject the task as a transient exception was thrown from one of its event handlers
                workerTask.setResponse(new TaskRejectedException("Failed to process task after scripts", ex));
            }
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

            // Try to add more documents to the batch if the batch size and time has not already been reached
            final boolean moreDocumentsAdded;
            if (currentBatchSize == 0) {
                // Add the first document to the batch within the batch timeframe
                final long initialEndTime = System.currentTimeMillis() + maxBatchTime;

                moreDocumentsAdded = tryAddMoreDocumentsToBatch(initialEndTime);

                // Start the batch timer now that the first document has been retrieved
                batchEndTime = System.currentTimeMillis() + maxBatchTime;
            } else if (currentBatchSize >= maxBatchSize) {
                // The maximum batch size has been reached so don't attempt to retrieve any more documents
                moreDocumentsAdded = false;
            } else {
                // Add the next document within the allowed timeframe
                moreDocumentsAdded = tryAddMoreDocumentsToBatch(batchEndTime);
            }

            // If a document wasn't returned then close the batch
            if (!moreDocumentsAdded) {
                isBatchClosed = true;
                return false;
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
         * Tries to add more documents to the batch, if that can be done before the specified cut-off time.
         * <p>
         * If the thread is interrupted then it will return false immediately.
         *
         * @param cutoffTime the cut-off time, specified in milliseconds since the Unix epoch
         * @return true if documents were successfully added to the batch; false if they were not
         */
        private boolean tryAddMoreDocumentsToBatch(final long cutoffTime)
        {
            for (;;) {
                // Get the next task to add to the batch
                final BulkDocumentTask bulkDocumentTask = getNextBulkDocumentTask(cutoffTime);

                // If a task hasn't been returned then return that no document could be added to the batch
                if (bulkDocumentTask == null) {
                    return false;
                }

                try {
                    // Load the task's customization scripts and raise its onProcessTask event
                    final AbstractTask task = bulkDocumentTask.getDocumentWorkerTask();
                    task.loadScripts();
                    task.raiseProcessTaskEvent();

                    // Get the documents from the task that should be added to the batch
                    final List<Document> documentsToAdd = getDocumentsToAddToBatch(task);

                    // Add the task to the collection
                    bulkDocumentTask.setDocuments(documentsToAdd);
                    bulkDocumentTasks.add(bulkDocumentTask);

                    // If there are documents to add to the batch then add them and return, otherwise try the next task
                    if (!documentsToAdd.isEmpty()) {
                        documentBatch.addAll(documentsToAdd);
                        return true;
                    }

                } catch (final DocumentWorkerTransientException ex) {

                    // Reject the task as a transient exception was thrown from one of its event handlers
                    bulkDocumentTask.getWorkerTask().setResponse(
                        new TaskRejectedException("Failed to process task before scripts", ex));

                    // Since a transient exception has occurred I'm going to close the batch rather than trying the next task
                    return false;

                } catch (final InterruptedException ex) {
                    // Since the thread has been interrupted I think the correct thing to do is to not set any response on the task at
                    // all. This is in line with what happens when an InterruptedException is throw from the worker's processDocuments()
                    // method.

                    // Reinterrupt the thread
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
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

    /**
     * Returns the documents from the specified task that should be added to the batch. The documents are returned in a prepared state
     * (i.e. the onBeforeProcessDocument and onProcessDocument events have already been raised for them).
     *
     * @param task the task that contains the documents to be added
     * @return the list of documents from the specified task that should be added to the batch
     * @throws DocumentWorkerTransientException if a transient issue has occurs when processing any of the event handlers
     * @throws InterruptedException if the thread is interrupted
     */
    private List<Document> getDocumentsToAddToBatch(final AbstractTask task)
        throws DocumentWorkerTransientException, InterruptedException
    {
        // Create a list to hold the documents that will be added to the batch
        final ArrayList<Document> documentsToAdd = new ArrayList<>();

        // Check whether the documents in the task are being processed separately
        if (processSubdocumentsSeparately) {
            // Get the root document
            final Document rootDocument = task.getDocument();

            // Cycle around all the documents in the task and try to add them to the list
            final Iterable<Document> allDocuments = DocumentFunctions.documentNodes(rootDocument)::iterator;

            for (final Document document : allDocuments) {
                if (prepareToAddDocumentToBatch(task, document)) {
                    documentsToAdd.add(document);
                }
            }
        } else {
            // Get the root document
            final Document rootDocument = task.getDocument();

            // Check whether it should be added to the batch
            if (prepareToAddDocumentToBatch(task, rootDocument)) {
                documentsToAdd.add(rootDocument);
            }
        }

        // Return the documents that should be added to the batch
        return documentsToAdd;
    }

    /**
     * Prepares the specified document to be added to the batch of documents. Returns false if it shouldn't be added.
     *
     * @param task the task that contains the document
     * @param document the document to be prepared
     * @return true if the document was successfully prepared and should be added to the batch; false if it should not be
     * @throws DocumentWorkerTransientException if the document could not be prepared due to a transient issue
     * @throws InterruptedException if the thread is interrupted
     */
    private static boolean prepareToAddDocumentToBatch(final AbstractTask task, final Document document)
        throws DocumentWorkerTransientException, InterruptedException
    {
        // Raise the onBeforeProcessDocument event and check the cancellation flag
        final boolean cancel = task.raiseBeforeProcessDocumentEvent(document);

        if (cancel) {
            return false;
        }

        // Raise onProcessDocument event
        task.raiseProcessDocumentEvent(document);

        // Return that the document can now be added to the batch
        return true;
    }
}
