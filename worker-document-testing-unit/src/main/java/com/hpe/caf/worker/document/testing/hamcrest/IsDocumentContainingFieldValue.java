package com.hpe.caf.worker.document.testing.hamcrest;

import com.hpe.caf.worker.document.DocumentWorkerFieldEncoding;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.Field;
import com.hpe.caf.worker.document.model.FieldValue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Objects;

/**
 * Base Document field value matcher.
 *
 * @param <T> value match type
 */
public abstract class IsDocumentContainingFieldValue<T> extends TypeSafeDiagnosingMatcher<Document>
{
    private final String fieldName;
    private final Matcher<T> fieldValueMatcher;
    private final DocumentWorkerFieldEncoding encoding;

    public IsDocumentContainingFieldValue(
        final String fieldName,
        final Matcher<T> fieldValueMatcher,
        final DocumentWorkerFieldEncoding encoding
    )
    {
        this.fieldName = Objects.requireNonNull(fieldName);
        this.fieldValueMatcher = Objects.requireNonNull(fieldValueMatcher);
        this.encoding = Objects.requireNonNull(encoding);
    }

    @Override
    protected boolean matchesSafely(final Document document, final Description description)
    {
        final Field field = document.getField(fieldName);
        boolean isPastFirst = false;
        description.appendText("actual value(s) were ");
        for (final FieldValue value : field.getValues()) {

            final T fieldValue = getFieldValue(value, encoding);
            if (fieldValueMatcher.matches(fieldValue)) {
                return true;
            }

            if (isPastFirst) {
                description.appendText(", ");
            }
            describeActual(fieldValue, description);
            isPastFirst = true;
        }
        return false;
    }

    protected abstract void describeActual(final T fieldValue, Description description);

    protected abstract T getFieldValue(final FieldValue fieldValue, final DocumentWorkerFieldEncoding encoding);

    @Override
    public void describeTo(final Description description)
    {
        description.appendText("a document containing field ").appendText(fieldName).appendText(" with value ");
        fieldValueMatcher.describeTo(description);
    }
}
