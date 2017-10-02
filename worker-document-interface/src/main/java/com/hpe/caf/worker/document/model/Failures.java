/*
 * Copyright 2015-2017 EntIT Software LLC, a Micro Focus company.
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

import java.util.stream.Stream;
import javax.annotation.Nonnull;

/**
 * Represents the collection of failures that are attached to a document. Each failure has a non-localisable identifier as well as a human
 * readable message and optionally a stack trace.
 */
public interface Failures extends DocumentWorkerObject, Iterable<Failure>
{
    /**
     * Adds the specified failure.
     *
     * @param failureId a non-localisable identifier related to the failure
     * @param failureMessage a human readable message relating to the failure
     */
    void add(String failureId, String failureMessage);

    /**
     * Adds the specified failure.
     *
     * @param failureId a non-localisable identifier related to the failure
     * @param failureMessage a human readable message relating to the failure
     * @param cause the cause of the failure
     */
    void add(String failureId, String failureMessage, Throwable cause);

    /**
     * Removes all the failures in this collection.
     */
    void clear();

    /**
     * Returns the document that this collection of failures is associated with.
     *
     * @return the document that this collection of failures is associated with
     */
    @Nonnull
    Document getDocument();

    /**
     * Returns true if this collection has been modified from its original state.
     *
     * @return true if this collection has been modified from its original state
     */
    boolean isChanged();

    /**
     * Returns true if there are no failures in this collection.
     *
     * @return true if there are no failures in this collection
     */
    boolean isEmpty();

    /**
     * Resets this collection of failures back to its original state, undoing any changes made to it using the add() or clear() methods.
     */
    void reset();

    /**
     * Returns the number of failures in this collection.
     *
     * @return the number of failures in this collection
     */
    int size();

    /**
     * Returns a sequential {@code Stream} with this failures collection as its source.
     *
     * @return a sequential {@code Stream} over the collection of failures
     */
    @Nonnull
    Stream<Failure> stream();
}
