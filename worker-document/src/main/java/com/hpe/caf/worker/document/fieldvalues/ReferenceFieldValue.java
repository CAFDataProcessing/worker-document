package com.hpe.caf.worker.document.fieldvalues;

import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.model.Field;
import java.util.Objects;

public final class ReferenceFieldValue extends AbstractFieldValue
{
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
        throw new RuntimeException("The field value is a remote reference.");
    }

    @Override
    public boolean isReference()
    {
        return true;
    }
}
