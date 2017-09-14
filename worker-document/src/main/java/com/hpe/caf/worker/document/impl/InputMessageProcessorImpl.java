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

import com.hpe.caf.api.Codec;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.DecodeMethod;
import com.hpe.caf.api.worker.InvalidTaskException;
import com.hpe.caf.api.worker.TaskRejectedException;
import com.hpe.caf.api.worker.WorkerTaskData;
import com.hpe.caf.worker.document.DocumentPostProcessorFactory;
import com.hpe.caf.worker.document.DocumentWorkerConstants;
import com.hpe.caf.worker.document.DocumentWorkerDocumentTask;
import com.hpe.caf.worker.document.DocumentWorkerTask;
import com.hpe.caf.worker.document.config.InputMessageConfiguration;
import com.hpe.caf.worker.document.exceptions.InvalidChangeLogException;
import com.hpe.caf.worker.document.model.InputMessageProcessor;
import com.hpe.caf.worker.document.tasks.AbstractTask;
import com.hpe.caf.worker.document.tasks.DocumentTask;
import com.hpe.caf.worker.document.tasks.FieldEnrichmentTask;
import com.hpe.caf.worker.document.util.BooleanFunctions;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputMessageProcessorImpl extends DocumentWorkerObjectImpl implements InputMessageProcessor
{
    private static final Logger LOG = LoggerFactory.getLogger(InputMessageProcessorImpl.class);

    private static final boolean DEFAULT_DOCUMENT_TASKS_ACCEPTED = true;
    private static final boolean DEFAULT_FIELD_ENRICHMENT_TASKS_ACCEPTED = true;
    private static final boolean DEFAULT_PROCESS_SUBDOCUMENTS_SEPARATELY = true;

    private final DocumentPostProcessorFactory postProcessorFactory;
    private boolean documentTasksAccepted;
    private boolean fieldEnrichmentTasksAccepted;
    private boolean processSubdocumentsSeparately;

    public InputMessageProcessorImpl(
        final ApplicationImpl application,
        final DocumentPostProcessorFactory postProcessorFactory,
        final InputMessageConfiguration configuration
    )
    {
        super(application);
        this.postProcessorFactory = postProcessorFactory;
        this.documentTasksAccepted = (configuration == null)
            ? DEFAULT_DOCUMENT_TASKS_ACCEPTED
            : BooleanFunctions.valueOf(configuration.getDocumentTasksAccepted(), DEFAULT_DOCUMENT_TASKS_ACCEPTED);

        this.fieldEnrichmentTasksAccepted = (configuration == null)
            ? DEFAULT_FIELD_ENRICHMENT_TASKS_ACCEPTED
            : BooleanFunctions.valueOf(configuration.getFieldEnrichmentTasksAccepted(), DEFAULT_FIELD_ENRICHMENT_TASKS_ACCEPTED);

        this.processSubdocumentsSeparately = (configuration == null)
            ? DEFAULT_PROCESS_SUBDOCUMENTS_SEPARATELY
            : BooleanFunctions.valueOf(configuration.getProcessSubdocumentsSeparately(), DEFAULT_PROCESS_SUBDOCUMENTS_SEPARATELY);
    }

    @Override
    public boolean getDocumentTasksAccepted()
    {
        return documentTasksAccepted;
    }

    @Override
    public boolean getFieldEnrichmentTasksAccepted()
    {
        return fieldEnrichmentTasksAccepted;
    }

    @Override
    public boolean getProcessSubdocumentsSeparately()
    {
        return processSubdocumentsSeparately;
    }

    @Override
    public void setDocumentTasksAccepted(final boolean accepted)
    {
        documentTasksAccepted = accepted;
    }

    @Override
    public void setFieldEnrichmentTasksAccepted(final boolean accepted)
    {
        fieldEnrichmentTasksAccepted = accepted;
    }

    @Override
    public void setProcessSubdocumentsSeparately(final boolean processSubdocumentsSeparately)
    {
        this.processSubdocumentsSeparately = processSubdocumentsSeparately;
    }

    /**
     * Verify that the specified worker task is supported and is a version that can be handled, and de-serialises it if it is.
     *
     * @param workerTask the Worker Framework task to interpret
     * @return a task object built from the data in the WorkerTaskData object
     * @throws InvalidTaskException if the task is invalid or is of a type that is not supported
     * @throws TaskRejectedException if the task cannot be processed at the minute, but another worker might be able to process it
     */
    @Nonnull
    public AbstractTask createTask(final WorkerTaskData workerTask)
        throws InvalidTaskException, TaskRejectedException
    {
        // Get the encoding scheme to use to decode the DocumentWorkerTask
        final Codec codec = application.getCodec();

        // Reject tasks of the wrong type and tasks that require a newer version
        final String workerName = DocumentWorkerConstants.WORKER_NAME;
        final String classifier = workerTask.getClassifier();

        if (fieldEnrichmentTasksAccepted && workerName.equals(classifier)) {
            final byte[] data = validateVersionAndData(workerTask, DocumentWorkerConstants.WORKER_API_VER);
            final DocumentWorkerTask documentWorkerTask
                = TaskValidator.deserialiseAndValidateTask(codec, DocumentWorkerTask.class, data);
            return FieldEnrichmentTask.create(application, workerTask, documentWorkerTask);
        } else if (documentTasksAccepted && DocumentWorkerConstants.DOCUMENT_TASK_NAME.equals(classifier)) {
            final byte[] data = validateVersionAndData(workerTask, DocumentWorkerConstants.DOCUMENT_TASK_API_VER);
            final DocumentWorkerDocumentTask documentWorkerDocumentTask
                = TaskValidator.deserialiseAndValidateTask(codec, DocumentWorkerDocumentTask.class, data);
            try {
                return DocumentTask.create(application, workerTask, documentWorkerDocumentTask, postProcessorFactory);
            } catch (InvalidChangeLogException ex) {
                throw new InvalidTaskException("Invalid change log", ex);
            }
        } else {
            throw new InvalidTaskException("Task of type " + classifier + " found on queue for " + workerName);
        }
    }

    @Nonnull
    private static byte[] validateVersionAndData(final WorkerTaskData workerTask, final int workerApiVersion)
        throws InvalidTaskException, TaskRejectedException
    {
        final int version = workerTask.getVersion();
        if (workerApiVersion < version) {
            throw new TaskRejectedException("Found task version " + version + ", which is newer than " + workerApiVersion);
        }

        final byte[] data = workerTask.getData();
        if (data == null) {
            throw new InvalidTaskException("Invalid input message: task not specified");
        }

        return data;
    }

    /**
     * The purpose of this static nested class is just to delay the creation of the validator object until it is required.
     */
    private static class TaskValidator
    {
        /**
         * Used to validate that the message being processed complies with the constraints that are declared.
         */
        private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        /**
         * Deserialise the given data into the specified class, and validate that any constraints specified have been met.
         */
        @Nonnull
        public static <T> T deserialiseAndValidateTask(final Codec codec, final Class<T> taskType, final byte[] data)
            throws InvalidTaskException
        {
            final T documentWorkerTask;
            try {
                documentWorkerTask = codec.deserialise(data, taskType, DecodeMethod.STRICT);
            } catch (CodecException e) {
                throw new InvalidTaskException("Invalid input message", e);
            }

            if (documentWorkerTask == null) {
                throw new InvalidTaskException("Invalid input message: no result from deserialisation");
            }

            final Set<ConstraintViolation<T>> violations = validator.validate(documentWorkerTask);
            if (violations.size() > 0) {
                LOG.error("Task of type {} failed validation due to: {}", taskType.getSimpleName(), violations);
                throw new InvalidTaskException("Task failed validation");
            }

            return documentWorkerTask;
        }
    }
}
