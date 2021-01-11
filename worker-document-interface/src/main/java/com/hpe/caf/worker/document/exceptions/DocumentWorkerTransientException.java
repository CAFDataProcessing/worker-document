/*
 * Copyright 2016-2021 Micro Focus or one of its affiliates.
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

/**
 * A DocumentWorkerTransientException indicates that a transient failure has occurred; the operation might be able to succeed if it is
 * retried at a later time.
 */
public class DocumentWorkerTransientException extends Exception
{
    /**
     * Constructs a DocumentWorkerTransientException with a given reason.
     *
     * @param message a description of the exception
     */
    public DocumentWorkerTransientException(String message)
    {
        super(message);
    }

    /**
     * Constructs a DocumentWorkerTransientException with a given reason and cause.
     *
     * @param message a description of the exception
     * @param cause the underlying reason for this exception
     */
    public DocumentWorkerTransientException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a DocumentWorkerTransientException with a given cause.
     *
     * @param cause the underlying reason for this exception
     */
    public DocumentWorkerTransientException(Throwable cause)
    {
        super(cause);
    }
}
