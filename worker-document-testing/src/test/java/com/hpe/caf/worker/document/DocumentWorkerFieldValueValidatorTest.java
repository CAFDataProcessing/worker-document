/*
 * Copyright 2018-2017 EntIT Software LLC, a Micro Focus company.
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

import com.google.common.base.Strings;
import com.hpe.caf.api.Codec;
import com.hpe.caf.api.worker.DataStore;
import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.worker.testing.ContentFileTestExpectation;
import com.hpe.caf.worker.testing.TestConfiguration;
import com.hpe.caf.worker.testing.configuration.ValidationSettings;
import static com.hpe.caf.worker.testing.data.ContentComparisonType.BINARY;
import com.hpe.caf.worker.testing.validation.PropertyValidator;
import com.hpe.caf.worker.testing.validation.ValidatorFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

public class DocumentWorkerFieldValueValidatorTest
{
    private static final String VALIDATION_FAILURE_MESSAGE_REGEX = "(?s).*Validation of property .+ failed.*";

    @Test
    public void testStringFieldValueValid() throws Exception
    {
        testStringFieldValueValidation("A string value", "A string value");
    }

    @Test
    public void testNullStringFieldValueValid() throws Exception
    {
        testStringFieldValueValidation(null, "");
    }

    @Test(expectedExceptions = {AssertionError.class}, expectedExceptionsMessageRegExp = VALIDATION_FAILURE_MESSAGE_REGEX)
    public void testStringFieldValueInvalid() throws Exception
    {
        testStringFieldValueValidation("Another string value", "A different string value");
    }

    @Test
    public void testBase64FieldValueValid() throws IOException
    {
        testBase64FieldValueValidation("U2FtcGxlIHRvIGJlIGJhc2U2NCBlbmNvZGVk", "U2FtcGxlIHRvIGJlIGJhc2U2NCBlbmNvZGVk");
    }

    @Test
    public void testNullBase64FieldValueValid() throws IOException
    {
        testBase64FieldValueValidation(null, "");
    }

    @Test(expectedExceptions = {AssertionError.class}, expectedExceptionsMessageRegExp = VALIDATION_FAILURE_MESSAGE_REGEX)
    public void testBase64FieldValueInvalid() throws IOException
    {
        testBase64FieldValueValidation("QW5vdGhlciBzYW1wbGUgdG8gYmUgYmFzZTY0IGVuY29kZWQ",
                                       "QSBkaWZmZXJlbnQgc2FtcGxlIHRvIGJlIGJhc2U2NCBlbmNvZGVk");
    }

    @Test
    public void testReferenceFieldValueValid() throws DataStoreException, IOException
    {
        testReferenceFieldValueValidation("the content of a file in storage", "the content of a file in storage");
    }

    @Test(expectedExceptions = {AssertionError.class}, expectedExceptionsMessageRegExp = VALIDATION_FAILURE_MESSAGE_REGEX)
    public void testReferenceFieldValueInvalid() throws DataStoreException, IOException
    {
        testReferenceFieldValueValidation("other content of file in storage", "different content of a file in storage");
    }

    @Test
    public void testMixedEncodingsValid() throws DataStoreException, IOException
    {
        final String PROPERTY_NAME = "PropertyAcceptedByValidator";
        final String ACTUAL_STORAGE_REF = "dummy-storage-ref-actual";

        final DataStore dataStore = mock(DataStore.class);
        when(dataStore.retrieve(ACTUAL_STORAGE_REF)).thenReturn(new ByteArrayInputStream("a string value".getBytes()));

        final DocumentWorkerFieldValue actualFieldValue = createStorageRefFieldValue(ACTUAL_STORAGE_REF);
        final DocumentWorkerFieldValueExpectation expectedFieldValue = convert(createStringFieldValue("a string value"));

        final PropertyValidator validator = getPropertyValidator(dataStore, PROPERTY_NAME, actualFieldValue, expectedFieldValue);

        assertTrue(validator instanceof DocumentWorkerFieldValueValidator,
                   "Expected a DocumentWorkerFieldValueValidator as we've configured a DocumentWorkerFieldValueValidator that accepts"
                   + "the name of the property being validated");
        validator.validate(PROPERTY_NAME, actualFieldValue, expectedFieldValue);
    }

    @Test
    public void testAnyStringFieldValidation() throws IOException
    {
        final DocumentWorkerFieldValue actualFieldValue = createStringFieldValue("a string value");
        final DocumentWorkerFieldValueExpectation expectedFieldValue = convert(createStringFieldValue("a string value"));

        final PropertyValidator validator = getPropertyValidator(mock(DataStore.class), null, actualFieldValue, expectedFieldValue);

        assertTrue(validator instanceof DocumentWorkerFieldValueValidator,
                   "Expected a DocumentWorkerFieldValueValidator as we've configured a DocumentWorkerFieldValueValidator that validates "
                   + "any StringFieldValue regardless of property name");
        validator.validate(null, actualFieldValue, expectedFieldValue);
    }

    private void testStringFieldValueValidation(final String actual, final String expected) throws IOException
    {
        final String PROPERTY_NAME = "PropertyAcceptedByValidator";

        final DocumentWorkerFieldValue actualFieldValue = createStringFieldValue(actual);
        final DocumentWorkerFieldValueExpectation expectedFieldValue = convert(createStringFieldValue(expected));

        final PropertyValidator validator
            = getPropertyValidator(mock(DataStore.class), PROPERTY_NAME, actualFieldValue, expectedFieldValue);

        assertTrue(validator instanceof DocumentWorkerFieldValueValidator,
                   "Expected a DocumentWorkerFieldValueValidator as we've configured a DocumentWorkerFieldValueValidator that accepts"
                   + "the name of the property being validated");
        validator.validate(PROPERTY_NAME, actualFieldValue, expectedFieldValue);
    }

    private void testBase64FieldValueValidation(final String actual, final String expected) throws IOException
    {
        final String PROPERTY_NAME = "PropertyAcceptedByValidator";

        final DocumentWorkerFieldValue actualFieldValue = createBase64FieldValue(actual);
        final DocumentWorkerFieldValueExpectation expectedFieldValue = convert(createBase64FieldValue(expected));

        final PropertyValidator validator
            = getPropertyValidator(mock(DataStore.class), PROPERTY_NAME, actualFieldValue, expectedFieldValue);

        assertTrue(validator instanceof DocumentWorkerFieldValueValidator,
                   "Expected a DocumentWorkerFieldValueValidator as we've configured a DocumentWorkerFieldValueValidator that accepts"
                   + "the name of the property being validated");
        validator.validate(PROPERTY_NAME, actualFieldValue, expectedFieldValue);
    }

    private void testReferenceFieldValueValidation(final String actualStoredContent, final String expectedStoredContent)
        throws DataStoreException, IOException
    {
        final String PROPERTY_NAME = "PropertyAcceptedByValidator";
        final String ACTUAL_STORAGE_REF = "dummy-storage-ref-actual";

        final DataStore dataStore = mock(DataStore.class);
        when(dataStore.retrieve(ACTUAL_STORAGE_REF)).thenReturn(new ByteArrayInputStream(actualStoredContent.getBytes()));

        final DocumentWorkerFieldValue actualFieldValue = createStorageRefFieldValue(ACTUAL_STORAGE_REF);
        final DocumentWorkerFieldValueExpectation expectedFieldValue = convert(createStorageRefFieldValue(expectedStoredContent));

        final PropertyValidator validator = getPropertyValidator(dataStore, PROPERTY_NAME, actualFieldValue, expectedFieldValue);

        assertTrue(validator instanceof DocumentWorkerFieldValueValidator,
                   "Expected a DocumentWorkerFieldValueValidator as we've configured a DocumentWorkerFieldValueValidator that accepts"
                   + "the name of the property being validated");
        validator.validate(PROPERTY_NAME, actualFieldValue, expectedFieldValue);
    }

    private PropertyValidator getPropertyValidator(final DataStore dataStore,
                                                   final String propertyName,
                                                   final DocumentWorkerFieldValue actualFieldValue,
                                                   final DocumentWorkerFieldValueExpectation expectedFieldValue)
    {
        final DocumentWorkerFieldValueValidator validator = (propertyName == null)
            ? new DocumentWorkerFieldValueValidator(dataStore, mock(TestConfiguration.class), mock(Codec.class))
            : new DocumentWorkerFieldValueValidator(dataStore, mock(TestConfiguration.class), mock(Codec.class),
                                                    propertyName, "AnotherAcceptedPropertyName");

        final ValidationSettings validationSettings = ValidationSettings.configure()
            .customValidators(validator)
            .build();

        final ValidatorFactory validatorFactory
            = new ValidatorFactory(validationSettings, null, null, TestConfiguration.createDefault(null, null, null, null));

        return validatorFactory.create(propertyName, actualFieldValue, expectedFieldValue);
    }

    private DocumentWorkerFieldValue createStringFieldValue(final String stringValue)
    {
        final DocumentWorkerFieldValue fieldValue = new DocumentWorkerFieldValue();
        // Default encoding is utf8.
        fieldValue.data = stringValue;
        return fieldValue;
    }

    private DocumentWorkerFieldValue createBase64FieldValue(final String base64Value)
    {
        final DocumentWorkerFieldValue fieldValue = new DocumentWorkerFieldValue();
        fieldValue.encoding = DocumentWorkerFieldEncoding.base64;
        fieldValue.data = base64Value;
        return fieldValue;
    }

    private DocumentWorkerFieldValue createStorageRefFieldValue(final String storageRef)
    {
        final DocumentWorkerFieldValue fieldValue = new DocumentWorkerFieldValue();
        fieldValue.encoding = DocumentWorkerFieldEncoding.storage_ref;
        fieldValue.data = storageRef;
        return fieldValue;
    }

    private DocumentWorkerFieldValueExpectation convert(final DocumentWorkerFieldValue fieldValue) throws IOException
    {
        final DocumentWorkerFieldValueExpectation expectation = new DocumentWorkerFieldValueExpectation();
        expectation.data = fieldValue.data;
        expectation.encoding = fieldValue.encoding;
        if (fieldValue.encoding == DocumentWorkerFieldEncoding.storage_ref) {
            expectation.content = createContentExpectation(fieldValue.data);
        }
        return expectation;
    }

    private ContentFileTestExpectation createContentExpectation(final String content) throws IOException
    {
        final ContentFileTestExpectation contentExpectation = new ContentFileTestExpectation();
        contentExpectation.setExpectedContentFile(saveContentToTempFile(content));
        contentExpectation.setComparisonType(BINARY);
        contentExpectation.setExpectedSimilarityPercentage(100);
        return contentExpectation;
    }

    private String saveContentToTempFile(final String content) throws IOException
    {
        final File temp = File.createTempFile("contentExpectation_", ".tmp");
        temp.deleteOnExit();
        if (!Strings.isNullOrEmpty(content)) {
            try (FileWriter writer = new FileWriter(temp)) {
                writer.write(content);
            }
        }
        return temp.getAbsolutePath();
    }
}
