package com.hpe.caf.worker.document.fieldvalues;

import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.model.Field;

public abstract class NonReferenceFieldValue extends AbstractFieldValue
{
    public NonReferenceFieldValue(final ApplicationImpl application, final Field field)
    {
        super(application, field);
    }

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
}
