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
package com.hpe.caf.worker.document.impl;

import com.hpe.caf.worker.document.fieldvalues.AbstractFieldValue;
import com.hpe.caf.worker.document.model.Field;
import com.hpe.caf.worker.document.model.FieldValue;
import com.hpe.caf.worker.document.model.FieldValues;
import com.hpe.caf.worker.document.views.ReadOnlyFieldValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public final class FieldValuesImpl extends DocumentWorkerObjectImpl implements FieldValues
{
    private final Field field;
    private final List<FieldValue> fieldValues;

    public FieldValuesImpl(final ApplicationImpl application, final Field field, final List<ReadOnlyFieldValue> fieldValues)
    {
        super(application);
        this.field = Objects.requireNonNull(field);
        this.fieldValues = createFieldValueList(application, field, fieldValues);
    }

    @Nonnull
    @Override
    public Field getField()
    {
        return field;
    }

    @Override
    public boolean isEmpty()
    {
        return fieldValues.isEmpty();
    }

    @Nonnull
    @Override
    public Iterator<FieldValue> iterator()
    {
        return fieldValues.iterator();
    }

    @Override
    public int size()
    {
        return fieldValues.size();
    }

    @Nonnull
    @Override
    public Stream<FieldValue> stream()
    {
        return fieldValues.stream();
    }

    @Nonnull
    private static List<FieldValue> createFieldValueList(
        final ApplicationImpl application,
        final Field field,
        final List<ReadOnlyFieldValue> fieldValues
    )
    {
        final ArrayList<FieldValue> returnList = new ArrayList<>(fieldValues.size());

        for (final ReadOnlyFieldValue fieldValue : fieldValues) {
            returnList.add(AbstractFieldValue.create(application, field, fieldValue));
        }

        return Collections.unmodifiableList(returnList);
    }
}
