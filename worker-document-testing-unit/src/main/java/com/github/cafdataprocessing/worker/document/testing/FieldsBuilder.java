/*
 * Copyright 2016-2024 Open Text.
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
package com.github.cafdataprocessing.worker.document.testing;

import com.github.cafdataprocessing.worker.document.DocumentWorkerFieldEncoding;
import com.github.cafdataprocessing.worker.document.DocumentWorkerFieldValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.codec.binary.Base64;

/**
 * The Document Fields builder.
 */
public class FieldsBuilder
{
    private final Map<String, List<DocumentWorkerFieldValue>> fields;
    private final DocumentBuilder parentBuilder;

    /**
     * Instantiates a new Fields builder.
     *
     * @param fields the fields
     * @param parentBuilder the parent builder
     */
    FieldsBuilder(final Map<String, List<DocumentWorkerFieldValue>> fields, final DocumentBuilder parentBuilder)
    {
        this.fields = Objects.requireNonNull(fields);
        this.parentBuilder = parentBuilder;
    }

    private static DocumentWorkerFieldValue createFieldValue(final String data, final DocumentWorkerFieldEncoding encoding)
    {
        final DocumentWorkerFieldValue value = new DocumentWorkerFieldValue();
        value.data = data;
        value.encoding = encoding;
        return value;
    }

    /**
     * Goes back to the parent Document builder.
     *
     * @return the document builder
     */
    public DocumentBuilder documentBuilder()
    {
        return parentBuilder;
    }

    /**
     * Starts building of a new field.
     *
     * @param fieldName the field name
     * @return the field values builder
     */
    public FieldValuesBuilder addField(final String fieldName)
    {
        Objects.requireNonNull(fieldName);
        final List<DocumentWorkerFieldValue> fieldValues = getFieldValues(fieldName);
        return new FieldValuesBuilder(fieldValues, this);
    }

    /**
     * Adds multiple field values.
     *
     * @param fieldName the field name
     * @param values the values
     * @return this builder
     */
    public FieldsBuilder addFieldValues(final String fieldName, final String... values)
    {
        Objects.requireNonNull(fieldName);
        Objects.requireNonNull(values);

        return addFieldValues(fieldName, DocumentWorkerFieldEncoding.utf8, values);
    }

    /**
     * Adds multiple field values.
     *
     * @param fieldName the field name
     * @param encoding the encoding
     * @param values the values
     * @return this builder
     */
    public FieldsBuilder addFieldValues(final String fieldName, final DocumentWorkerFieldEncoding encoding, final String... values)
    {
        Objects.requireNonNull(fieldName);
        Objects.requireNonNull(values);

        for (final String value : values) {
            addFieldValue(fieldName, value, encoding);
        }
        return this;
    }

    /**
     * Adds a field value.
     *
     * @param fieldName the field name
     * @param data the data
     * @return this builder
     */
    public FieldsBuilder addFieldValue(final String fieldName, final String data)
    {
        Objects.requireNonNull(fieldName);

        return addFieldValue(fieldName, data, null);
    }

    /**
     * Adds a field value.
     *
     * @param fieldName the field name
     * @param data the data
     * @param encoding the encoding
     * @return this builder
     */
    public FieldsBuilder addFieldValue(final String fieldName, final String data, final DocumentWorkerFieldEncoding encoding)
    {
        Objects.requireNonNull(fieldName);

        addFieldValueInternal(fieldName, data, encoding);

        return this;
    }

    /**
     * Adds a field value.
     *
     * @param fieldName the field name
     * @param data the data
     * @return this builder
     */
    public FieldsBuilder addFieldValue(final String fieldName, final byte[] data)
    {
        final String base64String = Base64.encodeBase64String(data);
        addFieldValueInternal(fieldName, base64String, DocumentWorkerFieldEncoding.base64);
        return this;
    }

    private List<DocumentWorkerFieldValue> addFieldValueInternal(
        final String fieldName,
        final String data,
        final DocumentWorkerFieldEncoding encoding
    )
    {
        final List<DocumentWorkerFieldValue> fieldValues = getFieldValues(fieldName);

        fieldValues.add(createFieldValue(data, encoding));
        return fieldValues;
    }

    private List<DocumentWorkerFieldValue> getFieldValues(final String fieldName)
    {
        List<DocumentWorkerFieldValue> fieldValues;
        if (!fields.containsKey(fieldName)) {
            fieldValues = new ArrayList<>();
            fields.put(fieldName, fieldValues);
        } else {
            fieldValues = fields.get(fieldName);
        }
        return fieldValues;
    }

    public static final class FieldValuesBuilder
    {
        private final List<DocumentWorkerFieldValue> fieldValues;
        private final FieldsBuilder parentBuilder;

        /**
         * Instantiates a new Field values builder.
         *
         * @param fieldValues the field values
         * @param parentBuilder the parent builder
         */
        private FieldValuesBuilder(final List<DocumentWorkerFieldValue> fieldValues, final FieldsBuilder parentBuilder)
        {
            this.fieldValues = fieldValues;
            this.parentBuilder = parentBuilder;
        }

        /**
         * Adds a value.
         *
         * @param data the data
         * @return this builder
         */
        public FieldValuesBuilder addValue(final String data)
        {
            return addValue(data, DocumentWorkerFieldEncoding.utf8);
        }

        /**
         * Adds a value.
         *
         * @param data the data
         * @param encoding the encoding
         * @return this builder
         */
        public FieldValuesBuilder addValue(final String data, final DocumentWorkerFieldEncoding encoding)
        {
            fieldValues.add(createFieldValue(data, encoding));
            return this;
        }

        /**
         * Returns to the parent Fields builder.
         *
         * @return the fields builder
         */
        public FieldsBuilder then()
        {
            return parentBuilder;
        }
    }
}
