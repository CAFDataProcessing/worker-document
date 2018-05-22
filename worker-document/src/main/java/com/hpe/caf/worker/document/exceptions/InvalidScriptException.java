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
package com.hpe.caf.worker.document.exceptions;

import com.hpe.caf.worker.document.DocumentWorkerScript;
import javax.annotation.Nonnull;

public final class InvalidScriptException extends Exception
{
    private final DocumentWorkerScript invalidScript;

    public InvalidScriptException(final DocumentWorkerScript invalidScript, final String message)
    {
        super(getErrorMessage(invalidScript.name, message));
        this.invalidScript = invalidScript;
    }

    public InvalidScriptException(final DocumentWorkerScript invalidScript, final String message, final Throwable cause)
    {
        super(getErrorMessage(invalidScript.name, message), cause);
        this.invalidScript = invalidScript;
    }

    @Nonnull
    public DocumentWorkerScript getInvalidScript()
    {
        return invalidScript;
    }

    @Nonnull
    private static String getErrorMessage(final String scriptName, final String message)
    {
        final StringBuilder sb = new StringBuilder();

        // Preamble
        if (scriptName == null || scriptName.isEmpty()) {
            sb.append("Script error");
        } else {
            sb.append("Error in ").append(scriptName).append(" script");
        }

        // Add the error message
        if (message != null && !message.isEmpty()) {
            sb.append(": ");
            sb.append(message);
        }

        return sb.toString();
    }
}
