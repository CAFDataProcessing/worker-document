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
package com.github.cafdataprocessing.framework.processor;

import com.hpe.caf.worker.document.extensibility.DocumentWorker;
import com.hpe.caf.worker.document.extensibility.DocumentWorkerFactory;
import com.hpe.caf.worker.document.model.Application;
import com.hpe.caf.worker.document.util.ServiceFunctions;

enum ServiceLoadedDocumentWorkerFactory implements DocumentWorkerFactory
{
    INSTANCE;

    /**
     * This function constructs the implementation of the DocumentWorker interface. It uses the standard Java service provider. If there
     * is a DocumentWorkerFactory registered then it will be loaded and used to construct the DocumentWorker object. If not then the
     * DocumentWorker will be loaded directly.
     *
     * @return the new DocumentWorker object, or null if it could not be constructed
     */
    @Override
    public DocumentWorker createDocumentWorker(final Application application)
    {
        final DocumentWorkerFactory documentWorkerFactory = ServiceFunctions.loadService(DocumentWorkerFactory.class);

        if (documentWorkerFactory == null) {
            return ServiceFunctions.loadService(DocumentWorker.class);
        } else {
            return documentWorkerFactory.createDocumentWorker(application);
        }
    }
}
