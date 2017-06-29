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
package com.hpe.caf.worker.document.fieldvalues;

import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.model.Field;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class ReferenceFieldValue extends AbstractFieldValue
{
    private static final Logger LOG = LoggerFactory.getLogger(ReferenceFieldValue.class);
    private final String data;

    public ReferenceFieldValue(final ApplicationImpl application, final Field field, final String data)
    {
        super(application, field);
        this.data = Objects.requireNonNull(data);
    }

    @Override
    public String getReference()
    {
        return data;
    }

    @Override
    public byte[] getValue()
    {
        try (InputStream retrieve = application.getDataStore().retrieve(data)) {
            return IOUtils.toByteArray(retrieve);
        }
        catch (DataStoreException | IOException e) {
            LOG.error("Failed to retrieve value from the data store.", e);
            throw new RuntimeException("The field value is a remote reference but retrieval of the data has failed. " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isReference()
    {
        return true;
    }
}
