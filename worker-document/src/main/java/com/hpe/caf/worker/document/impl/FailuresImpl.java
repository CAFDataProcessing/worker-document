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

import com.hpe.caf.worker.document.DocumentWorkerFailure;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.Failure;
import com.hpe.caf.worker.document.model.Failures;
import com.hpe.caf.worker.document.output.ChangesJournal;
import com.hpe.caf.worker.document.util.ThrowableFunctions;
import com.hpe.caf.worker.document.views.ReadOnlyFailure;
import com.hpe.caf.worker.document.views.ReadOnlyFailures;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public final class FailuresImpl extends DocumentWorkerObjectImpl implements Failures
{
    private final DocumentImpl document;
    private boolean ignoreOriginalFailures;
    private final List<ReadOnlyFailure> originalFailures;
    private final ArrayList<DocumentWorkerFailure> newFailures;

    public FailuresImpl(
        final ApplicationImpl application,
        final DocumentImpl document,
        final List<ReadOnlyFailure> originalFailures
    )
    {
        super(application);
        this.document = Objects.requireNonNull(document);
        this.ignoreOriginalFailures = false;
        this.originalFailures = Objects.requireNonNull(originalFailures);
        this.newFailures = new ArrayList<>();
    }

    @Override
    public void add(final String failureId, final String failureMessage)
    {
        add(failureId, failureMessage, null);
    }

    @Override
    public void add(final String failureId, final String failureMessage, final Throwable cause)
    {
        if (failureId == null && failureMessage == null && cause == null) {
            return;
        }

        final DocumentWorkerFailure failure = new DocumentWorkerFailure();
        failure.failureId = failureId;
        failure.failureMessage = failureMessage;

        if (cause != null) {
            failure.failureStack = ThrowableFunctions.getStackTrace(cause);
        }

        newFailures.add(failure);
    }

    @Override
    public void clear()
    {
        ignoreOriginalFailures = true;
        newFailures.clear();
    }

    @Nonnull
    @Override
    public Document getDocument()
    {
        return document;
    }

    @Override
    public boolean isChanged()
    {
        return (!newFailures.isEmpty())
            || (ignoreOriginalFailures && !originalFailures.isEmpty());
    }

    @Override
    public boolean isEmpty()
    {
        return (newFailures.isEmpty())
            && (ignoreOriginalFailures || originalFailures.isEmpty());
    }

    @Nonnull
    @Override
    public Iterator<Failure> iterator()
    {
        return createSnapshotList().iterator();
    }

    @Override
    public void reset()
    {
        ignoreOriginalFailures = false;
        newFailures.clear();
    }

    @Override
    public int size()
    {
        return (ignoreOriginalFailures ? 0 : originalFailures.size())
            + newFailures.size();
    }

    @Nonnull
    @Override
    public Stream<Failure> stream()
    {
        return createSnapshotList().stream();
    }

    public void recordChanges(final ChangesJournal journal)
    {
        if (ignoreOriginalFailures && !originalFailures.isEmpty()) {
            journal.setFailures(newFailures);
        } else if (!newFailures.isEmpty()) {
            journal.addFailures(newFailures);
        }
    }

    @Nonnull
    private List<Failure> createSnapshotList()
    {
        return Collections.unmodifiableList(readOnlyStream()
            .map(failure -> new FailureImpl(application, document, failure))
            .collect(Collectors.toList()));
    }

    @Nonnull
    private Stream<ReadOnlyFailure> readOnlyStream()
    {
        final Stream<ReadOnlyFailure> newFailuresStream = ReadOnlyFailures.createStream(newFailures);

        return ignoreOriginalFailures
            ? newFailuresStream
            : Stream.concat(originalFailures.stream(), newFailuresStream);
    }
}
