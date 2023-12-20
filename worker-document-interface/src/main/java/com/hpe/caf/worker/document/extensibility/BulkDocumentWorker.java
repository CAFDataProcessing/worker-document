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
package com.hpe.caf.worker.document.extensibility;

import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.model.Documents;

/**
 * The BulkDocumentWorker interface is designed to allow multiple documents to be processed together.
 */
public interface BulkDocumentWorker extends DocumentWorker
{
    /**
     * Processes a batch of documents. Fields can be added or removed from each of the documents.
     *
     * @param documents the batch of documents to be processed
     * @throws InterruptedException if any thread has interrupted the current thread
     * @throws DocumentWorkerTransientException if the documents could not be processed due to a transient issue
     */
    void processDocuments(Documents documents) throws InterruptedException, DocumentWorkerTransientException;
}
