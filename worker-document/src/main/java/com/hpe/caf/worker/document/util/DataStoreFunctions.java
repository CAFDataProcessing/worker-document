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
package com.hpe.caf.worker.document.util;

import com.hpe.caf.api.worker.DataStore;
import com.hpe.caf.api.worker.DataStoreException;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;

/**
 * DataStore-related utility functions.
 */
public final class DataStoreFunctions
{
    /**
     * Overrides the default constructor to ensure that no instances of this class are created.
     */
    private DataStoreFunctions()
    {
    }

    /**
     * Opens an InputStream for reading data from a remote data store.
     *
     * @param dataStore the remote data store that the data is to be read from
     * @param reference the reference to the data within the remote data store
     * @return a new InputStream which can be used for reading the data
     * @throws IOException if the data cannot be opened for reading
     */
    @Nonnull
    public static InputStream openInputStream(final DataStore dataStore, final String reference) throws IOException
    {
        try {
            return dataStore.retrieve(reference);
        } catch (final DataStoreException ex) {
            throw new IOException(ex);
        }
    }
}
