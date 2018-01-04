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
package com.hpe.caf.worker.document.changelog;

import com.hpe.caf.worker.document.DocumentWorkerChange;
import com.hpe.caf.worker.document.DocumentWorkerChange.InsertSubdocumentParams;
import com.hpe.caf.worker.document.DocumentWorkerChange.RemoveSubdocumentParams;
import com.hpe.caf.worker.document.DocumentWorkerChange.SetReferenceParams;
import com.hpe.caf.worker.document.DocumentWorkerChange.UpdateSubdocumentParams;
import com.hpe.caf.worker.document.DocumentWorkerChangeLogEntry;
import com.hpe.caf.worker.document.DocumentWorkerDocument;
import com.hpe.caf.worker.document.DocumentWorkerFailure;
import com.hpe.caf.worker.document.DocumentWorkerFieldValue;
import com.hpe.caf.worker.document.exceptions.InvalidChangeLogException;
import com.hpe.caf.worker.document.exceptions.UnexpectedSubdocumentReferenceException;
import com.hpe.caf.worker.document.views.ReadOnlyDocument;
import com.hpe.caf.worker.document.views.ReadOnlyFailure;
import com.hpe.caf.worker.document.views.ReadOnlyFailures;
import com.hpe.caf.worker.document.views.ReadOnlyFieldValue;
import com.hpe.caf.worker.document.views.ReadOnlyFieldValues;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

/**
 * Implements the application of a change log on top of a {@link ReadOnlyDocument}.
 */
public final class MutableDocument
{
    private String reference;
    private final Map<String, ArrayList<ReadOnlyFieldValue>> fields;
    private ArrayList<ReadOnlyFailure> failures;
    private final ArrayList<MutableDocument> subdocuments;

    public MutableDocument(final ReadOnlyDocument document)
    {
        Objects.requireNonNull(document);

        this.reference = document.getReference();
        this.fields = document.getFields().entrySet().stream().collect(
            Collectors.toMap(Entry::getKey,
                             entry -> new ArrayList<>(entry.getValue())));
        this.failures = new ArrayList<>(document.getFailures());
        this.subdocuments = document
            .getSubdocuments()
            .stream()
            .map(MutableDocument::new)
            .collect(toArrayList());
    }

    public String getReference()
    {
        return reference;
    }

    @Nonnull
    public Map<String, ArrayList<ReadOnlyFieldValue>> getFields()
    {
        return fields;
    }

    @Nonnull
    public ArrayList<ReadOnlyFailure> getFailures()
    {
        return failures;
    }

    @Nonnull
    public ArrayList<MutableDocument> getSubdocuments()
    {
        return subdocuments;
    }

    public void applyChangeLog(final List<DocumentWorkerChangeLogEntry> changeLog) throws InvalidChangeLogException
    {
        if (changeLog == null) {
            return;
        }

        for (final DocumentWorkerChangeLogEntry changeLogEntry : changeLog) {
            applyChangeLogEntry(changeLogEntry);
        }
    }

    public void applyChangeLogEntry(final DocumentWorkerChangeLogEntry changeLogEntry) throws InvalidChangeLogException
    {
        if (changeLogEntry == null) {
            return;
        }

        applyChanges(changeLogEntry.changes);
    }

    public void applyChanges(final List<DocumentWorkerChange> changes) throws InvalidChangeLogException
    {
        if (changes == null) {
            return;
        }

        for (final DocumentWorkerChange change : changes) {
            applyChange(change);
        }
    }

    private void applyChange(final DocumentWorkerChange change) throws InvalidChangeLogException
    {
        if (change == null) {
            return;
        }

        final SetReferenceParams setReferenceParams = change.setReference;
        if (setReferenceParams != null) {
            setReference(setReferenceParams.value);
        }

        final Map<String, List<DocumentWorkerFieldValue>> addFieldsParam = change.addFields;
        if (addFieldsParam != null) {
            addFields(addFieldsParam);
        }

        final Map<String, List<DocumentWorkerFieldValue>> setFieldsParam = change.setFields;
        if (setFieldsParam != null) {
            setFields(setFieldsParam);
        }

        final List<String> removeFieldsParam = change.removeFields;
        if (removeFieldsParam != null) {
            removeFields(removeFieldsParam);
        }

        final DocumentWorkerFailure addFailureParam = change.addFailure;
        if (addFailureParam != null) {
            addFailure(addFailureParam);
        }

        final List<DocumentWorkerFailure> setFailuresParam = change.setFailures;
        if (setFailuresParam != null) {
            setFailures(setFailuresParam);
        }

        final DocumentWorkerDocument addSubdocumentParam = change.addSubdocument;
        if (addSubdocumentParam != null) {
            addSubdocument(addSubdocumentParam);
        }

        final InsertSubdocumentParams insertSubdocumentParams = change.insertSubdocument;
        if (insertSubdocumentParams != null) {
            insertSubdocument(insertSubdocumentParams.index,
                              insertSubdocumentParams.subdocument);
        }

        final UpdateSubdocumentParams updateSubdocumentParams = change.updateSubdocument;
        if (updateSubdocumentParams != null) {
            updateSubdocument(updateSubdocumentParams.index,
                              updateSubdocumentParams.reference,
                              updateSubdocumentParams.changes);
        }

        final RemoveSubdocumentParams removeSubdocumentParams = change.removeSubdocument;
        if (removeSubdocumentParams != null) {
            removeSubdocument(removeSubdocumentParams.index,
                              removeSubdocumentParams.reference);
        }
    }

    private void setReference(final String value)
    {
        this.reference = value;
    }

    private void addFields(final Map<String, List<DocumentWorkerFieldValue>> addFields)
    {
        for (final Map.Entry<String, List<DocumentWorkerFieldValue>> addFieldEntry : addFields.entrySet()) {
            final String fieldName = addFieldEntry.getKey();
            final List<DocumentWorkerFieldValue> fieldValuesToAdd = addFieldEntry.getValue();

            if (fieldValuesToAdd != null) {
                final List<ReadOnlyFieldValue> fieldValues = fields.get(fieldName);
                final Stream<ReadOnlyFieldValue> extraFieldValues = ReadOnlyFieldValues.createStream(fieldValuesToAdd);

                if (fieldValues == null) {
                    fields.put(fieldName, extraFieldValues.collect(toArrayList()));
                } else {
                    fieldValues.addAll(extraFieldValues.collect(toList()));
                }
            }
        }
    }

    private void setFields(final Map<String, List<DocumentWorkerFieldValue>> setFields)
    {
        for (final Map.Entry<String, List<DocumentWorkerFieldValue>> setFieldEntry : setFields.entrySet()) {
            final String fieldName = setFieldEntry.getKey();
            final List<DocumentWorkerFieldValue> fieldValuesToSet = setFieldEntry.getValue();

            if (fieldValuesToSet == null) {
                fields.remove(fieldName);
            } else {
                final Stream<ReadOnlyFieldValue> newFieldValuesStream = ReadOnlyFieldValues.createStream(fieldValuesToSet);
                final ArrayList<ReadOnlyFieldValue> newFieldValues = newFieldValuesStream.collect(toArrayList());

                fields.put(fieldName, newFieldValues);
            }
        }
    }

    private void removeFields(final List<String> removeFields)
    {
        for (final String fieldName : removeFields) {
            fields.remove(fieldName);
        }
    }

    private void addFailure(final DocumentWorkerFailure addFailure)
    {
        final ReadOnlyFailure failure = ReadOnlyFailure.create(addFailure);

        if (failure != null) {
            failures.add(failure);
        }
    }

    private void setFailures(final List<DocumentWorkerFailure> setFailures)
    {
        failures = ReadOnlyFailures
            .createStream(setFailures)
            .collect(toArrayList());
    }

    private void addSubdocument(final DocumentWorkerDocument addSubdocument)
    {
        final ReadOnlyDocument subdocument = ReadOnlyDocument.create(addSubdocument);

        subdocuments.add(new MutableDocument(subdocument));
    }

    private void insertSubdocument(
        final int index,
        final DocumentWorkerDocument subdocument
    )
    {
        final ReadOnlyDocument newSubdocument = ReadOnlyDocument.create(subdocument);

        subdocuments.add(index, new MutableDocument(newSubdocument));
    }

    private void updateSubdocument(
        final int index,
        final String reference,
        final List<DocumentWorkerChange> changes
    ) throws InvalidChangeLogException
    {
        final MutableDocument subdocument = subdocuments.get(index);
        checkSubdocumentReference(reference, subdocument.reference);

        subdocument.applyChanges(changes);
    }

    private void removeSubdocument(
        final int index,
        final String reference
    ) throws UnexpectedSubdocumentReferenceException
    {
        final MutableDocument subdocument = subdocuments.get(index);
        checkSubdocumentReference(reference, subdocument.reference);

        subdocuments.remove(index);
    }

    private static <T> Collector<T, ?, ArrayList<T>> toArrayList()
    {
        return Collectors.toCollection(ArrayList::new);
    }

    private static void checkSubdocumentReference(
        final String expectedReference,
        final String actualReference
    ) throws UnexpectedSubdocumentReferenceException
    {
        if (expectedReference != null && !expectedReference.equals(actualReference)) {
            throw new UnexpectedSubdocumentReferenceException(expectedReference, actualReference);
        }
    }
}
