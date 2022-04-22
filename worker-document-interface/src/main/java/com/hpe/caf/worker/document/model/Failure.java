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
package com.hpe.caf.worker.document.model;

import javax.annotation.Nonnull;

/**
 * Represents a failure attached to the document.
 */
public interface Failure extends DocumentWorkerObject
{
    /**
     * Returns the document that this failure is associated with.
     *
     * @return the document that this failure is associated with
     */
    @Nonnull
    Document getDocument();

    /**
     * Returns the non-localisable identifier related to the failure.
     *
     * @return the non-localisable identifier related to the failure
     */
    String getFailureId();

    /**
     * Returns a human readable message relating to the failure.
     *
     * @return a human readable message relating to the failure
     */
    String getFailureMessage();

    /**
     * Returns the stack trace relating to the failure, or {@code null} if it was not recorded.
     *
     * @return the stack trace relating to the failure
     */
    String getFailureStack();
}
