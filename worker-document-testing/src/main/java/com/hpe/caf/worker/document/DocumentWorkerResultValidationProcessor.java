/*
 * Copyright 2016-2021 Micro Focus or one of its affiliates.
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.hpe.caf.api.worker.TaskMessage;
import com.hpe.caf.worker.testing.*;
import com.hpe.caf.worker.testing.configuration.ValidationSettings;
import com.hpe.caf.worker.testing.validation.PropertyMap;
import com.hpe.caf.worker.testing.validation.PropertyValidatingProcessor;
import java.util.Map;

/**
 * Processor for validation of the worker result, compares with the expected result in the test item.
 */
public class DocumentWorkerResultValidationProcessor<TTestInput>
    extends PropertyValidatingProcessor<DocumentWorkerResult, TTestInput, DocumentWorkerTestExpectation>
{
    public DocumentWorkerResultValidationProcessor(
        final TestConfiguration<DocumentWorkerTask, DocumentWorkerResult, TTestInput, DocumentWorkerTestExpectation> testConfiguration,
        final WorkerServices workerServices
    )
    {
        super(testConfiguration,
              workerServices,
              ValidationSettings.configure().customValidators(
                  new DocumentWorkerFieldChangesValidator(new DocumentWorkerFieldValueValidator(workerServices.getDataStore(),
                                                                                                testConfiguration,
                                                                                                workerServices.getCodec()))).build());
    }

    @Override
    protected boolean processWorkerResult(final TestItem<TTestInput, DocumentWorkerTestExpectation> testItem,
                                          final TaskMessage message,
                                          final DocumentWorkerResult workerResult) throws Exception
    {
        return super.processWorkerResult(testItem, message, workerResult);
    }

    @Override
    protected boolean isCompleted(final TestItem<TTestInput, DocumentWorkerTestExpectation> testItem,
                                  final TaskMessage message,
                                  final DocumentWorkerResult documentWorkerResult)
    {
        return true;
    }

    @Override
    protected Map<String, Object> getExpectationMap(final TestItem<TTestInput, DocumentWorkerTestExpectation> testItem,
                                                    final TaskMessage message,
                                                    final DocumentWorkerResult documentWorkerResult)
    {

        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        final DocumentWorkerResultExpectation expectation = testItem.getExpectedOutputData().getResult();
        final PropertyMap expectationPropertyMap = convert(expectation);
        return expectationPropertyMap;
    }

    @Override
    protected Object getValidatedObject(final TestItem<TTestInput, DocumentWorkerTestExpectation> testItem,
                                        final TaskMessage message,
                                        final DocumentWorkerResult documentWorkerResult)
    {
        return convert(documentWorkerResult);
    }

    private PropertyMap convert(final DocumentWorkerResult result)
    {
        final PropertyMap propertyMap = new PropertyMap();
        if (result != null) {
            if (result.fieldChanges != null) {
                propertyMap.put("fieldChanges", result.fieldChanges);
            }
            if (result.failures != null) {
                propertyMap.put("failures", result.failures);
            }
        }
        return propertyMap;
    }

    private PropertyMap convert(final DocumentWorkerResultExpectation result)
    {
        final PropertyMap propertyMap = new PropertyMap();
        if (result.fieldChanges != null) {
            propertyMap.put("fieldChanges", result.fieldChanges);
        }
        if (result.failures != null) {
            propertyMap.put("failures", result.failures);
        }
        return propertyMap;
    }
}
