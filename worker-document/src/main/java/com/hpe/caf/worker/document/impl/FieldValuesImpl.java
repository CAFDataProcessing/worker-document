package com.hpe.caf.worker.document.impl;

import com.hpe.caf.worker.document.DocumentWorkerFieldValue;
import com.hpe.caf.worker.document.fieldvalues.AbstractFieldValue;
import com.hpe.caf.worker.document.model.Field;
import com.hpe.caf.worker.document.model.FieldValue;
import com.hpe.caf.worker.document.model.FieldValues;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class FieldValuesImpl extends DocumentWorkerObjectImpl implements FieldValues
{
    private final Field field;
    private final List<FieldValue> fieldValues;

    public FieldValuesImpl(final ApplicationImpl application, final Field field, final List<DocumentWorkerFieldValue> fieldValues)
    {
        super(application);
        this.field = Objects.requireNonNull(field);
        this.fieldValues = createFieldValueList(application, field, fieldValues);
    }

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

    @Override
    public Stream<FieldValue> stream()
    {
        return fieldValues.stream();
    }

    private static List<FieldValue> createFieldValueList(
        final ApplicationImpl application,
        final Field field,
        final List<DocumentWorkerFieldValue> fieldValues
    )
    {
        final List<FieldValue> returnList = new ArrayList<>(fieldValues.size());

        for (final DocumentWorkerFieldValue fieldValue : fieldValues) {
            returnList.add(AbstractFieldValue.create(application, field, fieldValue));
        }

        return Collections.unmodifiableList(returnList);
    }
}
