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
package com.hpe.caf.worker.document.fieldvalues;

import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.model.Field;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.annotation.Nonnull;

public abstract class NonReferenceFieldValue extends AbstractFieldValue
{
    public NonReferenceFieldValue(final ApplicationImpl application, final Field field)
    {
        super(application, field);
    }

    @Nonnull
    @Override
    public final String getReference()
    {
        throw new RuntimeException("The field value is not a remote reference.");
    }

    @Override
    public final boolean isReference()
    {
        return false;
    }

    @Nonnull
    @Override
    public InputStream openInputStream()
    {
        final byte[] data = getValue();

        return new ByteArrayInputStream(data);
    }
}
