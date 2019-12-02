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
package com.hpe.caf.worker.document.output;

import com.hpe.caf.worker.document.DocumentWorkerAction;
import com.hpe.caf.worker.document.DocumentWorkerChange;
import com.hpe.caf.worker.document.DocumentWorkerDocument;
import com.hpe.caf.worker.document.DocumentWorkerFailure;
import com.hpe.caf.worker.document.DocumentWorkerFieldChanges;
import com.hpe.caf.worker.document.DocumentWorkerFieldValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class ChangeLogBuilder implements ChangesJournal
{
    private final ArrayList<Supplier<DocumentWorkerChange>> changeSuppliers;
    private final ArrayList<Integer> failureChangeIndexes;

    public ChangeLogBuilder()
    {
        this.changeSuppliers = new ArrayList<>();
        this.failureChangeIndexes = new ArrayList<>();
    }

    @Override
    public void setReference(final String reference)
    {
        final DocumentWorkerChange change = new DocumentWorkerChange();
        change.setReference = new DocumentWorkerChange.SetReferenceParams();
        change.setReference.value = reference;

        changeSuppliers.add(() -> change);
    }

    @Override
    public void addFieldChanges(final Map<String, DocumentWorkerFieldChanges> fieldChangesMap)
    {
        // Just return if there are no changes
        if (fieldChangesMap == null || fieldChangesMap.isEmpty()) {
            return;
        }

        // Split out the fields between those to be added, removed, and updated
        final Map<String, List<DocumentWorkerFieldValue>> addFields = new HashMap<>();
        final Map<String, List<DocumentWorkerFieldValue>> setFields = new HashMap<>();
        final List<String> removeFields = new ArrayList<>();

        for (final Map.Entry<String, DocumentWorkerFieldChanges> fieldChangesEntry : fieldChangesMap.entrySet()) {
            final String fieldName = fieldChangesEntry.getKey();
            final DocumentWorkerFieldChanges fieldChanges = fieldChangesEntry.getValue();
            final DocumentWorkerAction action = nullToAdd(fieldChanges.action);
            final List<DocumentWorkerFieldValue> values = fieldChanges.values;
            final boolean hasValues = (values != null) && (!values.isEmpty());

            switch (action) {
                case add:
                    if (hasValues) {
                        addFields.put(fieldName, values);
                    }
                    break;
                case replace:
                    if (hasValues) {
                        setFields.put(fieldName, values);
                    } else {
                        removeFields.add(fieldName);
                    }
                    break;
                default:
                    throw new RuntimeException("Logical error: the action is not recognised");
            }
        }

        // Add change objects for each type of field change
        if (!removeFields.isEmpty()) {
            final DocumentWorkerChange change = new DocumentWorkerChange();
            change.removeFields = removeFields;
            changeSuppliers.add(() -> change);
        }

        if (!addFields.isEmpty()) {
            final DocumentWorkerChange change = new DocumentWorkerChange();
            change.addFields = addFields;
            changeSuppliers.add(() -> change);
        }

        if (!setFields.isEmpty()) {
            final DocumentWorkerChange change = new DocumentWorkerChange();
            change.setFields = setFields;
            changeSuppliers.add(() -> change);
        }
    }

    @Override
    public void addFailure(final DocumentWorkerFailure failure)
    {
        final DocumentWorkerChange change = new DocumentWorkerChange();
        change.addFailure = failure;

        failureChangeIndexes.add(changeSuppliers.size());
        changeSuppliers.add(() -> change);
    }

    @Override
    public void addFailures(final Iterable<DocumentWorkerFailure> failures)
    {
        if (failures != null) {
            for (final DocumentWorkerFailure failure : failures) {
                addFailure(failure);
            }
        }
    }

    @Override
    public void setFailures(final List<DocumentWorkerFailure> failures)
    {
        Objects.requireNonNull(failures);

        // Remove any failures already recorded as this will overwrite them
        for (int i = failureChangeIndexes.size() - 1; i >= 0; i--) {
            final int failIndex = failureChangeIndexes.get(i);
            changeSuppliers.remove(failIndex);
        }

        failureChangeIndexes.clear();

        // Set the failures
        final DocumentWorkerChange change = new DocumentWorkerChange();
        change.setFailures = failures;

        failureChangeIndexes.add(changeSuppliers.size());
        changeSuppliers.add(() -> change);
    }

    @Override
    public void addSubdocument(final DocumentWorkerDocument subdocument)
    {
        final DocumentWorkerChange change = new DocumentWorkerChange();
        change.addSubdocument = subdocument;

        changeSuppliers.add(() -> change);
    }

    @Override
    public ChangesJournal updateSubdocument(final int index, final String reference)
    {
        final ChangeLogBuilder changeLogBuilder = new ChangeLogBuilder();

        changeSuppliers.add(() -> {
            final List<DocumentWorkerChange> changes = changeLogBuilder.getChanges();

            if (changes.isEmpty()) {
                return null;
            } else {
                final DocumentWorkerChange change = new DocumentWorkerChange();
                change.updateSubdocument = new DocumentWorkerChange.UpdateSubdocumentParams();
                change.updateSubdocument.index = index;
                change.updateSubdocument.reference = reference;
                change.updateSubdocument.changes = changes;

                return change;
            }
        });

        return changeLogBuilder;
    }

    @Override
    public void removeSubdocument(final int index, final String reference)
    {
        final DocumentWorkerChange change = new DocumentWorkerChange();
        change.removeSubdocument = new DocumentWorkerChange.RemoveSubdocumentParams();
        change.removeSubdocument.index = index;
        change.removeSubdocument.reference = reference;

        changeSuppliers.add(() -> change);
    }

    @Nonnull
    public List<DocumentWorkerChange> getChanges()
    {
        return changeSuppliers.stream()
            .map(Supplier::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private static DocumentWorkerAction nullToAdd(final DocumentWorkerAction action)
    {
        return (action != null) ? action : DocumentWorkerAction.add;
    }
}
