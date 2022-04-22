/*
 * Copyright 2016-2022 Micro Focus or one of its affiliates.
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

/**
 * Object-related utility functions.
 */
public final class ObjectFunctions
{
    /**
     * Overrides the default constructor to ensure that no instances of this class are created.
     */
    private ObjectFunctions()
    {
    }

    /**
     * Returns the first parameter if it is not {@code null}, or else returns the second parameter.
     *
     * @param <T> the type of the objects
     * @param first the first parameter
     * @param second the second parameter
     * @return {@code first} if it is non-null; otherwise {@code second}
     */
    public static <T> T coalesce(final T first, final T second)
    {
        return (first != null) ? first : second;
    }
}
