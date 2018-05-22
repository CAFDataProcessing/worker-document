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
package com.hpe.caf.worker.document.impl;

import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.Failure;
import com.hpe.caf.worker.document.views.ReadOnlyFailure;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class FailureImpl extends DocumentWorkerObjectImpl implements Failure
{
    private final DocumentImpl document;
    private final ReadOnlyFailure failure;

    public FailureImpl(
        final ApplicationImpl application,
        final DocumentImpl document,
        final ReadOnlyFailure failure
    )
    {
        super(application);
        this.document = Objects.requireNonNull(document);
        this.failure = Objects.requireNonNull(failure);
    }

    @Nonnull
    @Override
    public Document getDocument()
    {
        return document;
    }

    @Override
    public String getFailureId()
    {
        return failure.getFailureId();
    }

    @Override
    public String getFailureMessage()
    {
        return failure.getFailureMessage();
    }

    @Override
    public String getFailureStack()
    {
        return failure.getFailureStack();
    }
}
