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
package com.hpe.caf.worker.document.tasks;

import com.hpe.caf.api.worker.TaskStatus;
import com.hpe.caf.api.worker.WorkerResponse;
import com.hpe.caf.api.worker.WorkerTaskData;
import com.hpe.caf.worker.document.DocumentWorkerConstants;
import com.hpe.caf.worker.document.DocumentWorkerFailure;
import com.hpe.caf.worker.document.DocumentWorkerResult;
import com.hpe.caf.worker.document.DocumentWorkerTask;
import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.output.DocumentWorkerResultBuilder;
import com.hpe.caf.worker.document.views.ReadOnlyDocument;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class FieldEnrichmentTask extends AbstractTask
{
    @Nonnull
    public static FieldEnrichmentTask create(
        final ApplicationImpl application,
        final WorkerTaskData workerTask,
        final DocumentWorkerTask documentWorkerTask
    )
    {
        Objects.requireNonNull(documentWorkerTask);

        return new FieldEnrichmentTask(
            application,
            workerTask,
            ReadOnlyDocument.create(documentWorkerTask.fields),
            documentWorkerTask.customData);
    }

    private FieldEnrichmentTask(
        final ApplicationImpl application,
        final WorkerTaskData workerTask,
        final ReadOnlyDocument effectiveDocument,
        final Map<String, String> customData
    )
    {
        super(application, workerTask, effectiveDocument, customData);
    }

    @Override
    protected WorkerResponse createWorkerResponseImpl()
    {
        // Build the DocumentWorkerResult object
        final DocumentWorkerResultBuilder responseBuilder = new DocumentWorkerResultBuilder();
        document.recordChanges(responseBuilder);

        final DocumentWorkerResult documentWorkerResult = responseBuilder.toDocumentWorkerResult();

        // Select the output queue
        final String outputQueue = getOutputQueue(documentWorkerResult.failures);

        // Serialise the result object
        final byte[] data = application.serialiseResult(documentWorkerResult);

        // Create the WorkerResponse object
        return new WorkerResponse(outputQueue,
                                  TaskStatus.RESULT_SUCCESS,
                                  data,
                                  DocumentWorkerConstants.WORKER_NAME,
                                  DocumentWorkerConstants.WORKER_API_VER,
                                  null);
    }

    @Override
    protected WorkerResponse handleGeneralFailureImpl(final Throwable failure)
    {
        return new WorkerResponse(application.getFailureQueue(),
                                  TaskStatus.RESULT_EXCEPTION,
                                  getExceptionData(failure),
                                  DocumentWorkerConstants.WORKER_NAME,
                                  DocumentWorkerConstants.WORKER_API_VER,
                                  null);
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

    private String getOutputQueue(final List<DocumentWorkerFailure> failures)
    {
        return (failures == null || failures.isEmpty())
            ? application.getSuccessQueue()
            : application.getFailureQueue();
    }
}
