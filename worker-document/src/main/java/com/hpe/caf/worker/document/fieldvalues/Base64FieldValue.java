package com.hpe.caf.worker.document.fieldvalues;

import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.model.Field;
import java.util.Objects;
import org.apache.commons.codec.binary.Base64;

public final class Base64FieldValue extends NonReferenceFieldValue
{
    private final byte[] data;

    public Base64FieldValue(final ApplicationImpl application, final Field field, final String data)
    {
        super(application, field);
        this.data = Base64.decodeBase64(Objects.requireNonNull(data));
    }

    @Override
    public byte[] getValue()
    {
        return data;
    }
}
