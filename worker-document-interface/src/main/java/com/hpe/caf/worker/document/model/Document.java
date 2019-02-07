/*
 * Copyright 2016-2019 Micro Focus or one of its affiliates.
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
 * The document has a reference and fields associated with it which may be manipulated when it is processed. Additionally it may also have
 * custom data associated with it which can be used to affect how the document is processed without actually becoming metadata of the
 * document. Furthermore a document may have subdocuments which can also be manipulated when it is processed.
 * <p>
 * If there is a failure processing the document then the failure information may also be stored with the document.
 */
public interface Document extends DocumentWorkerObject
{
    /**
     * Returns the task that is associated with the document.
     *
     * @return the task that is associated with the document
     */
    @Nonnull
    Task getTask();

    /**
     * Returns the reference that is associated with the document.
     *
     * @return the reference that is associated with the document
     */
    String getReference();

    /**
     * Sets the reference that should be associated with the document.
     *
     * @param reference the reference to be associated with the document
     */
    void setReference(String reference);

    /**
     * Resets the reference back to its original state, undoing any changes made to it using the
     * {@link #setReference(String) setReference()} method.
     */
    void resetReference();

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
     * Gets the collection of failures that are currently associated with the document.
     *
     * @return an object which can be used to access the collection of failures
     */
    @Nonnull
    Failures getFailures();

    /**
     * Records the specified failure on the document.
     *
     * @param failureId a non-localisable identifier related to the failure
     * @param failureMessage a human readable message relating to the failure
     */
    void addFailure(String failureId, String failureMessage);

    /**
     * Returns the parent document of this document, or {@code null} if this document does not have a parent document.
     *
     * @return the document that is the parent of this document
     */
    Document getParentDocument();

    /**
     * Returns the root document of this document's hierarchy. If this document is the root document of the hierarchy then calling this
     * method will return itself.
     *
     * @return the root of the document hierarchy
     */
    @Nonnull
    Document getRootDocument();

    /**
     * Gets the list of subdocuments currently associated with the document.
     * <p>
     * Subdocuments are ordered and can be accessed by index. If a subdocument is deleted then it is immediately removed from the list,
     * causing the indexes of the subdocuments following it to be changed.
     *
     * @return an object which can be used to access the collection of subdocuments
     */
    @Nonnull
    Subdocuments getSubdocuments();

    /**
     * Returns true if this document currently has any child subdocuments.
     *
     * @return true if this document currently has any child subdocuments
     */
    boolean hasSubdocuments();

    /**
     * Returns true if the document has been modified from its original state.
     *
     * @return true if the document has been modified from its original state
     */
    boolean hasChanges();

    /**
     * Resets the document, and all of its subdocuments, back to its original state, undoing any modifications that have been made to the
     * document.
     */
    void reset();
}
