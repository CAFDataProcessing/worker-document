/*
 * Copyright 2016-2023 Open Text.
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
import com.hpe.caf.worker.document.model.Subdocument;
import com.hpe.caf.worker.document.views.ReadOnlyDocument;
import javax.annotation.Nonnull;

public final class SubdocumentImpl extends DocumentImpl implements Subdocument
{
    private final SubdocumentsImpl parent;

    private boolean isDeleted;

    public SubdocumentImpl(
        final ApplicationImpl application,
        final SubdocumentsImpl parent,
        final ReadOnlyDocument document
    )
    {
        super(application, parent.getDocument().getTask(), document);
        this.parent = parent;
        this.isDeleted = false;
    }

    @Nonnull
    @Override
    public Document getParentDocument()
    {
        return parent.getDocument();
    }

    @Nonnull
    @Override
    public Document getRootDocument()
    {
        return super.getTask().getDocument();
    }

    @Override
    public void delete()
    {
        isDeleted = true;
    }

    @Override
    public boolean hasChanges()
    {
        return isDeleted || super.hasChanges();
    }

    @Override
    public void reset()
    {
        isDeleted = false;
        super.reset();
    }

    @Override
    public boolean isDeleted()
    {
        return isDeleted;
    }
}
