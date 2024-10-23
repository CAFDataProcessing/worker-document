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
package com.github.cafdataprocessing.worker.document;

import com.github.cafdataprocessing.worker.document.impl.ApplicationImpl;
import com.github.workerframework.worker.api.BulkWorker;
import com.github.workerframework.worker.api.BulkWorkerRuntime;
import com.github.cafdataprocessing.worker.document.extensibility.BulkDocumentWorker;

/**
 * This class allows implementations of the BulkDocumentWorker class can be used with the Worker Framework.
 */
public final class BulkDocumentWorkerAdapter extends DocumentWorkerAdapter implements BulkWorker
{
    /**
     * This is the actual implementation of the worker.<p>
     * This class is adapting its interface so that it can be used with the bulk methods of the Worker Framework.
     */
    private final BulkDocumentWorker bulkDocumentWorker;

    public BulkDocumentWorkerAdapter(final ApplicationImpl application, final BulkDocumentWorker bulkDocumentWorker)
    {
        super(application, bulkDocumentWorker);
        this.bulkDocumentWorker = bulkDocumentWorker;
    }

    @Override
    public void processTasks(final BulkWorkerRuntime runtime) throws InterruptedException
    {
        final BulkDocumentMessageProcessor messageProcessor
            = new BulkDocumentMessageProcessor(application, bulkDocumentWorker, runtime);

        messageProcessor.processTasks();
    }
}
