package com.hpe.caf.worker.document.testing.hamcrest;

import com.hpe.caf.worker.document.DocumentWorkerFieldEncoding;
import com.hpe.caf.worker.document.model.FieldValue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Document string field value matcher.
 */
public class IsContainingStringFieldValue extends IsDocumentContainingFieldValue<String>
{
    public IsContainingStringFieldValue(
        final String fieldName,
        final Matcher<String> fieldValueMatcher,
        final DocumentWorkerFieldEncoding encoding
    )
    {
        super(fieldName, fieldValueMatcher, encoding);
    }

    @Override
    protected void describeActual(final String fieldValue, final Description description)
    {
        description.appendText(fieldValue);
    }

    @Override
    protected String getFieldValue(final FieldValue fieldValue, final DocumentWorkerFieldEncoding encoding)
    {
        if (encoding == DocumentWorkerFieldEncoding.storage_ref) {
            return fieldValue.getReference();
        }
        return fieldValue.getStringValue();
    }
}
