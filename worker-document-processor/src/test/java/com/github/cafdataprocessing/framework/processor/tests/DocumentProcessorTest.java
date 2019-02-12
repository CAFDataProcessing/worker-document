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
package com.github.cafdataprocessing.framework.processor.tests;

import com.github.cafdataprocessing.framework.processor.DocumentProcessor;
import com.github.cafdataprocessing.framework.processor.DocumentProcessorBuilder;
import com.github.cafdataprocessing.framework.processor.DocumentProcessorConfiguration;
import com.github.cafdataprocessing.framework.processor.InvalidTaskException;
import com.github.cafdataprocessing.framework.processor.serviceproviders.SingleServiceProvider;
import com.github.cafdataprocessing.workers.testworker.TestWorker;
import com.google.gson.Gson;
import com.hpe.caf.worker.document.DocumentWorkerDocumentTask;
import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.extensibility.DocumentWorker;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.HealthMonitor;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import com.github.cafdataprocessing.framework.processor.TaskResult;

public final class DocumentProcessorTest
{
    @Test
    public void f1() throws DocumentWorkerTransientException, InterruptedException, InvalidTaskException
    {
        final DocumentWorkerDocumentTask documentTask = createDocumentTask(document -> {
            document.getField("INITIAL_FIELD").set("Initial value");
            document.getSubdocuments().add("new-subdocument").getField("INITIAL_SUBDOC_FIELD").set("Initial subdocument value");
        });

        final TaskResult processTask1;

        try (final DocumentProcessor processor = new DocumentProcessorBuilder()
            .setWorkerFactory(__ -> new TestWorker())
            .setServiceProvider(SingleServiceProvider.create(DocumentProcessorTest.class, this))
            .build()) {

            if (processor.getBulkSupport()) {
                // Use processor.processTasks();
                processTask1 = null;
            } else {
                processTask1 = processor.processTask(documentTask);
            }
        }

        Assert.assertNotNull(processTask1);
        Assert.assertNotNull(processTask1.getResult());

        transform(processTask1.getResult(), document -> {
                  Assert.assertArrayEquals(new String[]{"Initial value"},
                                           document.getField("INITIAL_FIELD").getStringValues().toArray());
              });
    }

    private static DocumentWorkerDocumentTask createDocumentTask(final Consumer<Document> documentConsumer)
        throws DocumentWorkerTransientException, InterruptedException, InvalidTaskException
    {
        return transform(new DocumentWorkerDocumentTask(), documentConsumer);
    }

    private static DocumentWorkerDocumentTask transform(
        final DocumentWorkerDocumentTask documentTask,
        final Consumer<Document> documentConsumer
    ) throws DocumentWorkerTransientException, InterruptedException, InvalidTaskException
    {
        try (final DocumentProcessor processor = new DocumentProcessorBuilder()
            .setConfiguration(configFromJson("{inputMessageProcessing: {processSubdocumentsSeparately: false}}"))
            .setWorkerFactory(__ -> new DocumentWorker()
            {
                @Override
                public void checkHealth(final HealthMonitor healthMonitor)
                {
                }

                @Override
                public void processDocument(final Document document)
                {
                    documentConsumer.accept(document);
                }
            })
            .build()) {

            return processor.processTask(documentTask).getResult();
        }
    }

    private static DocumentProcessorConfiguration configFromJson(final String configJson)
    {
        return new Gson().fromJson(configJson, DocumentProcessorConfiguration.class);
    }
}
