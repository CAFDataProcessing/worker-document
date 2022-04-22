/*
 * Copyright 2016-2022 Micro Focus or one of its affiliates.
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
package com.hpe.caf.worker.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.hpe.caf.api.Codec;
import com.hpe.caf.api.worker.DataStore;
import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.util.ref.ReferencedData;
import com.hpe.caf.worker.testing.*;
import com.hpe.caf.worker.testing.validation.CustomPropertyValidator;
import com.hpe.caf.worker.testing.validation.ReferenceDataValidator;
import com.hpe.caf.worker.testing.validation.ValuePropertyValidator;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.apache.commons.io.IOUtils;

/**
 * A property validator that validates individual DocumentWorkerFieldValue instances.
 */
public class DocumentWorkerFieldValueValidator extends CustomPropertyValidator
{
    private final Collection<String> recognizedPropertyNames;
    private final DataStore dataStore;
    protected final ReferenceDataValidator referencedDataValidator;
    protected final ValuePropertyValidator simpleValueValidator;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Constructs a validator that validates DocumentWorkerFieldValue instances of specific named fields
     *
     * @param dataStore the data store from which storage reference field values will be retrieved for validation
     * @param testConfiguration various settings controlling testing
     * @param codec the codec to be used when validating storage reference values
     * @param recognizedPropertyNames the names of the DocumentWorkerFieldValue fields that are validated by this validator instance; may
     * be null, in which case the validator may be used to validate any DocumentWorkerFieldValue field
     */
    public DocumentWorkerFieldValueValidator(final DataStore dataStore,
                                             final TestConfiguration testConfiguration,
                                             final Codec codec,
                                             final String... recognizedPropertyNames)
    {
        this.dataStore = Objects.requireNonNull(dataStore);
        this.referencedDataValidator = new ReferenceDataValidator(false,
                                                                  dataStore,
                                                                  codec,
                                                                  testConfiguration.getTestDataFolder(),
                                                                  testConfiguration.getTestSourcefileBaseFolder());
        this.simpleValueValidator = new ValuePropertyValidator();
        this.recognizedPropertyNames = Arrays.asList(recognizedPropertyNames);
        this.mapper.registerModule(new GuavaModule());
    }

    @Override
    public boolean canValidate(final String propertyName, final Object sourcePropertyValue, final Object validatorPropertyValue)
    {
        final boolean nameCheckPassed = recognizedPropertyNames.isEmpty()
            || (propertyName != null && recognizedPropertyNames.contains(propertyName));

        return nameCheckPassed
            && isDocumentWorkerFieldValue(sourcePropertyValue)
            && isDocumentWorkerFieldValueExpectation(validatorPropertyValue);
    }

    @Override
    protected boolean isValid(final Object testedPropertyValue, final Object validatorPropertyValue)
    {
        if (testedPropertyValue == null && validatorPropertyValue == null) {
            return true;
        }
        if (testedPropertyValue == null || validatorPropertyValue == null) {
            return false;
        }
        try {
            final DocumentWorkerFieldValue testedValue
                = convert(DocumentWorkerFieldValue.class, testedPropertyValue);
            final DocumentWorkerFieldValueExpectation validatorValue
                = convert(DocumentWorkerFieldValueExpectation.class, validatorPropertyValue);
            return testedValue != null && validatorValue != null && equal(testedValue, validatorValue);
        } catch (final DataStoreException | IOException e) {
            return false;
        }
    }

    private boolean isDocumentWorkerFieldValue(final Object value)
    {
        return convert(DocumentWorkerFieldValue.class, value) != null;
    }

    private boolean isDocumentWorkerFieldValueExpectation(final Object value)
    {
        return convert(DocumentWorkerFieldValueExpectation.class, value) != null;
    }

    private <T> T convert(final Class<T> type, final Object value)
    {
        if (type.isInstance(value)) {
            return (T) value;
        }
        if (value instanceof LinkedHashMap) {
            try {
                return mapper.convertValue(value, type);
            } catch (final IllegalArgumentException e) {
            }
        }
        return null;
    }

    /**
     * Compares a DocumentWorkerFieldValue with an expected value and indicates if they can be considered equal.
     *
     * @param actual the document worker field value to check.
     * @param expected expectation that the document worker field value should meet to be considered equal.
     * @return whether the actual field value matched the expectation.
     * @throws DataStoreException if there is a failure retrieving the field value from data store.
     * @throws IOException if there is a failure marshalling field value to bytes for comparison.
     */
    protected boolean equal(final DocumentWorkerFieldValue actual, final DocumentWorkerFieldValueExpectation expected)
        throws DataStoreException, IOException
    {
        final DocumentWorkerFieldEncoding expectedValueEncoding = nullToUtf8(expected.encoding);
        if (expectedValueEncoding == DocumentWorkerFieldEncoding.storage_ref) {
            return referencedDataValidator.isValid(getReferencedData(actual), expected.content);
        }
        final Object actualValue
            = nullToUtf8(actual.encoding) == expectedValueEncoding
            ? nullToEmpty(actual.data)
            : expectedValueEncoding == DocumentWorkerFieldEncoding.base64
                ? getBytes(actual)
                : new String(getBytes(actual));
        return simpleValueValidator.isValid(actualValue, nullToEmpty(expected.data));
    }

    /**
     * Retrieves ReferencedData object for data on provided DocumentWorkerFieldValue.
     *
     * @param actual DocumentWorkerFieldValue to get ReferencedData for.
     * @return ReferencedData representation of data on {@code actual}.
     * @throws DataStoreException if there is a failure retrieving data from data store when setting on ReferencedData object.
     * @throws IOException if there is a failure marshalling field value to bytes.
     */
    protected ReferencedData getReferencedData(final DocumentWorkerFieldValue actual) throws DataStoreException, IOException
    {
        return nullToUtf8(actual.encoding) == DocumentWorkerFieldEncoding.storage_ref
            ? ReferencedData.getReferencedData(nullToEmpty(actual.data))
            : ReferencedData.getWrappedData(getBytes(actual));
    }

    /**
     * Returns byte array representation of data on provided {@code value}.
     *
     * @param value field value to convert data for.
     * @return byte array representation of data on passed field value. If data was a storage reference the actual data will have been
     * retrieved from storage and converted to byte array.
     * @throws DataStoreException if there is a failure retrieving data from data store when setting on ReferencedData object.
     * @throws IOException if there is a failure marshalling field value to bytes.
     */
    protected byte[] getBytes(final DocumentWorkerFieldValue value) throws DataStoreException, IOException
    {
        return getBytes(value.encoding, value.data);
    }

    private byte[] getBytes(final DocumentWorkerFieldEncoding encoding, final String data) throws DataStoreException, IOException
    {
        final String nonNullData = nullToEmpty(data);
        switch (nullToUtf8(encoding)) {
            case storage_ref:
                return IOUtils.toByteArray(dataStore.retrieve(nonNullData));
            case base64:
                return Base64.getDecoder().decode(nonNullData);
            default: //utf8
                return nonNullData.getBytes(StandardCharsets.UTF_8);
        }
    }

    /**
     * Returns an empty {@code String} if {@code str} is null otherwise returns {@code str}.
     *
     * @param str value to check for null and potentially return.
     * @return empty string if {@code str} was null otherwise {@code str} unchanged.
     */
    protected static String nullToEmpty(final String str)
    {
        return (str != null) ? str : "";
    }

    /**
     * Returns encoding as utf8 if {@code encoding} provided is null otherwise returns {@code encoding}.
     *
     * @param encoding value to check for null and potentially return.
     * @return if {@code encoding} is null then {@code DocumentWorkerFieldEncoding.utf8} or {@code encoding} if it was not null.
     */
    protected static DocumentWorkerFieldEncoding nullToUtf8(final DocumentWorkerFieldEncoding encoding)
    {
        return (encoding != null) ? encoding : DocumentWorkerFieldEncoding.utf8;
    }
}
