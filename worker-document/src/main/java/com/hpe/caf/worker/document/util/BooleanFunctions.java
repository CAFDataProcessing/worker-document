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

/**
 * Boolean-related utility functions.
 */
public final class BooleanFunctions
{
    /**
     * Overrides the default constructor to ensure that no instances of this class are created.
     */
    private BooleanFunctions()
    {
    }

    /**
     * Returns the value of the specified {@link Boolean} if it is non-null, or the specified default value if it is null.
     *
     * @param bool the value to be tested
     * @param def a value to return if if bool has no value
     * @return the value of bool or the specified default if it is null
     */
    public static boolean valueOf(final Boolean bool, boolean def)
    {
        return (bool == null)
            ? def
            : bool;
    }
}
