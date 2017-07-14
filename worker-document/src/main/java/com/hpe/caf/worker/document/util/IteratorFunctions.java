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
package com.hpe.caf.worker.document.util;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Iterator-related utility functions.
 */
public final class IteratorFunctions
{
    /**
     * Overrides the default constructor to ensure that no instances of this class are created.
     */
    private IteratorFunctions()
    {
    }

    /**
     * Adapts the specified {@code Iterator} to a {@code Stream}.
     *
     * @param <T> the type of elements
     * @param iterator the {@code Iterator} to be wrapped
     * @return a {@code Stream} which can be used to iterate over the underlying collection
     */
    public static <T> Stream<T> asStream(final Iterator<T> iterator)
    {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(iterator, 0),
            false);
    }
}
