/*
 * Copyright 2016-2020 Micro Focus or one of its affiliates.
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
package com.hpe.caf.worker.document.testing.hamcrest;

import com.hpe.caf.worker.document.DocumentWorkerFieldEncoding;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.Field;
import com.hpe.caf.worker.document.model.FieldValue;
import java.util.Objects;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

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
            if (!isFieldValueOfExpectedType(value)) {
                continue;
            }

            final T fieldValue = getFieldValue(value);
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

    protected abstract void describeActual(T fieldValue, Description description);

    protected abstract T getFieldValue(FieldValue fieldValue);

    protected abstract boolean isFieldValueOfExpectedType(FieldValue value);

    @Override
    public void describeTo(final Description description)
    {
        description.appendText("a document containing field ").appendText(fieldName).appendText(" with value ");
        fieldValueMatcher.describeTo(description);
    }
}
