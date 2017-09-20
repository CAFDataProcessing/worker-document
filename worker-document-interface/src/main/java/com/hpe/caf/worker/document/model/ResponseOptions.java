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
package com.hpe.caf.worker.document.model;

import java.util.Map;

/**
 * Interface for additional worker response properties.
 */
public interface ResponseOptions extends DocumentWorkerObject
{
    /**
     * Gets the queue name that will be used when a worker sends response message.
     * @return The queue name.
     */
    String getQueueName();

    /**
     * Sets the queue name to use when a worker sends response message.
     * This queue will override queue name specified in the configuration.
     * @param queueName The queue name.
     */
    void setQueueName(String queueName);

    /**
     * Gets the custom data.
     * @return The custom data map which contains additional information for a document worker task.
     */
    Map<String, String> getCustomData();

    /**
     * Sets the custom data.
     * @param customData Custom data which contains additional information for a document worker task.
     */
    void setCustomData(Map<String, String> customData);

    /**
     * Returns a {@link Task} that is associated with this object.
     * @return a {@link Task} that is associated with this object.
     */
    Task getTask();
}
