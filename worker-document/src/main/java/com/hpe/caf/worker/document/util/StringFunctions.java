/*
 * Copyright 2018-2017 EntIT Software LLC, a Micro Focus company.
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
 * String-related utility functions.
 */
public final class StringFunctions
{
    /**
     * Overrides the default constructor to ensure that no instances of this class are created.
     */
    private StringFunctions()
    {
    }

    /**
     * Determines whether two specified String objects have the same value.
     *
     * @param s1 the first string to compare
     * @param s2 the second string to compare
     * @return true if the value of the first string is the same as the value of the second string; otherwise, false
     */
    public static boolean equals(final String s1, final String s2)
    {
        return (s1 == null)
            ? s2 == null
            : s1.equals(s2);
    }
}
