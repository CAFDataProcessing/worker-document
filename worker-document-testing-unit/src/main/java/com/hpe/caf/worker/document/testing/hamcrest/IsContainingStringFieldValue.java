/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development LP.
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
