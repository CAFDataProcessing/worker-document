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

import com.hpe.caf.api.Codec;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.DecodeMethod;
import com.hpe.caf.api.worker.InvalidTaskException;
import com.hpe.caf.api.worker.TaskRejectedException;
import com.hpe.caf.api.worker.WorkerTaskData;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DocumentWorkerTaskFunctions
{
    /**
     * Used for logging.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DocumentWorkerTaskFunctions.class);

    /**
     * Used to validate that the message being processed complies with the DocumentWorkerTask constraints that are declared.
     */
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * Overrides the default constructor to ensure that no instances of this class are created.
     */
    private DocumentWorkerTaskFunctions()
    {
    }

    /**
     * Verify that the specified worker task has the right type and is a version that can be handled, and de-serialises it if it is.
     *
     * @param workerTask the Worker Framework task to examine and extract the DocumentWorkerTask from
     * @param codec the encoding scheme to use to decode the DocumentWorkerTask
     * @return the decoded DocumentWorkerTask that was extracted from the WorkerTaskData object
     * @throws InvalidTaskException if the task is invalid and could never be processed
     * @throws TaskRejectedException if the task cannot be processed at the minute, but another worker might be able to process it
     */
    public static DocumentWorkerTask getTask(final WorkerTaskData workerTask, final Codec codec)
        throws InvalidTaskException, TaskRejectedException
    {
        final DocumentWorkerTask documentWorkerTask = verifyWorkerTask(
            codec, workerTask.getClassifier(), workerTask.getVersion(), workerTask.getData());

        if (documentWorkerTask == null) {
            throw new InvalidTaskException("Invalid input message: no result from deserialisation");
        }

        final Set<ConstraintViolation<DocumentWorkerTask>> violations = validator.validate(documentWorkerTask);
        if (violations.size() > 0) {
            LOG.error("Task of type {} failed validation due to: {}", documentWorkerTask.getClass().getSimpleName(), violations);
            throw new InvalidTaskException("Task failed validation");
        }

        return documentWorkerTask;
    }

    /**
     * Verify that the specified worker task has the right type and is a version that can be handled.
     */
    private static DocumentWorkerTask verifyWorkerTask(
        final Codec codec,
        final String classifier,
        final int version,
        final byte[] data
    )
        throws TaskRejectedException, InvalidTaskException
    {
        // Reject tasks of the wrong type and tasks that require a newer version
        final String workerName = DocumentWorkerConstants.WORKER_NAME;
        if (!workerName.equals(classifier)) {
            throw new InvalidTaskException("Task of type " + classifier + " found on queue for " + workerName);
        }

        final int workerApiVersion = DocumentWorkerConstants.WORKER_API_VER;
        if (workerApiVersion < version) {
            throw new TaskRejectedException("Found task version " + version + ", which is newer than " + workerApiVersion);
        }

        if (data == null) {
            throw new InvalidTaskException("Invalid input message: task not specified");
        }

        try {
            return codec.deserialise(data, DocumentWorkerTask.class, DecodeMethod.STRICT);
        } catch (CodecException e) {
            throw new InvalidTaskException("Invalid input message", e);
        }
    }
}
