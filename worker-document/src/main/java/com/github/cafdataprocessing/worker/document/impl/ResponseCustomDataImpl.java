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
package com.github.cafdataprocessing.worker.document.impl;

import jakarta.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.github.cafdataprocessing.worker.document.model.ResponseCustomData;

public final class ResponseCustomDataImpl extends DocumentWorkerObjectImpl implements ResponseCustomData
{
    private final ResponseImpl response;
    private final HashMap<String, String> customDataMap;

    public ResponseCustomDataImpl(final ApplicationImpl application, final ResponseImpl response)
    {
        super(application);
        this.response = Objects.requireNonNull(response);
        this.customDataMap = new HashMap<>();
    }

    @Nonnull
    @Override
    public ResponseImpl getResponse()
    {
        return response;
    }

    @Override
    public String get(final String dataKey)
    {
        return customDataMap.get(dataKey);
    }

    @Override
    public String put(final String dataKey, final String dataValue)
    {
        return (dataValue == null)
            ? customDataMap.remove(dataKey)
            : customDataMap.put(dataKey, dataValue);
    }

    @Override
    public void putAll(final Map<String, String> m)
    {
        for (final Map.Entry<String, String> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String remove(final String dataKey)
    {
        return customDataMap.remove(dataKey);
    }

    @Nonnull
    public Map<String, String> asMap()
    {
        return Collections.unmodifiableMap(customDataMap);
    }
}
