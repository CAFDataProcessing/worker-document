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
package com.hpe.caf.worker.document.output;

import com.hpe.caf.worker.document.DocumentWorkerDocument;
import com.hpe.caf.worker.document.DocumentWorkerFailure;
import com.hpe.caf.worker.document.DocumentWorkerFieldChanges;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public interface ChangesJournal
{
    void setReference(String reference);

    void addFieldChanges(Map<String, DocumentWorkerFieldChanges> fieldChangesMap);

    void addFailure(DocumentWorkerFailure failure);

    void addFailures(Iterable<DocumentWorkerFailure> failures);

    void setFailures(List<DocumentWorkerFailure> failures);

    void addSubdocument(DocumentWorkerDocument subdocument);

    @Nonnull
    ChangesJournal updateSubdocument(int index, String reference);

    void removeSubdocument(int index, String reference);
}
