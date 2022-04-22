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
package com.hpe.caf.worker.document.impl;

import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.Failures;
import com.hpe.caf.worker.document.model.Field;
import com.hpe.caf.worker.document.model.Fields;
import com.hpe.caf.worker.document.model.Subdocuments;
import com.hpe.caf.worker.document.output.ChangesJournal;
import com.hpe.caf.worker.document.tasks.AbstractTask;
import com.hpe.caf.worker.document.util.StringFunctions;
import com.hpe.caf.worker.document.views.ReadOnlyDocument;
import java.util.Objects;
import javax.annotation.Nonnull;

public class DocumentImpl extends DocumentWorkerObjectImpl implements Document
{
    private final AbstractTask documentTask;

    private final ReadOnlyDocument document;

    private String reference;

    private final FieldsImpl fields;

    private final FailuresImpl failures;

    private final SubdocumentsImpl subdocuments;

    public DocumentImpl(
        final ApplicationImpl application,
        final AbstractTask documentTask,
        final ReadOnlyDocument document
    )
    {
        super(application);
        this.documentTask = Objects.requireNonNull(documentTask);
        this.document = Objects.requireNonNull(document);
        this.reference = document.getReference();
        this.fields = new FieldsImpl(application, this);
        this.failures = new FailuresImpl(application, this, document.getFailures());
        this.subdocuments = new SubdocumentsImpl(application, this, document.getSubdocuments());
    }

    @Override
    public final String getReference()
    {
        return reference;
    }

    @Override
    public final void setReference(final String reference)
    {
        this.reference = reference;
    }

    @Override
    public final void resetReference()
    {
        reference = getOriginalReference();
    }

    @Nonnull
    @Override
    public final Fields getFields()
    {
        return fields;
    }

    @Nonnull
    @Override
    public final Field getField(final String fieldName)
    {
        return fields.get(fieldName);
    }

    @Override
    public final String getCustomData(final String dataKey)
    {
        return documentTask.getCustomData(dataKey);
    }

    @Nonnull
    @Override
    public final Failures getFailures()
    {
        return failures;
    }

    @Override
    public final void addFailure(final String failureId, final String failureMessage)
    {
        failures.add(failureId, failureMessage);
    }

    @Override
    public Document getParentDocument()
    {
        return null;
    }

    @Nonnull
    @Override
    public Document getRootDocument()
    {
        return this;
    }

    @Nonnull
    @Override
    public final Subdocuments getSubdocuments()
    {
        return subdocuments;
    }

    @Override
    public final boolean hasSubdocuments()
    {
        return !subdocuments.isEmpty();
    }

    @Override
    public boolean hasChanges()
    {
        return hasReferenceChanged()
            || fields.hasChanges()
            || failures.isChanged()
            || subdocuments.hasChanges();
    }

    @Override
    public void reset()
    {
        resetReference();
        fields.reset();
        failures.reset();
        subdocuments.reset();
    }

    public final void recordChanges(final ChangesJournal journal)
    {
        // Set the reference if it has been updated
        if (hasReferenceChanged()) {
            journal.setReference(reference);
        }

        // Record any field changes
        fields.recordChanges(journal);

        // Record any failure changes
        failures.recordChanges(journal);

        // Record any subdocument changes
        subdocuments.recordChanges(journal);
    }

    @Nonnull
    @Override
    public final AbstractTask getTask()
    {
        return documentTask;
    }

    @Nonnull
    public final ReadOnlyDocument getInitialDocument()
    {
        return document;
    }

    public final String getOriginalReference()
    {
        return document.getReference();
    }

    private boolean hasReferenceChanged()
    {
        final String originalReference = getOriginalReference();

        return !StringFunctions.equals(reference, originalReference);
    }
}
