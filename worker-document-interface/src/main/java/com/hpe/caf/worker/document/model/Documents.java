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

import java.util.stream.Stream;
import javax.annotation.Nonnull;

/**
 * Represents a batch of documents to be processed. Each document contains a set of fields and may contain some custom data with regard to
 * how it should be processed.
 */
public interface Documents extends DocumentWorkerObject, Iterable<Document>
{
    /**
     * Draws this batch of documents to a close.
     * <p>
     * Calling this method will result in no new documents being added to the batch.
     */
    void closeBatch();

    /**
     * Returns the number of documents that are currently in this batch.
     * <p>
     * This number will usually increase as the batch is iterated, until the batch is closed.
     *
     * @return the number of documents that are currently in this batch
     */
    int currentSize();

    /**
     * Returns whether or not this batch is closed.
     * <p>
     * Additional documents may continue to be added to an open batch. After a batch has been closed, either automatically by the
     * framework or manually by calling {@link #closeBatch()}, no additional documents will be added to it.
     *
     * @return true if this batch is closed
     */
    boolean isBatchClosed();

    /**
     * Returns a sequential {@code Stream} with this document batch as its source.
     *
     * @return a sequential {@code Stream} over the batch of documents
     */
    @Nonnull
    Stream<Document> stream();
}
