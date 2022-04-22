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
package com.hpe.caf.worker.document.impl;

import com.hpe.caf.worker.document.converters.DocumentConverter;
import com.hpe.caf.worker.document.model.Subdocument;
import com.hpe.caf.worker.document.model.Subdocuments;
import com.hpe.caf.worker.document.output.ChangesJournal;
import com.hpe.caf.worker.document.views.ReadOnlyDocument;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;

public final class SubdocumentsImpl extends DocumentWorkerObjectImpl implements Subdocuments
{
    private final DocumentImpl document;

    private final List<ReadOnlyDocument> originalSubdocuments;

    private final SubdocumentImpl[] subdocuments;

    private final ArrayList<SubdocumentImpl> newSubdocuments;

    public SubdocumentsImpl(
        final ApplicationImpl application,
        final DocumentImpl document,
        final List<ReadOnlyDocument> originalSubdocuments
    )
    {
        super(application);
        this.document = Objects.requireNonNull(document);
        this.originalSubdocuments = Objects.requireNonNull(originalSubdocuments);
        this.subdocuments = new SubdocumentImpl[originalSubdocuments.size()];
        this.newSubdocuments = new ArrayList<>();
    }

    @Nonnull
    @Override
    public Subdocument add(final String reference)
    {
        final ReadOnlyDocument initialState = ReadOnlyDocument.create(reference);
        final SubdocumentImpl subdocument = new SubdocumentImpl(application, this, initialState);

        newSubdocuments.add(subdocument);

        return subdocument;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note: Currently this collection does not store which subdocuments are deleted. For this reason the implementation of this method
     * must work through each of the subdocuments and check their status rather than being able to directly go to the correct subdocument.
     * It's easy to imagine that this would not be appropriate if there were a large number of subdocuments. If that were ever to be the
     * case then we would have to implement a callback from the SubdocumentImpl class to create visibility here with regard to which
     * documents have been deleted.
     *
     * @param index index of the subdocument to return
     * @return the subdocument at the specified position
     */
    @Nonnull
    @Override
    public Subdocument get(final int index)
    {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }

        Integer pos = findValidIndexFrom(-subdocuments.length);

        for (int i = 0;; i++) {
            if (pos == null) {
                throw new IndexOutOfBoundsException();
            }

            if (i >= index) {
                break;
            }

            pos = findValidIndexFrom(pos + 1);
        }

        return retrieveSubdocument(pos);
    }

    @Nonnull
    @Override
    public DocumentImpl getDocument()
    {
        return document;
    }

    @Override
    public boolean isEmpty()
    {
        return Arrays.stream(subdocuments).allMatch(subdocument -> subdocument != null && subdocument.isDeleted())
            && newSubdocuments.stream().allMatch(subdocument -> subdocument.isDeleted());
    }

    @Nonnull
    @Override
    public Iterator<Subdocument> iterator()
    {
        return new SubdocumentIterator();
    }

    private final class SubdocumentIterator implements Iterator<Subdocument>
    {
        /**
         * Current position of the iterator, where negative positions are used for iterating the collection of original subdocuments and
         * positive ones are used for iterating through any new subdocuments added. Subdocuments which have been deleted are ignored.
         */
        private int pos;

        public SubdocumentIterator()
        {
            // Start at the first of the original subdocuments
            pos = -subdocuments.length;
        }

        @Override
        public boolean hasNext()
        {
            return findValidIndexFrom(pos) != null;
        }

        @Override
        public Subdocument next()
        {
            // Get the index of the next valid subdocument if there are any left
            final Integer index = findValidIndexFrom(pos);

            // Throw an exception if there aren't
            if (index == null) {
                throw new NoSuchElementException();
            }

            // Move the cursor on
            pos++;

            // Return the current subdocument
            return retrieveSubdocument(index);
        }
    }

    @Override
    public int size()
    {
        final long totalCount = Arrays.stream(subdocuments).filter(subdocument -> subdocument == null || !subdocument.isDeleted()).count()
            + newSubdocuments.stream().filter(subdocument -> !subdocument.isDeleted()).count();

        return (int) totalCount;
    }

    @Nonnull
    @Override
    public Stream<Subdocument> stream()
    {
        return StreamSupport.stream(spliterator(), false);
    }

    public boolean hasChanges()
    {
        return Arrays.stream(subdocuments).anyMatch(subdocument -> subdocument != null && subdocument.hasChanges())
            || newSubdocuments.stream().anyMatch(subdocument -> !subdocument.isDeleted());
    }

    public void reset()
    {
        for (final SubdocumentImpl subdocument : subdocuments) {
            if (subdocument != null) {
                subdocument.reset();
            }
        }

        for (final SubdocumentImpl subdocument : newSubdocuments) {
            // Note: I'm leaving it in the collection in case reset() ever gets called on it (effectively un-deleting it).
            // I suppose an alternative would be to throw a RuntimeException if reset() is called on a deleted document.
            subdocument.delete();
        }
    }

    public void recordChanges(final ChangesJournal journal)
    {
        // I've decided to cycle around the collection of subdocuments in reverse so as to remove any ambiguity with the indexes
        // (since removing a document causes the indexes of documents that follow it to change).
        for (int i = subdocuments.length - 1; i >= 0; i--) {
            final SubdocumentImpl subdocument = subdocuments[i];
            if (subdocument != null) {
                final String originalReference = subdocument.getOriginalReference();
                if (subdocument.isDeleted()) {
                    journal.removeSubdocument(i, originalReference);
                } else {
                    final ChangesJournal subdocumentJournal = journal.updateSubdocument(i, originalReference);
                    subdocument.recordChanges(subdocumentJournal);
                }
            }
        }

        // Add any new subdocuments
        newSubdocuments.stream()
            .filter(subdocument -> !subdocument.isDeleted())
            .map(DocumentConverter::convert)
            .forEach(journal::addSubdocument);
    }

    private Integer findValidIndexFrom(int pos)
    {
        for (; pos < 0; pos++) {
            final SubdocumentImpl subdocument = subdocuments[pos + subdocuments.length];
            if (subdocument == null || !subdocument.isDeleted()) {
                return pos;
            }
        }

        for (; pos < newSubdocuments.size(); pos++) {
            final SubdocumentImpl subdocument = newSubdocuments.get(pos);
            if (!subdocument.isDeleted()) {
                return pos;
            }
        }

        return null;
    }

    @Nonnull
    private SubdocumentImpl retrieveSubdocument(final int pos)
    {
        if (pos < 0) {
            final int index = pos + subdocuments.length;

            SubdocumentImpl subdocument = subdocuments[index];

            if (subdocument == null) {
                final ReadOnlyDocument initialState = originalSubdocuments.get(index);
                subdocument = new SubdocumentImpl(application, this, initialState);
                subdocuments[index] = subdocument;
            }

            return subdocument;
        } else {
            return newSubdocuments.get(pos);
        }
    }
}
