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

import javax.annotation.Nonnull;

/**
 * Represents one of the data values of a field.
 */
public interface FieldValue extends DocumentWorkerObject
{
    /**
     * Returns the field that this field value is associated with.
     *
     * @return the field that this field value is associated with
     */
    @Nonnull
    Field getField();

    /**
     * Decodes the data assuming that it is a UTF-8 encoded string and returns it. The {@link #isReference()} call should be used before
     * calling this method because it throws a RuntimeException if this field value is a reference. A replacement character will be used
     * for sequences which are not valid UTF-8.
     *
     * @return the field value data as a string
     * @throws RuntimeException if this field value is a reference
     */
    @Nonnull
    String getStringValue();

    /**
     * If this field value is a reference (i.e. if the data is actually stored in a remote data store) then this method retrieves the
     * remote data store reference. The {@link #isReference()} call should be used before calling this method because it throws a
     * RuntimeException if this field value is not a reference.
     *
     * @return the remote data store reference to the field value data
     * @throws RuntimeException if this field value is not a reference
     */
    @Nonnull
    String getReference();

    /**
     * Returns the data if the field value is not a reference (i.e. if the data is available without having to access a remote data
     * store). The {@link #isReference()} call should be used before calling this method because it throws a RuntimeException if this
     * field value is a reference.
     *
     * @return the field value data
     * @throws RuntimeException if this field value is a reference
     */
    @Nonnull
    byte[] getValue();

    /**
     * Returns true if the data is actually stored in the remote data store.
     *
     * @return true if the data is actually stored in the remote data store
     */
    boolean isReference();

    /**
     * Checks if the field value data is a valid UTF-8 encoded string. The {@link #isReference()} call should be used before calling this
     * method because it throws a RuntimeException if this field value is a reference.
     *
     * @return whether the field value data is a valid UTF-8 encoded string
     * @throws RuntimeException if this field value is a reference
     */
    boolean isStringValue();

    /**
     * TODO: All methods so far only work for either value or reference - we should have a getData() method that works regardless (i.e.
     * goes of to storage if it has to). Need to work out what the signature for such a method should be. It could be quite large so
     * should it return byte[], ByteArrayOutputStream, something else? Should it throw InteruptedException / IOException etc.?
     *
     * @return the actual data value
     */
    //(byte[] or what) getData() throws (what);
}
