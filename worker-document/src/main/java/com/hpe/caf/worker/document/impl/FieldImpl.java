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
package com.hpe.caf.worker.document.impl;

import com.hpe.caf.worker.document.DocumentWorkerAction;
import com.hpe.caf.worker.document.DocumentWorkerFieldChanges;
import com.hpe.caf.worker.document.DocumentWorkerFieldEncoding;
import com.hpe.caf.worker.document.DocumentWorkerFieldValue;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.Field;
import com.hpe.caf.worker.document.model.FieldValues;
import com.hpe.caf.worker.document.views.ReadOnlyFieldValue;
import com.hpe.caf.worker.document.views.ReadOnlyFieldValues;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.codec.binary.Base64;

public final class FieldImpl extends DocumentWorkerObjectImpl implements Field
{
    private final DocumentImpl document;

    private final String fieldName;

    private final List<ReadOnlyFieldValue> initialFieldValues;

    private final DocumentWorkerFieldChanges fieldChanges;

    public FieldImpl(final ApplicationImpl application, final DocumentImpl document, final String fieldName)
    {
        super(application);
        this.document = Objects.requireNonNull(document);
        this.fieldName = Objects.requireNonNull(fieldName);
        this.initialFieldValues = getInitialFieldValues(document, fieldName);
        this.fieldChanges = createFieldChanges();
    }

    @Override
    public void add(final String data)
    {
        final DocumentWorkerFieldValue fieldValue = new DocumentWorkerFieldValue();
        fieldValue.data = data;

        fieldChanges.values.add(fieldValue);
    }

    @Override
    public void add(final byte[] data)
    {
        final DocumentWorkerFieldValue fieldValue = new DocumentWorkerFieldValue();
        fieldValue.data = Base64.encodeBase64String(data);
        fieldValue.encoding = DocumentWorkerFieldEncoding.base64;

        fieldChanges.values.add(fieldValue);
    }

    @Override
    public void addReference(final String dataRef)
    {
        final DocumentWorkerFieldValue fieldValue = new DocumentWorkerFieldValue();
        fieldValue.data = dataRef;
        fieldValue.encoding = DocumentWorkerFieldEncoding.storage_ref;

        fieldChanges.values.add(fieldValue);
    }

    @Override
    public void clear()
    {
        fieldChanges.action = DocumentWorkerAction.replace;
        fieldChanges.values.clear();
    }

    @Nonnull
    @Override
    public Document getDocument()
    {
        return document;
    }

    @Nonnull
    @Override
    public String getName()
    {
        return fieldName;
    }

    @Nonnull
    @Override
    public List<String> getStringValues()
    {
        final List<String> stringValueList = getValues()
            .stream()
            .filter(fieldValue -> (!fieldValue.isReference()) && fieldValue.isStringValue())
            .map(fieldValue -> fieldValue.getStringValue())
            .collect(Collectors.toList());

        return Collections.unmodifiableList(stringValueList);
    }

    @Nonnull
    @Override
    public FieldValues getValues()
    {
        final List<ReadOnlyFieldValue> currentFieldValues = new ArrayList<>();

        if (fieldChanges.action == DocumentWorkerAction.add) {
            currentFieldValues.addAll(initialFieldValues);
        }

        final List<ReadOnlyFieldValue> newFieldValues = ReadOnlyFieldValues.create(fieldChanges.values);
        currentFieldValues.addAll(newFieldValues);

        return new FieldValuesImpl(application, this, currentFieldValues);
    }

    @Override
    public boolean hasChanges()
    {
        return (fieldChanges.action == DocumentWorkerAction.replace)
            || (!fieldChanges.values.isEmpty());
    }

    @Override
    public boolean hasValues()
    {
        // Return true if there are new values being added OR (the action is 'add' AND there are initial values)
        return (!fieldChanges.values.isEmpty())
            || ((fieldChanges.action == DocumentWorkerAction.add) && (!initialFieldValues.isEmpty()));
    }

    @Override
    public void set(final String data)
    {
        clear();
        add(data);
    }

    @Override
    public void set(final byte[] data)
    {
        clear();
        add(data);
    }

    @Override
    public void setReference(final String dataRef)
    {
        clear();
        addReference(dataRef);
    }

    @Override
    public void reset()
    {
        fieldChanges.action = DocumentWorkerAction.add;
        fieldChanges.values.clear();
    }

    /**
     * Returns the changes made to the field, or null if no changes have been made.
     *
     * @return the changes made to the field, or null if no changes have been made
     */
    public DocumentWorkerFieldChanges getChanges()
    {
        return hasChanges()
            ? fieldChanges
            : null;
    }

    @Nonnull
    private static List<ReadOnlyFieldValue> getInitialFieldValues(final DocumentImpl document, final String fieldName)
    {
        final List<ReadOnlyFieldValue> fieldValues = document.getInitialDocument().getFields().get(fieldName);

        return (fieldValues != null) ? fieldValues : Collections.emptyList();
    }

    @Nonnull
    private static DocumentWorkerFieldChanges createFieldChanges()
    {
        final DocumentWorkerFieldChanges fieldChanges = new DocumentWorkerFieldChanges();
        fieldChanges.action = DocumentWorkerAction.add;
        fieldChanges.values = new ArrayList<>();

        return fieldChanges;
    }
}
