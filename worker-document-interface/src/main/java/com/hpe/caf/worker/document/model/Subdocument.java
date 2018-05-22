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
package com.hpe.caf.worker.document.model;

import javax.annotation.Nonnull;

/**
 * Represents a direct or indirect subdocument of the document being processed.
 * <p>
 * Subdocuments are documents in their own right and can also have fields, failures, and subdocuments of their own.
 */
public interface Subdocument extends Document
{
    /**
     * Returns the parent document of this subdocument.
     *
     * @return the document that is the parent of this subdocument
     */
    @Nonnull
    @Override
    Document getParentDocument();

    /**
     * Deletes this subdocument.
     * <p>
     * After a subdocument has been deleted the {@link #reset()} method may still be used to restore it to its original state, but other
     * methods which would query or manipulate the deleted document should not be called. Calling these methods will result in undefined
     * behavior.
     */
    void delete();

    /**
     * Returns true if the subdocument has been deleted.
     *
     * @return true if the subdocument has been deleted
     */
    boolean isDeleted();
}
