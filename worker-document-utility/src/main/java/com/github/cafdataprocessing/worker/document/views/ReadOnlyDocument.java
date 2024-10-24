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
package com.github.cafdataprocessing.worker.document.views;

import com.github.cafdataprocessing.worker.document.DocumentWorkerDocument;
import com.github.cafdataprocessing.worker.document.DocumentWorkerFieldValue;
import com.github.cafdataprocessing.worker.document.changelog.MutableDocument;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Provides a read-only, non-null, view of an underlying document object. This can be used when the underlying document object must not be
 * changed. It can be passed around in lieu of the document object to allow it to be read without the danger of it being manipulated.
 * Additionally it also makes the underlying document easier to read, as it translates nulls into empty sets or lists where possible.
 */
public final class ReadOnlyDocument
{
    private final String reference;
    private final Map<String, List<ReadOnlyFieldValue>> fields;
    private final List<ReadOnlyFailure> failures;
    private final List<ReadOnlyDocument> subdocuments;

    /**
     * Constructs a ReadOnlyDocument object which provides read access to the specified fields.
     * <p>
     * <b>Note: </b>The specified fields must not be changed after this object is constructed.
     *
     * @param fields the map of fields to wrap
     * @return the new read-only document object
     */
    @Nonnull
    public static ReadOnlyDocument create(final Map<String, List<DocumentWorkerFieldValue>> fields)
    {
        return new ReadOnlyDocument(
            null,
            ReadOnlyFields.create(fields),
            ReadOnlyFailures.none(),
            ReadOnlyDocuments.none());
    }

    /**
     * Constructs a ReadOnlyDocument object which provides read access to the specified document.
     * <p>
     * <b>Note: </b>The specified document must not be changed after this object is constructed. If this constraint is violated then there
     * will be undefined results. What is presented may contain elements of both the original and current document.
     *
     * @param document the document to wrap
     * @return the new read-only document object
     */
    @Nonnull
    public static ReadOnlyDocument create(final DocumentWorkerDocument document)
    {
        if (document == null) {
            return new ReadOnlyDocument(
                null,
                ReadOnlyFields.none(),
                ReadOnlyFailures.none(),
                ReadOnlyDocuments.none());
        } else {
            return new ReadOnlyDocument(
                document.reference,
                ReadOnlyFields.create(document.fields),
                ReadOnlyFailures.create(document.failures),
                ReadOnlyDocuments.create(document.subdocuments));
        }
    }

    /**
     * Constructs a ReadOnlyDocument object which provides read access to the specified document.
     * <p>
     * <b>Note: </b>The specified document must not be changed after this object is constructed. If this constraint is violated then there
     * will be undefined results. What is presented may contain elements of both the original and current document.
     *
     * @param document the document to wrap
     * @return the new read-only document object
     */
    @Nonnull
    public static ReadOnlyDocument create(final MutableDocument document)
    {
        Objects.requireNonNull(document);

        return new ReadOnlyDocument(
            document.getReference(),
            ReadOnlyFields.createFromMutable(document.getFields()),
            ReadOnlyFailures.create(document.getFailures()),
            ReadOnlyDocuments.create(document.getSubdocuments()));
    }

    /**
     * Constructs a ReadOnlyDocument object for document which just has a reference but does not have any fields or subdocuments.
     * <p>
     * This could be useful to represent the initial state of a newly created document.
     *
     * @param reference the reference that the document should have
     * @return the new read-only document object
     */
    @Nonnull
    public static ReadOnlyDocument create(final String reference)
    {
        return new ReadOnlyDocument(
            reference,
            ReadOnlyFields.none(),
            ReadOnlyFailures.none(),
            ReadOnlyDocuments.none());
    }

    private ReadOnlyDocument(
        final String reference,
        final Map<String, List<ReadOnlyFieldValue>> fields,
        final List<ReadOnlyFailure> failures,
        final List<ReadOnlyDocument> subdocuments
    )
    {
        this.reference = reference;
        this.fields = fields;
        this.failures = failures;
        this.subdocuments = subdocuments;
    }

    public String getReference()
    {
        return reference;
    }

    @Nonnull
    public Map<String, List<ReadOnlyFieldValue>> getFields()
    {
        return fields;
    }

    @Nonnull
    public List<ReadOnlyFailure> getFailures()
    {
        return failures;
    }

    @Nonnull
    public List<ReadOnlyDocument> getSubdocuments()
    {
        return subdocuments;
    }
}
