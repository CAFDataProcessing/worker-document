/*
 * Copyright 2016-2024 Open Text.
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
package com.github.cafdataprocessing.worker.document.model;

import jakarta.annotation.Nonnull;

/**
 * Used for customizing the response to the document processing task.
 * <p>
 * Not all response options are applicible to all types of tasks.
 */
public interface Response extends DocumentWorkerObject
{
    /**
     * Returns an object which can be used to manipulate the custom data that will be sent as a part of the response.
     *
     * @return the object that can be used to access or update the custom data in the response
     */
    @Nonnull
    ResponseCustomData getCustomData();

    /**
     * Returns the queue that the response will be published to if any failures are newly added to the document or to any of its
     * subdocuments. Failures can be added to a document using the {@link Document#addFailure Document.addFailure()} method or one of the
     * {@link Failures#add Failures.add()} methods.
     *
     * @return the queue that will be used if the response contains failures
     */
    @Nonnull
    ResponseQueue getFailureQueue();

    /**
     * Returns the queue that the response will be published to if it does not contain any new failures.
     *
     * @return the queue that will be used if the response is successful
     */
    @Nonnull
    ResponseQueue getSuccessQueue();

    /**
     * Returns the task that this response customization object is associated with.
     *
     * @return the task that this response customization object is associated with
     */
    @Nonnull
    Task getTask();
}
