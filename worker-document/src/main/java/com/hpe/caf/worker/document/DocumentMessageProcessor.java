/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development LP.
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

import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.extensibility.DocumentWorker;
import com.hpe.caf.api.worker.InvalidTaskException;
import com.hpe.caf.api.worker.TaskRejectedException;
import com.hpe.caf.api.worker.TaskStatus;
import com.hpe.caf.api.worker.Worker;
import com.hpe.caf.api.worker.WorkerResponse;
import com.hpe.caf.api.worker.WorkerTaskData;
import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.impl.DocumentImpl;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.Subdocument;
import com.hpe.caf.worker.document.tasks.AbstractTask;
import java.nio.charset.StandardCharsets;

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
    public final WorkerResponse doWork() throws InterruptedException, TaskRejectedException, InvalidTaskException
    {
        // Retrieve the Document object
        final DocumentImpl document = documentWorkerTask.getDocument();

        // Process the document
        try {
            processDocument(document);
        } catch (DocumentWorkerTransientException dwte) {
            throw new TaskRejectedException("Failed to process document", dwte);
        } catch (final RuntimeException re) {
            documentWorkerTask.handleRuntimeException(re);
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
    public final WorkerResponse getGeneralFailureResult(Throwable t)
    {
        return new WorkerResponse(application.getFailureQueue(),
                                  TaskStatus.RESULT_EXCEPTION,
                                  getExceptionData(t),
                                  DocumentWorkerConstants.WORKER_NAME,
                                  DocumentWorkerConstants.WORKER_API_VER,
                                  null);
    }

    /**
     * Calls the implementation's {@link DocumentWorker#processDocument processDocument()} function with the specified document. If
     * subdocuments are to be processed separately then the function is also called for each of the subdocuments in the document's
     * hierarchy.
     */
    private void processDocument(final Document document) throws DocumentWorkerTransientException, InterruptedException
    {
        final boolean processSubdocumentsSeparately
            = application.getInputMessageProcessor().getProcessSubdocumentsSeparately();

        if (processSubdocumentsSeparately) {
            processDocumentHierarchy(document);
        } else {
            documentWorker.processDocument(document);
        }
    }

    /**
     * Calls the implementation's {@link DocumentWorker#processDocument processDocument()} function for both the specified document and
     * for all of the documents in its hierarchy.
     */
    private void processDocumentHierarchy(final Document document) throws DocumentWorkerTransientException, InterruptedException
    {
        documentWorker.processDocument(document);

        for (final Subdocument subdocument : document.getSubdocuments()) {
            processDocumentHierarchy(subdocument);
        }
    }

    /**
     * Returns the details of the exception in a UTF-8 encoded byte array.
     *
     * @param t the Throwable from the Worker
     * @return a byte array that contains the exception details
     */
    private static byte[] getExceptionData(final Throwable t)
    {
        final String exceptionString = getExceptionStackTrace(t);

        return exceptionString.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Builds up a stack trace with one level of cause stack trace
     *
     * @param t the exception to build a stack trace from
     * @return stack trace constructed from exception
     */
    private static String getExceptionStackTrace(final Throwable t)
    {
        // Build up exception detail from stack trace
        final StringBuilder sb = new StringBuilder();
        appendExceptionDetail(sb, t);

        // If a cause exists add it to the exception detail
        final Throwable cause = t.getCause();

        if (cause != null) {
            sb.append(". Cause: ");
            appendExceptionDetail(sb, cause);
        }

        // Return the final string
        return sb.toString();
    }

    /**
     * This function appends the details of the specified exception to the specified StringBuilder.
     *
     * @param sb the StringBuilder object to append to
     * @param t the exception to append
     */
    private static void appendExceptionDetail(final StringBuilder sb, final Throwable t)
    {
        // Append the exception class and message
        sb.append(t.getClass()).append(' ').append(t.getMessage());

        // Append the stack trace if there is one
        final StackTraceElement[] stackTrace = t.getStackTrace();
        if (stackTrace != null) {
            for (StackTraceElement stackTraceElement : stackTrace) {
                sb.append(' ').append(stackTraceElement);
            }
        }
    }
}
