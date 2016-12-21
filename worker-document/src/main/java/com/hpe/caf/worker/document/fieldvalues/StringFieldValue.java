package com.hpe.caf.worker.document.fieldvalues;

import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.model.Field;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class StringFieldValue extends NonReferenceFieldValue
{
    private final String data;

    public StringFieldValue(final ApplicationImpl application, final Field field, final String data)
    {
        super(application, field);
        this.data = Objects.requireNonNull(data);
    }

    @Override
    public String getStringValue()
    {
        return data;
    }

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
