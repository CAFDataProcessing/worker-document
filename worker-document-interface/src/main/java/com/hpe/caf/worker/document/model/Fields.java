/*
 * Copyright 2016-2020 Micro Focus or one of its affiliates.
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
 * Represents the collection of fields that are attached to a document. Each field has a name and a collection of data values.
 */
public interface Fields extends DocumentWorkerObject, Iterable<Field>
{
    /**
     * Gets a field object for the specified field. This can be used to read the data values currently associated with the field, or to
     * add or replace the data values.
     *
     * @param fieldName the name of the field to access
     * @return the object that can be used to access or update the field
     */
    @Nonnull
    Field get(String fieldName);

    /**
     * Returns the document that this collection of fields is associated with.
     *
     * @return the document that this collection of fields is associated with
     */
    @Nonnull
    Document getDocument();

    /**
     * Resets this collection of fields back to its original state, undoing any changes made to any of the fields in the collection.
     */
    void reset();

    /**
     * Returns a sequential {@code Stream} with this field collection as its source.
     *
     * @return a sequential {@code Stream} over the collection of fields
     */
    @Nonnull
    Stream<Field> stream();
}
