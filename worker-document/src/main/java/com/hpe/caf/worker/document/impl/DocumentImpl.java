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
package com.hpe.caf.worker.document.impl;

import com.hpe.caf.api.worker.TaskStatus;
import com.hpe.caf.api.worker.WorkerResponse;
import com.hpe.caf.api.worker.WorkerTaskData;
import com.hpe.caf.worker.document.DocumentWorkerConstants;
import com.hpe.caf.worker.document.DocumentWorkerFailure;
import com.hpe.caf.worker.document.DocumentWorkerResult;
import com.hpe.caf.worker.document.DocumentWorkerResultFunctions;
import com.hpe.caf.worker.document.DocumentWorkerTask;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.Field;
import com.hpe.caf.worker.document.model.Fields;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class DocumentImpl extends DocumentWorkerObjectImpl implements Document
{
    private final DocumentWorkerTask documentWorkerTask;

    private final FieldsImpl fields;

    private List<DocumentWorkerFailure> failures;

    public DocumentImpl(
        final ApplicationImpl application,
        final WorkerTaskData workerTask,
        final DocumentWorkerTask documentWorkerTask
    )
    {
        super(application);
        Objects.requireNonNull(workerTask);
        this.documentWorkerTask = Objects.requireNonNull(documentWorkerTask);
        this.fields = new FieldsImpl(application, this);
        this.failures = null;
    }

    @Override
    public Fields getFields()
    {
        return fields;
    }

    @Override
    public Field getField(String fieldName)
    {
        return fields.get(fieldName);
    }

    @Override
    public String getCustomData(String dataKey)
    {
        final Map<String, String> customMap = documentWorkerTask.customData;
        if (customMap == null) {
            return null;
        }

        return customMap.get(dataKey);
    }

    @Override
    public void addFailure(String failureId, String failureMessage)
    {
        if (failureId == null && failureMessage == null) {
            return;
        }

        final DocumentWorkerFailure failure = new DocumentWorkerFailure();
        failure.failureId = failureId;
        failure.failureMessage = failureMessage;

        if (failures == null) {
            failures = new ArrayList<>(1);
        }

        failures.add(failure);
    }

    public DocumentWorkerTask getDocumentWorkerTask()
    {
        return documentWorkerTask;
    }

    public WorkerResponse createWorkerResponse()
    {
        // Construct the DocumentWorkerResult object
        final DocumentWorkerResult documentWorkerResult = new DocumentWorkerResult();
        documentWorkerResult.fieldChanges = fields.getChanges();
        documentWorkerResult.failures = failures;

        // Select the output queue
        final String outputQueue = (failures == null || failures.isEmpty())
            ? application.getSuccessQueue()
            : application.getFailureQueue();

        // Serialise the result object
        final byte[] data = DocumentWorkerResultFunctions.serialise(documentWorkerResult, application.getCodec());

        // Create the WorkerResponse object
        return new WorkerResponse(outputQueue,
                                  TaskStatus.RESULT_SUCCESS,
                                  data,
                                  DocumentWorkerConstants.WORKER_NAME,
                                  DocumentWorkerConstants.WORKER_API_VER,
                                  null);
    }
}
