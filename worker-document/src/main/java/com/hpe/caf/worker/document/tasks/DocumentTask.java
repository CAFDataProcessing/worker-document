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
package com.hpe.caf.worker.document.tasks;

import com.hpe.caf.api.worker.TaskRejectedException;
import com.hpe.caf.api.worker.TaskStatus;
import com.hpe.caf.api.worker.WorkerResponse;
import com.hpe.caf.api.worker.WorkerTaskData;
import com.hpe.caf.worker.document.DocumentPostProcessor;
import com.hpe.caf.worker.document.DocumentPostProcessorFactory;
import com.hpe.caf.worker.document.DocumentWorkerChange;
import com.hpe.caf.worker.document.DocumentWorkerChangeLogEntry;
import com.hpe.caf.worker.document.DocumentWorkerConstants;
import com.hpe.caf.worker.document.DocumentWorkerDocumentTask;
import com.hpe.caf.worker.document.changelog.ChangeLogFunctions;
import com.hpe.caf.worker.document.changelog.MutableDocument;
import com.hpe.caf.worker.document.config.DocumentWorkerConfiguration;
import com.hpe.caf.worker.document.exceptions.InvalidChangeLogException;
import com.hpe.caf.worker.document.exceptions.PostProcessingFailedException;
import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.model.ResponseOptions;
import com.hpe.caf.worker.document.output.ChangeLogBuilder;
import com.hpe.caf.worker.document.util.ListFunctions;
import com.hpe.caf.worker.document.views.ReadOnlyDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DocumentTask extends AbstractTask
{
    private static final Logger LOG = LoggerFactory.getLogger(DocumentTask.class);
    private final DocumentWorkerDocumentTask documentTask;
    private final DocumentPostProcessor postProcessor;

    @Nonnull
    public static DocumentTask create(
            final ApplicationImpl application,
            final WorkerTaskData workerTask,
            final DocumentWorkerDocumentTask documentTask,
            final DocumentPostProcessorFactory postProcessorFactory) throws InvalidChangeLogException, TaskRejectedException
    {
        Objects.requireNonNull(documentTask);

        return new DocumentTask(application, workerTask, documentTask, postProcessorFactory);
    }

    private DocumentTask(
            final ApplicationImpl application,
            final WorkerTaskData workerTask,
            final DocumentWorkerDocumentTask documentTask,
            DocumentPostProcessorFactory postProcessorFactory) throws InvalidChangeLogException, TaskRejectedException
    {
        super(application,
              workerTask,
              createEffectiveDocument(documentTask),
              documentTask.customData);

        this.documentTask = documentTask;
        this.postProcessor = postProcessorFactory.create(document);
    }

    @Nonnull
    private static ReadOnlyDocument createEffectiveDocument(final DocumentWorkerDocumentTask documentTask)
        throws InvalidChangeLogException
    {
        Objects.requireNonNull(documentTask);

        final ReadOnlyDocument baseDocument = ReadOnlyDocument.create(documentTask.document);

        final MutableDocument effectiveDocument = new MutableDocument(baseDocument);
        effectiveDocument.applyChangeLog(documentTask.changeLog);

        return ReadOnlyDocument.create(effectiveDocument);
    }

    @Override
    protected WorkerResponse createWorkerResponseImpl()
    {
        if (postProcessor != null) {
            LOG.trace("Post processor is not null - will execute.");
            try {
                postProcessor.postProcessDocument(document);
            }
            catch (PostProcessingFailedException e) {
                LOG.error("Failed to execute post-processing on a document.", e);
            }
        }

        // Build up the changes to add to the change log
        final ChangeLogBuilder changeLogBuilder = new ChangeLogBuilder();
        document.recordChanges(changeLogBuilder);

        final List<DocumentWorkerChange> changes = changeLogBuilder.getChanges();

        // Create a new change log entry
        final DocumentWorkerChangeLogEntry changeLogEntry = new DocumentWorkerChangeLogEntry();
        changeLogEntry.name = getChangeLogEntryName();
        changeLogEntry.changes = changes.isEmpty() ? null : changes;

        // Put together the complete change log
        final ArrayList<DocumentWorkerChangeLogEntry> changeLog = ListFunctions.copy(documentTask.changeLog, 1);
        changeLog.add(changeLogEntry);

        // Construct the DocumentWorkerDocumentTask object
        final DocumentWorkerDocumentTask documentWorkerResult = new DocumentWorkerDocumentTask();
        documentWorkerResult.document = documentTask.document;
        documentWorkerResult.changeLog = changeLog;

        ResponseOptions responseOptions = getResponseOptions();
        // Select the output queue
        // TODO: ResponseOptions queue name will override the queue set below. This means that failure queue will
        // not be used in case of failures. This is correct behaviour but we might want to add ability
        // to set a failure queue on response options.
        String outputQueue = ChangeLogFunctions.hasFailures(changes) ? application.getFailureQueue()
                : application.getSuccessQueue();

        if (responseOptions.getQueueName() != null) {
            outputQueue = responseOptions.getQueueName();
        }

        documentWorkerResult.customData = responseOptions.getCustomData();

        LOG.trace("Response queue name - {}", outputQueue);

        // Serialise the result object
        final byte[] data = application.serialiseResult(documentWorkerResult);

        // Create the WorkerResponse object
        return new WorkerResponse(outputQueue,
                                  TaskStatus.RESULT_SUCCESS,
                                  data,
                                  DocumentWorkerConstants.DOCUMENT_TASK_NAME,
                                  DocumentWorkerConstants.DOCUMENT_TASK_API_VER,
                                  null);
    }

    @Override
    protected WorkerResponse handleGeneralFailureImpl(final Throwable failure)
    {
        document.getFailures().add(failure.getClass().getName(),
                                   failure.getLocalizedMessage(),
                                   failure);
        // Create a RESULT_SUCCESS for the document
        // (RESULT_SUCCESS is used even if there are failures, as the failures are successfully returned)
        return this.createWorkerResponse();
    }

    private String getChangeLogEntryName()
    {
        final DocumentWorkerConfiguration config = application.getConfiguration();
        final String changeLogEntryName = config.getWorkerName() + ":" + config.getWorkerVersion();

        return changeLogEntryName;
    }
}
