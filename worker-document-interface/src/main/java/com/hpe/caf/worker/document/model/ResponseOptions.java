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
 * Class containing additional worker response properties.
 * This class is immutable.
 */
public class ResponseOptions
{
    private final String queueName;
    private final Map<String, String> customData;

    /**#
     * Constructs the class.
     * @param queueName The queue name to be used in response.
     * @param customData The custom data to add to the response message.
     */
    public ResponseOptions(String queueName, Map<String, String> customData)
    {
        this.queueName = queueName;
        this.customData = customData;
    }

    /**
     * Gets the queue name.
     *
     * @return The queue name.
     */
    public String getQueueName()
    {
        return queueName;
    }

    /**
     * Gets the custom data.
     *
     * @return The custom data map.
     */
    public Map<String, String> getCustomData()
    {
        return customData;
    }
}
