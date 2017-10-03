/*
 * Copyright 2015-2017 EntIT Software LLC, a Micro Focus company.
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
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.hamcrest.text.IsEqualIgnoringCase;

/**
 * Hamcrest Matchers for the Document object.
 *
 * @see com.hpe.caf.worker.document.model.Document
 */
public final class DocumentMatchers
{
    private DocumentMatchers()
    {
    }

    /**
     * Contains field with a string value.
     *
     * @param fieldName Field name to match
     * @param fieldValueMatcher Field value matcher
     * @return Matcher
     */
    public static IsContainingStringFieldValue containsStringFieldValue(final String fieldName, final Matcher<String> fieldValueMatcher)
    {
        return new IsContainingStringFieldValue(fieldName, fieldValueMatcher, DocumentWorkerFieldEncoding.utf8);
    }

    /**
     * Contains field with a string value.
     *
     * @param fieldName Field name to match
     * @param fieldValue Field value to match
     * @return Matcher
     */
    public static IsContainingStringFieldValue containsStringFieldValue(final String fieldName, final String fieldValue)
    {
        return containsStringFieldValue(fieldName, IsEqual.equalTo(fieldValue));
    }

    /**
     * Contains field with a string value ignoring case.
     *
     * @param fieldName Field name to match
     * @param fieldValue Field value to match
     * @return Matcher
     */
    public static IsContainingStringFieldValue containsStringFieldValueIgnoringCase(final String fieldName, final String fieldValue)
    {
        return containsStringFieldValue(fieldName, IsEqualIgnoringCase.equalToIgnoringCase(fieldValue));
    }

    /**
     * Contains field with a storage reference value.
     *
     * @param fieldName Field name to match
     * @param fieldValueMatcher Field value matcher
     * @return Matcher
     */
    public static IsContainingStringFieldValue containsReference(final String fieldName, final Matcher<String> fieldValueMatcher)
    {
        return new IsContainingStringFieldValue(fieldName, fieldValueMatcher, DocumentWorkerFieldEncoding.storage_ref);
    }

    /**
     * Contains field with a storage reference value.
     *
     * @param fieldName Field name to match
     * @param fieldValue Field value to match
     * @return Matcher
     */
    public static IsContainingStringFieldValue containsReference(final String fieldName, final String fieldValue)
    {
        return containsReference(fieldName, IsEqual.equalTo(fieldValue));
    }

    /**
     * Contains field with a binary value (encoded using Base64 internally).
     *
     * @param fieldName Field name to match
     * @param fieldValueMatcher Field value matcher.
     * @return Matcher
     */
    public static IsContainingByteValue containsByteValue(final String fieldName, final Matcher<byte[]> fieldValueMatcher)
    {
        return new IsContainingByteValue(fieldName, fieldValueMatcher, DocumentWorkerFieldEncoding.base64);
    }

    /**
     * Contains field with a binary value (encoded using Base64 internally).
     *
     * @param fieldName Field name to match
     * @param fieldValue Field value matcher.
     * @return Matcher
     */
    public static IsContainingByteValue containsByteValue(final String fieldName, final byte[] fieldValue)
    {
        return new IsContainingByteValue(fieldName, IsEqual.equalTo(fieldValue), DocumentWorkerFieldEncoding.base64);
    }
}
