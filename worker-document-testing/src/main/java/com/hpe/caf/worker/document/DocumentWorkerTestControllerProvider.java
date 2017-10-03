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
package com.hpe.caf.worker.document;

import com.hpe.caf.worker.document.config.DocumentWorkerConfiguration;
import com.hpe.caf.worker.testing.*;
import com.hpe.caf.worker.testing.execution.AbstractTestControllerProvider;

/**
 * Class providing task factory, validation processor, save result processor, result preparation provider for running integration tests.
 */
public class DocumentWorkerTestControllerProvider extends AbstractTestControllerProvider<DocumentWorkerConfiguration, DocumentWorkerTask, DocumentWorkerResult, DocumentWorkerTestInput, DocumentWorkerTestExpectation>
{

    public DocumentWorkerTestControllerProvider()
    {
        super(DocumentWorkerConstants.WORKER_NAME, DocumentWorkerConfiguration::getOutputQueue, DocumentWorkerConfiguration.class, DocumentWorkerTask.class, DocumentWorkerResult.class, DocumentWorkerTestInput.class, DocumentWorkerTestExpectation.class);
    }

    /**
     * Return a task factory for creating tasks.
     *
     * @param configuration
     * @return DocumentWorkerTaskFactory
     * @throws Exception
     */
    @Override
    protected WorkerTaskFactory<DocumentWorkerTask, DocumentWorkerTestInput, DocumentWorkerTestExpectation> getTaskFactory(TestConfiguration<DocumentWorkerTask, DocumentWorkerResult, DocumentWorkerTestInput, DocumentWorkerTestExpectation> configuration)
        throws Exception
    {
        return new DocumentWorkerTaskFactory();
    }

    /**
     * Return a result validation processor for validating the worker result is the same as the expected result in the test item.
     *
     * @param configuration
     * @param workerServices
     * @return DocumentWorkerResultValidationProcessor
     */
    @Override
    protected ResultProcessor getTestResultProcessor(TestConfiguration<DocumentWorkerTask, DocumentWorkerResult, DocumentWorkerTestInput, DocumentWorkerTestExpectation> configuration, WorkerServices workerServices)
    {
        return new DocumentWorkerResultValidationProcessor(configuration, workerServices);
    }

    /**
     * Return a result preparation provider for preparing test items from YAML files.
     *
     * @param configuration
     * @return DocumentWorkerResultPreparationProvider
     */
    @Override
    protected TestItemProvider getDataPreparationItemProvider(TestConfiguration<DocumentWorkerTask, DocumentWorkerResult, DocumentWorkerTestInput, DocumentWorkerTestExpectation> configuration)
    {
        return new DocumentWorkerResultPreparationProvider(configuration);
    }

    /**
     * Return a save result processor for generating .testcase and result.content files found in test-data &gt; input folder.
     *
     * @param configuration
     * @param workerServices
     * @return DocumentWorkerSaveResultProcessor
     */
    @Override
    protected ResultProcessor getDataPreparationResultProcessor(TestConfiguration<DocumentWorkerTask, DocumentWorkerResult, DocumentWorkerTestInput, DocumentWorkerTestExpectation> configuration, WorkerServices workerServices)
    {
        return new DocumentWorkerSaveResultProcessor(configuration, workerServices);
    }

}
