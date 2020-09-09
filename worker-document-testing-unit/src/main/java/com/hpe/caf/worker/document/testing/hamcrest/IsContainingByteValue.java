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
import com.hpe.caf.worker.document.model.FieldValue;
import java.util.Arrays;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

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
    protected byte[] getFieldValue(final FieldValue fieldValue)
    {
        return fieldValue.getValue();
    }

    @Override
    protected boolean isFieldValueOfExpectedType(FieldValue value)
    {
        return !value.isReference();
    }
}
