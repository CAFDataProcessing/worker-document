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
package com.hpe.caf.worker.document.model;

import java.util.Map;
import javax.annotation.Nonnull;

/**
 * Used for customizing the response to the document processing task.
 * <p>
 * Not all response options are applicible to all types of tasks.
 */
public interface ResponseOptions extends DocumentWorkerObject
{
    /**
     * Returns the queue name set by {@link #setQueueNameOverride setQueueNameOverride()}, or {@code null} if
     * {@code setQueueNameOverride()} has not been used.
     * <p>
     * If {@code null} is returned then the default queue set in the configuration is used.
     *
     * @return the queue name set on this {@code ResponseOptions} object
     */
    String getQueueNameOverride();

    /**
     * Sets the name of the queue where the worker should send the response to this document processing task. This queue will override the
     * default queue name specified in the configuration. It is used even if the document contains failures.
     * <p>
     * Passing {@code null} to this method revokes any queue name override previously set and means that the response queue selection will
     * once again be based on the configuration.
     *
     * @param queueName the queue where the response message should be sent
     */
    void setQueueNameOverride(String queueName);

    /**
     * Returns the custom data that will be sent as a part of the response to this message. It is set using the
     * {@link #setCustomData setCustomData()} method.
     *
     * @return the custom data to be sent as part of the response
     */
    Map<String, String> getCustomData();

    /**
     * Sets the custom data which will be sent as part of the response to this document processing task.
     *
     * @param customData the custom data which will be sent with the response
     */
    void setCustomData(Map<String, String> customData);

    /**
     * Returns the task that this response customization object is associated with.
     *
     * @return the task that this response customization object is associated with
     */
    @Nonnull
    Task getTask();
}
