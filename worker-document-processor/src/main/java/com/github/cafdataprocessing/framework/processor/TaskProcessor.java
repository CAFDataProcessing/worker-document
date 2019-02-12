/*
 * Copyright 2016-2019 Micro Focus or one of its affiliates.
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
package com.github.cafdataprocessing.framework.processor;

import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.extensibility.DocumentWorker;
import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.Subdocument;
import com.hpe.caf.worker.document.tasks.AbstractTask;
import javax.annotation.Nonnull;

/**
 * Uses a DocumentWorker implementation process a task message. Each instance processes just a single message.
 */
public final class TaskProcessor
{
    /**
     * Stores the global data that was initially passed to the DocumentWorkerProcessor when it was called. It effectively acts as a global
     * object for the worker.
     */
    private final ApplicationImpl application;

    /**
     * The actual implementation of the worker.
     */
    private final DocumentWorker documentWorker;

    /**
     * Contains the details from the task to be processed.
     */
    private final AbstractTask documentWorkerTask;

    /**
     * Constructs the TaskProcessor object, which is used to process a single task.
     *
     * @param application the global data for the worker
     * @param documentWorker the actual implementation of the worker
     * @param documentWorkerTask the task to the processed
     */
    TaskProcessor(
        final ApplicationImpl application,
        final DocumentWorker documentWorker,
        final AbstractTask documentWorkerTask
    )
    {
        this.application = application;
        this.documentWorker = documentWorker;
        this.documentWorkerTask = documentWorkerTask;
    }

    @Nonnull
    public final TaskResult process() throws DocumentWorkerTransientException, InterruptedException
    {
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
        }

        // Create the output message for the document
        return documentWorkerTask.createWorkerResponse();
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
