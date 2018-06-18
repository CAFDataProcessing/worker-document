/*
 * Copyright 2016-2018 Micro Focus or one of its affiliates.
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
package com.hpe.caf.worker.document.testing;

import java.util.Map;
import java.util.Objects;

/**
 * Document CustomData builder
 */
public class CustomDataBuilder
{
    private final Map<String, String> map;
    private final DocumentBuilder parentBuilder;

    public CustomDataBuilder(final Map<String, String> map, final DocumentBuilder parentBuilder)
    {
        this.map = Objects.requireNonNull(map);
        this.parentBuilder = parentBuilder;
    }

    /**
     * Add a new custom data
     *
     * @param name Custom data name
     * @param value Custom data value
     * @return This builder
     */
    public CustomDataBuilder add(final String name, final String value)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);

        map.put(name, value);
        return this;
    }

    /**
     * Goes back to the parent DocumentBuilder.
     *
     * @return DocumentBuilder.
     */
    public DocumentBuilder documentBuilder()
    {
        return parentBuilder;
    }
}
