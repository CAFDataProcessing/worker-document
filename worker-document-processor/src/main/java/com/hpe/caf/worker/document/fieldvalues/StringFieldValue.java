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
package com.hpe.caf.worker.document.fieldvalues;

import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.model.Field;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class StringFieldValue extends NonReferenceFieldValue
{
    private final String data;

    public StringFieldValue(final ApplicationImpl application, final Field field, final String data)
    {
        super(application, field);
        this.data = Objects.requireNonNull(data);
    }

    @Nonnull
    @Override
    public String getStringValue()
    {
        return data;
    }

    @Nonnull
    @Override
    public byte[] getValue()
    {
        return data.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public boolean isStringValue()
    {
        return true;
    }
}
