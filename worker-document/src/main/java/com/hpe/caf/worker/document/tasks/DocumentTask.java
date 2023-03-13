/*
 * Copyright 2016-2023 Open Text.
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
import com.hpe.caf.worker.document.DocumentWorkerChange;
import com.hpe.caf.worker.document.DocumentWorkerChangeLogEntry;
import com.hpe.caf.worker.document.DocumentWorkerConstants;
import com.hpe.caf.worker.document.DocumentWorkerDocumentTask;
import com.hpe.caf.worker.document.DocumentWorkerScript;
import com.hpe.caf.worker.document.changelog.ChangeLogFunctions;
import com.hpe.caf.worker.document.changelog.MutableDocument;
import com.hpe.caf.worker.document.config.DocumentWorkerConfiguration;
import com.hpe.caf.worker.document.exceptions.InvalidChangeLogException;
import com.hpe.caf.worker.document.exceptions.InvalidScriptException;
import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.impl.ScriptImpl;
import com.hpe.caf.worker.document.output.ChangeLogBuilder;
import com.hpe.caf.worker.document.util.DocumentFunctions;
import com.hpe.caf.worker.document.util.ListFunctions;
import com.hpe.caf.worker.document.util.MapFunctions;
import com.hpe.caf.worker.document.views.ReadOnlyDocument;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public final class DocumentTask extends AbstractTask
{
    private final DocumentWorkerDocumentTask documentTask;

    @Nonnull
    public static DocumentTask create(
        final ApplicationImpl application,
        final WorkerTaskData workerTask,
        final DocumentWorkerDocumentTask documentTask
    ) throws InvalidChangeLogException, InvalidScriptException
    {
        Objects.requireNonNull(documentTask);

        return new DocumentTask(application, workerTask, documentTask);
    }

    private DocumentTask(
        final ApplicationImpl application,
        final WorkerTaskData workerTask,
        final DocumentWorkerDocumentTask documentTask
    ) throws InvalidChangeLogException, InvalidScriptException
    {
        super(application,
              workerTask,
              createEffectiveDocument(documentTask),
              documentTask.customData,
              documentTask.scripts);

        this.documentTask = documentTask;
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

    @Nonnull
    @Override
    protected WorkerResponse createWorkerResponseImpl()
    {
        // Build up the changes to add to the change log
        final ChangeLogBuilder changeLogBuilder = new ChangeLogBuilder();
        document.recordChanges(changeLogBuilder);

        final List<DocumentWorkerChange> changes = changeLogBuilder.getChanges();

        // Check if any of the changes include any new failures
        final boolean hasFailures = ChangeLogFunctions.hasFailures(changes);

        // Select the output queue
        final String outputQueue = response.getOutputQueue(hasFailures);

        // If there have been failures, then check if the framework is configured to return a RESULT_EXCEPTION rather than simply adding
        // them to the change log
        if (hasFailures && application.getConfiguration().getEnableExceptionOnFailure()) {

            final String failures = DocumentFunctions.documentNodes(document)
                .flatMap(d -> d.getFailures().stream())
                .map(f -> f.getFailureId() + ": " + f.getFailureMessage())
                .collect(Collectors.joining("\n"));

            return new WorkerResponse(outputQueue,
                                      TaskStatus.RESULT_EXCEPTION,
                                      failures.getBytes(StandardCharsets.UTF_8),
                                      "DocumentWorkerException",
                                      1,
                                      null);
        }

        // Create a new change log entry
        final DocumentWorkerChangeLogEntry changeLogEntry = new DocumentWorkerChangeLogEntry();
        changeLogEntry.name = getChangeLogEntryName();
        changeLogEntry.changes = changes.isEmpty() ? null : changes;

        // Put together the complete change log
        final ArrayList<DocumentWorkerChangeLogEntry> changeLog = ListFunctions.copy(documentTask.changeLog, 1);
        changeLog.add(changeLogEntry);

        // Get the installed scripts to include them in the response
        final List<DocumentWorkerScript> installedScripts = scripts.streamImpls()
            .filter(ScriptImpl::shouldIncludeInResponse)
            .map(ScriptImpl::toDocumentWorkerScript)
            .collect(Collectors.toList());

        // Construct the DocumentWorkerDocumentTask object
        final DocumentWorkerDocumentTask documentWorkerResult = new DocumentWorkerDocumentTask();
        documentWorkerResult.document = documentTask.document;
        documentWorkerResult.changeLog = changeLog;
        documentWorkerResult.customData = MapFunctions.emptyToNull(response.getCustomData().asMap());
        documentWorkerResult.scripts = ListFunctions.emptyToNull(installedScripts);

        // Serialise the result object
        final byte[] data = application.serialiseResult(documentWorkerResult);

        // If the response message includes any scripts then it is in the v2 message format
        // If any of the scripts have an engine specified then it is in the v3 message format
        final int resultMessageVersion;
        if (documentWorkerResult.scripts == null) {
            resultMessageVersion = 1;
        } else if (documentWorkerResult.scripts.stream().map(script -> script.engine).allMatch(Objects::isNull)) {
            resultMessageVersion = 2;
        } else {
            resultMessageVersion = 3;
        }

        // Create the WorkerResponse object
        return new WorkerResponse(outputQueue,
                                  TaskStatus.RESULT_SUCCESS,
                                  data,
                                  DocumentWorkerConstants.DOCUMENT_TASK_NAME,
                                  resultMessageVersion,
                                  null);
    }

    @Nonnull
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

    @Nonnull
    private String getChangeLogEntryName()
    {
        final DocumentWorkerConfiguration config = application.getConfiguration();
        final String changeLogEntryName = config.getWorkerName() + ":" + config.getWorkerVersion();

        return changeLogEntryName;
    }
}
