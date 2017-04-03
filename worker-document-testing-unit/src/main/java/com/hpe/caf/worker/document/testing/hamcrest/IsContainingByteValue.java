package com.hpe.caf.worker.document.testing.hamcrest;

import com.hpe.caf.worker.document.DocumentWorkerFieldEncoding;
import com.hpe.caf.worker.document.model.FieldValue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Arrays;

/**
 * Document byte field value matcher
 */
public class IsContainingByteValue extends IsDocumentContainingFieldValue<byte[]>
{
    public IsContainingByteValue(
        final String fieldName,
        final Matcher<byte[]> fieldValueMatcher,
        final DocumentWorkerFieldEncoding encoding
    )
    {
        super(fieldName, fieldValueMatcher, encoding);
    }

    @Override
    protected void describeActual(final byte[] fieldValue, final Description description)
    {
        description.appendText(Arrays.toString(fieldValue));
    }

    @Override
    protected byte[] getFieldValue(final FieldValue fieldValue, final DocumentWorkerFieldEncoding encoding)
    {
        return fieldValue.getValue();
    }
}
