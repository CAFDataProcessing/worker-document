/*
 * Copyright 2016-2018 Micro Focus or one of its affiliates.
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

import com.hpe.caf.worker.document.DocumentWorkerDocument;
import com.hpe.caf.worker.document.DocumentWorkerFailure;
import com.hpe.caf.worker.document.DocumentWorkerFieldChanges;
import com.hpe.caf.worker.document.DocumentWorkerResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DocumentWorkerResultBuilder implements ChangesJournal
{
    private final DocumentWorkerResult documentWorkerResult;

    public DocumentWorkerResultBuilder()
    {
        this.documentWorkerResult = new DocumentWorkerResult();
    }

    @Override
    public void setReference(final String reference)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addFieldChanges(final Map<String, DocumentWorkerFieldChanges> fieldChangesMap)
    {
        documentWorkerResult.fieldChanges = fieldChangesMap;
    }

    @Override
    public void addFailure(final DocumentWorkerFailure failure)
    {
        List<DocumentWorkerFailure> failures = documentWorkerResult.failures;
        if (failures == null) {
            failures = new ArrayList<>(1);
            documentWorkerResult.failures = failures;
        }

        failures.add(failure);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void addSubdocument(final DocumentWorkerDocument subdocument)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ChangesJournal updateSubdocument(final int index, final String reference)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeSubdocument(final int index, final String reference)
    {
        throw new UnsupportedOperationException();
    }

    public DocumentWorkerResult toDocumentWorkerResult()
    {
        return documentWorkerResult;
    }
}
