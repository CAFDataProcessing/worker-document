/*
 * Copyright 2016-2023 Open Text.
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

import jakarta.annotation.Nonnull;
import java.util.stream.Stream;

/**
 * Represents the list of subdocuments that are attached to a document.
 */
public interface Subdocuments extends DocumentWorkerObject, Iterable<Subdocument>
{
    /**
     * Creates a new subdocument and adds it to the collection.
     *
     * @param reference the reference that the subdocument should initially be assigned
     * @return the newly created subdocument
     */
    @Nonnull
    Subdocument add(String reference);

    /**
     * Retrieves the subdocument at the specified position.
     * <p>
     * Note that if a subdocument in the list is deleted that this will affect the positions of the subdocuments which follow it in the
     * subdocument list.
     *
     * @param index index of the subdocument to return
     * @return the subdocument at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= size())
     */
    @Nonnull
    Subdocument get(int index);

    /**
     * Returns the document that this collection of subdocuments is associated with.
     *
     * @return the document that this collection of subdocuments is associated with
     */
    @Nonnull
    Document getDocument();

    /**
     * Returns true if there are no subdocuments in this collection.
     *
     * @return true if there are no subdocuments in this collection
     */
    boolean isEmpty();

    /**
     * Returns the number of subdocuments in this collection.
     *
     * @return the number of subdocuments in this collection
     */
    int size();

    /**
     * Returns a sequential {@code Stream} with this subdocuments collection as its source.
     *
     * @return a sequential {@code Stream} over the collection of subdocuments
     */
    @Nonnull
    Stream<Subdocument> stream();
}
