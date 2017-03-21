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
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A property validator that validates individual DocumentWorkerFieldValue instances.
 */
public class DocumentWorkerFieldValueValidator extends CustomPropertyValidator {
    private final Collection<String> recognizedPropertyNames;
    private final DataStore dataStore;
    private final ReferenceDataValidator referencedDataValidator;
    private final ValuePropertyValidator simpleValueValidator;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Constructs a validator that validates DocumentWorkerFieldValue instances of specific named fields
     * @param dataStore the data store from which storage reference field values will be retrieved for validation
     * @param testConfiguration various settings controlling testing
     * @param codec the codec to be used when validating storage reference values
     * @param recognizedPropertyNames the names of the DocumentWorkerFieldValue fields that are validated by this validator instance;
     *                                may be null, in which case the validator may be used to validate any DocumentWorkerFieldValue field
     */
    public DocumentWorkerFieldValueValidator(final DataStore dataStore,
                                             final TestConfiguration testConfiguration,
                                             final Codec codec,
                                             final String... recognizedPropertyNames) {
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
    public boolean canValidate(String propertyName, Object sourcePropertyValue, Object validatorPropertyValue) {
        final boolean nameCheckPassed = recognizedPropertyNames.isEmpty() ||
                (propertyName != null && recognizedPropertyNames.contains(propertyName));
        return nameCheckPassed &&
                isDocumentWorkerFieldValue(sourcePropertyValue) &&
                isDocumentWorkerFieldValueExpectation(validatorPropertyValue);
    }

    @Override
    protected boolean isValid(Object testedPropertyValue, Object validatorPropertyValue) {
        if (testedPropertyValue == null && validatorPropertyValue == null) {
            return true;
        }
        if (testedPropertyValue == null || validatorPropertyValue == null) {
            return false;
        }
        try {
            final DocumentWorkerFieldValue testedValue =
                    convert(DocumentWorkerFieldValue.class, testedPropertyValue);
            final DocumentWorkerFieldValueExpectation validatorValue =
                    convert(DocumentWorkerFieldValueExpectation.class, validatorPropertyValue);
            return testedValue != null && validatorValue != null && equal(testedValue, validatorValue);
        } catch (DataStoreException | IOException e) {
            return false;
        }
    }

    private boolean isDocumentWorkerFieldValue(final Object value) {
        return convert(DocumentWorkerFieldValue.class, value) != null;
    }

    private boolean isDocumentWorkerFieldValueExpectation(final Object value) {
        return convert(DocumentWorkerFieldValueExpectation.class, value) != null;
    }

    private <T> T convert(final Class<T> type, final Object value) {
        if (type.isInstance(value)) {
            return (T)value;
        }
        if (value instanceof LinkedHashMap) {
            try {
                return mapper.convertValue(value, type);
            } catch (IllegalArgumentException e) {
            }
        }
        return null;
    }

    private boolean equal(final DocumentWorkerFieldValue actual, final DocumentWorkerFieldValueExpectation expected)
            throws DataStoreException, IOException {
        DocumentWorkerFieldEncoding expectedValueEncoding = nullToUtf8(expected.encoding);
        if (expectedValueEncoding == DocumentWorkerFieldEncoding.storage_ref) {
            return referencedDataValidator.isValid(getReferencedData(actual), expected.content);
        }
        Object actualValue =
                nullToUtf8(actual.encoding) == expectedValueEncoding ?
                        nullToEmpty(actual.data) :
                        expectedValueEncoding == DocumentWorkerFieldEncoding.base64 ?
                                getBytes(actual) :
                                new String(getBytes(actual));
        return simpleValueValidator.isValid(actualValue, nullToEmpty(expected.data));
    }

    private ReferencedData getReferencedData(final DocumentWorkerFieldValue actual) throws DataStoreException, IOException {
        return nullToUtf8(actual.encoding) == DocumentWorkerFieldEncoding.storage_ref ?
                ReferencedData.getReferencedData(nullToEmpty(actual.data)) :
                ReferencedData.getWrappedData(getBytes(actual));
    }

    private byte[] getBytes(final DocumentWorkerFieldValue value) throws DataStoreException, IOException {
        return getBytes(value.encoding, value.data);
    }

    private byte[] getBytes(final DocumentWorkerFieldEncoding encoding, final String data) throws DataStoreException, IOException {
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

    private static String nullToEmpty(final String str)
    {
        return (str != null) ? str : "";
    }

    private static DocumentWorkerFieldEncoding nullToUtf8(final DocumentWorkerFieldEncoding encoding)
    {
        return (encoding != null) ? encoding : DocumentWorkerFieldEncoding.utf8;
    }
}
