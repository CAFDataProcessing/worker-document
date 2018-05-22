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
package com.hpe.caf.worker.document.impl;

import com.hpe.caf.worker.document.DocumentWorkerFieldChanges;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.Field;
import com.hpe.caf.worker.document.model.Fields;
import com.hpe.caf.worker.document.output.ChangesJournal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public final class FieldsImpl extends DocumentWorkerObjectImpl implements Fields
{
    private final DocumentImpl document;

    private final Map<String, FieldImpl> fields;

    private boolean allFieldsAddedToMap;

    public FieldsImpl(final ApplicationImpl application, final DocumentImpl document)
    {
        super(application);
        this.document = Objects.requireNonNull(document);
        this.fields = new HashMap<>();
        this.allFieldsAddedToMap = false;
    }

    @Nonnull
    @Override
    public Field get(final String fieldName)
    {
        FieldImpl field = fields.get(fieldName);

        if (field == null) {
            field = new FieldImpl(application, document, fieldName);
            fields.put(fieldName, field);
        }

        return field;
    }

    @Nonnull
    @Override
    public Document getDocument()
    {
        return document;
    }

    @Nonnull
    @Override
    public Iterator<Field> iterator()
    {
        return createSnapshotList().iterator();
    }

    @Override
    public void reset()
    {
        for (final FieldImpl field : fields.values()) {
            field.reset();
        }
    }

    @Nonnull
    @Override
    public Stream<Field> stream()
    {
        return createSnapshotList().stream();
    }

    public boolean hasChanges()
    {
        return fields.values().stream().anyMatch(Field::hasChanges);
    }

    /**
     * Records any field changes made into the specified journal.
     *
     * @param journal the register of changes
     */
    public void recordChanges(final ChangesJournal journal)
    {
        final Map<String, DocumentWorkerFieldChanges> changes = new HashMap<>();

        for (final FieldImpl field : fields.values()) {

            final DocumentWorkerFieldChanges fieldChanges = field.getChanges();

            if (fieldChanges != null) {
                changes.put(field.getName(), fieldChanges);
            }
        }

        if (!changes.isEmpty()) {
            journal.addFieldChanges(changes);
        }
    }

    /**
     * Returns a list of all the fields that currently make up the fields collection
     */
    @Nonnull
    private List<Field> createSnapshotList()
    {
        // Ensure that all fields have been added to the internal collection
        ensureAllFieldsAddedToMap();

        // Copy the fields into a list
        final List<Field> fieldList = fields.values().stream().collect(Collectors.toList());

        // Return a read-only version of the list
        return Collections.unmodifiableList(fieldList);
    }

    /**
     * Ensures that all of the fields have been added to the internal fields map, and not just those that have already been accessed.
     */
    private void ensureAllFieldsAddedToMap()
    {
        if (allFieldsAddedToMap) {
            return;
        }

        final Set<String> fieldNames = document.getInitialDocument().getFields().keySet();

        for (final String fieldName : fieldNames) {
            get(fieldName);
        }

        allFieldsAddedToMap = true;
    }
}
