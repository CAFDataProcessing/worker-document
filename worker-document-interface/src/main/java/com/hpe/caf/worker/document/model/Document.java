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
package com.hpe.caf.worker.document.model;

import javax.annotation.Nonnull;

/**
 * Represents a document being processed.
 * <p>
 * The document has fields associated with it which may be manipulated when it is processed. Additionally it may also have custom data
 * associated with it. This custom data can be used to affect how the document is processed without actually becoming metadata of the
 * document.
 * <p>
 * If there is a failure processing the document then the failure information may also be stored with the document.
 */
public interface Document extends DocumentWorkerObject
{
    /**
     * Gets the list of fields currently associated with the document. This includes both fields which have been added to the document and
     * fields which have been removed from the document. As it includes fields which have been removed from the document you may find that
     * some of the fields have no values associated with them.
     *
     * @return an object which can be used to access the collection of fields
     */
    @Nonnull
    Fields getFields();

    /**
     * Gets a field object for the specified field. This can be used to read the data values currently associated with the field, or to
     * add or replace the data values.
     *
     * @param fieldName the name of the field to access
     * @return the object that can be used to access or update the field
     */
    @Nonnull
    Field getField(String fieldName);

    /**
     * Used to retrieve any custom data that was sent with the document, but which is not technically part of it.
     *
     * @param dataKey the key of the data to be retrieved (note that the key lookup is case-sensitive)
     * @return the value retrieved if it was sent, or null if no data was sent with the specified key
     */
    String getCustomData(String dataKey);

    /**
     * Records the specified failure on the document.
     *
     * @param failureId a non localisable identifier related to the failure
     * @param failureMessage a human readable message relating to the failure
     */
    void addFailure(String failureId, String failureMessage);
}
