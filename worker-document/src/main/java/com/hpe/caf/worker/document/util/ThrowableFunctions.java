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
package com.hpe.caf.worker.document.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utility functions related to {@link Throwable} objects.
 */
public final class ThrowableFunctions
{
    /**
     * Overrides the default constructor to ensure that no instances of this class are created.
     */
    private ThrowableFunctions()
    {
    }

    /**
     * Returns the specified throwable and its backtrace as a string.
     *
     * @param cause the throwable whose backtrace is to be returned
     * @return the specified throwable and its backtrace
     */
    public static String getStackTrace(final Throwable cause)
    {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);

        cause.printStackTrace(pw);

        return sw.toString();
    }
}
