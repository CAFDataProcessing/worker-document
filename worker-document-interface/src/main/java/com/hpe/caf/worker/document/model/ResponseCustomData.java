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
package com.hpe.caf.worker.document.model;

import jakarta.annotation.Nonnull;
import java.util.Map;

/**
 * Represents the custom data that is sent as part of the response to the document processing task.
 */
public interface ResponseCustomData extends DocumentWorkerObject
{
    /**
     * Returns the response customization object that this custom data object is associated with.
     *
     * @return the response customization object that this custom data object is associated with
     */
    @Nonnull
    Response getResponse();

    /**
     * Used to retrieve any custom data that has been set on the response.
     *
     * @param dataKey the key of the data to be retrieved (note that the key lookup is case-sensitive)
     * @return the value which has been set, or null if no value has been set with the specified key
     */
    String get(String dataKey);

    /**
     * Used to set custom data to be sent with the response. The custom data could be used to send processing instructions to the
     * receiving worker, to affect how the document is processed by it.
     *
     * @param dataKey the key of the data to be sent with the response (note that the key is case-sensitive)
     * @param dataValue the value to be associated with the specified key
     * @return the previous value associated with the key, or {@code null} if there was no value previously associated with the key
     */
    String put(String dataKey, String dataValue);

    /**
     * Used to set custom data to be sent with the response. All of the mappings from the specified map are added to the response's custom
     * data. The effect of this call is equivalent to that of calling {@link #put(String, String) put(String, String)} for each mapping in
     * the specified map.
     *
     * @param m the mappings to be added to the custom data of the response
     */
    void putAll(Map<String, String> m);

    /**
     * Removes a custom data value that has been set on the response.
     *
     * @param dataKey the key of the data to be removed from the response (note that the key is case-sensitive)
     * @return the previous value associated with the key, or {@code null} if there was no value previously associated with the key
     */
    String remove(String dataKey);
}
