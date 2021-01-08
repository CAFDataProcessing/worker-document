/*
 * Copyright 2016-2021 Micro Focus or one of its affiliates.
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

import com.hpe.caf.api.worker.InvalidTaskException;
import com.hpe.caf.api.worker.TaskRejectedException;
import com.hpe.caf.api.worker.Worker;
import com.hpe.caf.api.worker.WorkerResponse;
import com.hpe.caf.api.worker.WorkerTaskData;
import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.extensibility.DocumentWorker;
import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.Subdocument;
import com.hpe.caf.worker.document.tasks.AbstractTask;

/**
 * Uses a DocumentWorker implementation process an messages. Each instance processes just a single message.
 */
public final class DocumentMessageProcessor implements Worker
{
    /**
     * Stores the global data that was initially passed to the WorkerFactoryProvider when it was called. It effectively acts as a global
     * object for the worker.
     */
    private final ApplicationImpl application;

    /**
     * The actual implementation of the worker.<p>
     * This class is adapting its interface so that it can be used with the Worker Framework.
     */
    private final DocumentWorker documentWorker;

    /**
     * Contains the details from the WorkerTaskData that are specific to Document Workers.
     */
    private final AbstractTask documentWorkerTask;

    /**
     * Constructs the DocumentMessageProcessor object, which is used to process a single worker message.
     *
     * @param application the global data for the worker
     * @param documentWorker the actual implementation of the worker
     * @param workerTask the task which the worker should perform
     * @throws TaskRejectedException if the task can't be handled right now but should be retried
     * @throws InvalidTaskException if there is something wrong with the task which means that if will always fail
     */
    public DocumentMessageProcessor(
        final ApplicationImpl application,
        final DocumentWorker documentWorker,
        final WorkerTaskData workerTask
    )
        throws TaskRejectedException, InvalidTaskException
    {
        this.application = application;
        this.documentWorker = documentWorker;
        this.documentWorkerTask = application.getInputMessageProcessor().createTask(workerTask);
    }

    @Override
    public final WorkerResponse doWork() throws InterruptedException, TaskRejectedException
    {
        try {
            try {
                // Load the customization scripts
                documentWorkerTask.loadScripts();

                // Process the task
                processTask();

            } catch (final RuntimeException ex) {

                // Raise the onError event
                final boolean handled = documentWorkerTask.raiseOnErrorEvent(ex);

                // Re-throw the exception if it was not handled
                if (!handled) {
                    throw ex;
                }
            } finally {
                // Unload the customization scripts
                documentWorkerTask.unloadScripts();
            }
        } catch (final DocumentWorkerTransientException dwte) {
            throw new TaskRejectedException("Failed to process document", dwte);
        }

        // Create a RESULT_SUCCESS for the document
        // (RESULT_SUCCESS is used at this level even if there are failures, as the failures have been successfully returned)
        return documentWorkerTask.createWorkerResponse();
    }

    @Override
    public final String getWorkerIdentifier()
    {
        return DocumentWorkerConstants.WORKER_NAME;
    }

    @Override
    public final int getWorkerApiVersion()
    {
        return DocumentWorkerConstants.WORKER_API_VER;
    }

    @Override
    public final WorkerResponse getGeneralFailureResult(final Throwable t)
    {
        return documentWorkerTask.handleGeneralFailure(t);
    }

    /**
     * Calls the implementation's {@link DocumentWorker#processDocument processDocument()} function with the specified document. If
     * subdocuments are to be processed separately then the function is also called for each of the subdocuments in the document's
     * hierarchy.
     */
    private void processTask() throws DocumentWorkerTransientException, InterruptedException
    {
        // Raise the onProcessTask event
        documentWorkerTask.raiseProcessTaskEvent();

        // Retrieve the document
        final Document document = documentWorkerTask.getDocument();

        // Process the document (or documents if subdocuments are being treated separately)
        final boolean processSubdocumentsSeparately
            = application.getInputMessageProcessor().getProcessSubdocumentsSeparately();

        if (processSubdocumentsSeparately) {
            processDocumentHierarchy(document);
        } else {
            processDocument(document);
        }

        // Raise the onAfterProcessTask event
        documentWorkerTask.raiseAfterProcessTaskEvent();
    }

    /**
     * Calls the implementation's {@link DocumentWorker#processDocument processDocument()} function for both the specified document and
     * for all of the documents in its hierarchy.
     */
    private void processDocumentHierarchy(final Document document) throws DocumentWorkerTransientException, InterruptedException
    {
        processDocument(document);

        for (final Subdocument subdocument : document.getSubdocuments()) {
            processDocumentHierarchy(subdocument);
        }
    }

    /**
     * Calls the customization scripts, and if none of them have set the cancellation flag then calls the implementation's
     * {@link DocumentWorker#processDocument processDocument()} function.
     */
    private void processDocument(final Document document) throws DocumentWorkerTransientException, InterruptedException
    {
        // Raise the onBeforeProcessDocument event and check the cancellation flag
        if (documentWorkerTask.raiseBeforeProcessDocumentEvent(document)) {
            return;
        }

        // Raise the onProcessDocument event
        documentWorkerTask.raiseProcessDocumentEvent(document);

        // Proceed to process the document
        documentWorker.processDocument(document);

        // Raise the onAfterProcessDocument event
        documentWorkerTask.raiseAfterProcessDocumentEvent(document);
    }
}
