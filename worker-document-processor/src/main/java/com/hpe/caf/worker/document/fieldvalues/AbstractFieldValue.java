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

import com.hpe.caf.worker.document.DocumentWorkerFieldEncoding;
import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.impl.DocumentWorkerObjectImpl;
import com.hpe.caf.worker.document.model.Field;
import com.hpe.caf.worker.document.model.FieldValue;
import com.hpe.caf.worker.document.views.ReadOnlyFieldValue;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javax.annotation.Nonnull;

public abstract class AbstractFieldValue extends DocumentWorkerObjectImpl implements FieldValue
{
    /**
     * Constructs the appropriate implementation based on the encoding of the field value.
     *
     * @param application the global data for the worker
     * @param field the field that the field value is associated with
     * @param value the object that contains the field value
     * @return an object that can be used to interrogate the field value
     */
    @Nonnull
    public static AbstractFieldValue create(final ApplicationImpl application, final Field field, final ReadOnlyFieldValue value)
    {
        // Confirm that the arguments are not null
        Objects.requireNonNull(application);
        Objects.requireNonNull(value);

        // Deal with the possibility that the data or the encoding values might be null (by moving them to their defaults)
        final String data = value.getData();
        final DocumentWorkerFieldEncoding encoding = value.getEncoding();

        // Construct the appropriate concreate implementation
        switch (encoding) {
            case utf8:
                return new StringFieldValue(application, field, data);
            case base64:
                return new Base64FieldValue(application, field, data);
            case storage_ref:
                return new ReferenceFieldValue(application, field, data);
            default:
                throw new RuntimeException("Logical error: the encoding is not recognised");
        }
    }

    private final Field field;

    public AbstractFieldValue(final ApplicationImpl application, final Field field)
    {
        super(application);
        this.field = field;
    }

    @Nonnull
    @Override
    public Field getField()
    {
        return field;
    }

    @Nonnull
    @Override
    public String getStringValue()
    {
        final byte[] data = getValue();

        return new String(data, StandardCharsets.UTF_8);
    }

    @Override
    public boolean isStringValue()
    {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(getValue());
        final CharsetDecoder utfDecoder = StandardCharsets.UTF_8.newDecoder();

        try {
            utfDecoder.decode(byteBuffer);
            return true;
        } catch (final CharacterCodingException ex) {
            return false;
        }
    }
}
