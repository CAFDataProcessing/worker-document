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
package com.hpe.caf.worker.document.util;

import java.util.Map;

/**
 * Utility functions related to map collections.
 */
public final class MapFunctions
{
    /**
     * Overrides the default constructor to ensure that no instances of this class are created.
     */
    private MapFunctions()
    {
    }

    /**
     * Returns the specified map if is contains any key-value mappings, or {@code null} if the map is empty.
     *
     * @param <K> the type of keys maintained by this map
     * @param <V> the type of mapped values
     * @param map the map to be checked
     * @return the specified map or null if it is empty
     */
    public static <K, V> Map<K, V> emptyToNull(final Map<K, V> map)
    {
        return isNullOrEmpty(map) ? null : map;
    }

    /**
     * Returns true if the specified map is {@code null} or contains no key-value mappings.
     *
     * @param <K> the type of keys maintained by this map
     * @param <V> the type of mapped values
     * @param map the map to be checked
     * @return true if the map if null or contains no key-value mappings
     */
    public static <K, V> boolean isNullOrEmpty(final Map<K, V> map)
    {
        return map == null || map.isEmpty();
    }
}
