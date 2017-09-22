/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development LP.
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

package com.hpe.caf.worker.document;

import com.hpe.caf.worker.document.exceptions.PostProcessingFailedException;
import com.hpe.caf.worker.document.model.Document;

/**
 * An interface a document post-processor.
 * The {@code postProcessDocument} will be called when core processing of a document is finished.
 */
public interface DocumentPostProcessor
{
    /**
     * Method called after core document processing by a worker is finished.
     * @param document a document processed by a worker
     * @throws PostProcessingFailedException when post-processor fails
     */
    void postProcessDocument(final Document document) throws PostProcessingFailedException;
}
