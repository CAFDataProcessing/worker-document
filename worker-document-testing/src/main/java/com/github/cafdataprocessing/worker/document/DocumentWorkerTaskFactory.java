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

import com.github.workerframework.worker.testing.TestItem;
import com.github.workerframework.worker.testing.WorkerTaskFactory;

/**
 * Task factory for creating tasks from test item.
 */
public class DocumentWorkerTaskFactory
    implements WorkerTaskFactory<DocumentWorkerTask, DocumentWorkerTestInput, DocumentWorkerTestExpectation>
{
    @Override
    public String getWorkerName()
    {
        return DocumentWorkerConstants.WORKER_NAME;
    }

    @Override
    public int getApiVersion()
    {
        return DocumentWorkerConstants.WORKER_API_VER;
    }

    @Override
    public DocumentWorkerTask createTask(
        final TestItem<DocumentWorkerTestInput, DocumentWorkerTestExpectation> testItem
    ) throws Exception
    {
        return testItem.getInputData().getTask();
    }
}
