/*
 * Copyright 2016-2019 Micro Focus or one of its affiliates.
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
package com.hpe.caf.worker.document.model;

import com.hpe.caf.worker.document.extensibility.BulkDocumentWorker;
import com.hpe.caf.worker.document.extensibility.DocumentWorker;

/**
 * Used for controlling how messages that are received from the input queue are processed.
 */
public interface InputMessageProcessor extends DocumentWorkerObject
{
    /**
     * Returns whether the worker's {@link DocumentWorker#processDocument(Document) processDocument()} method is called for each document
     * in a hierarchy of documents, or whether it is only called for the root document of the document hierarchy.
     * <p>
     * If the worker implements the {@link BulkDocumentWorker} interface then this method returns whether each document in a hierarchy of
     * documents is included in the batch of documents separately, or whether the batch of documents only includes root documents.
     *
     * @return true if each document in a hierarchy is processed separately
     * @see DocumentWorker#processDocument(Document)
     * @see BulkDocumentWorker#processDocuments(Documents)
     */
    boolean getProcessSubdocumentsSeparately();

    /**
     * Sets whether the {@link DocumentWorker#processDocument(Document) processDocument()} method should be called for each subdocument in
     * a document hierarchy in addition to being called for the root document of the hierarchy.
     * <p>
     * If the worker implements the {@link BulkDocumentWorker} interface then this setting controls whether each subdocument is added
     * separately to the batch of documents passed for processing.
     *
     * @param processSubdocumentsSeparately true to have each subdocument processed separately
     * @see DocumentWorker#processDocument(Document)
     * @see BulkDocumentWorker#processDocuments(Documents)
     */
    void setProcessSubdocumentsSeparately(boolean processSubdocumentsSeparately);
}
