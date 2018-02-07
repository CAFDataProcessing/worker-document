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
package com.hpe.caf.worker.document.testing;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.hpe.caf.api.worker.WorkerException;
import com.hpe.caf.api.worker.WorkerTaskData;
import com.hpe.caf.worker.document.DocumentWorkerDocument;
import com.hpe.caf.worker.document.DocumentWorkerDocumentTask;
import com.hpe.caf.worker.document.DocumentWorkerFieldValue;
import com.hpe.caf.worker.document.DocumentWorkerTask;
import com.hpe.caf.worker.document.exceptions.InvalidChangeLogException;
import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.impl.DocumentImpl;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.tasks.AbstractTask;
import com.hpe.caf.worker.document.tasks.DocumentTask;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.mockito.Mockito;

/**
 * Document Worker "Document" object builder / configurator.
 * <p>
 * There are two entry points for configuring a {@link Document}:
 * <ul>
 * <li>Using {@code DocumentBuilder.configure()} method, which allows to configure a document in code.</li>
 * <li>Using {@code DocumentBuilder.fromFile(...)} method, which allows to configure a document using either JSON or YAML serialized
 * {@link DocumentWorkerTask} file.</li>
 * </ul>
 */
public final class DocumentBuilder
{
    private final DocumentWorkerDocumentTask workerTask;
    private TestServices services;

    private DocumentBuilder(DocumentWorkerDocumentTask workerTask)
    {
        this.workerTask = workerTask;
        if (workerTask.document == null) {
            workerTask.document = new DocumentWorkerDocument();
        }
    }

    /**
     * Configures a new Document.
     *
     * @return Document object builder.
     */
    public static DocumentBuilder configure()
    {
        return new DocumentBuilder(new DocumentWorkerDocumentTask());
    }

    /**
     * Configures a new Document using a file with JSON or YAML-serialized Document Worker task
     *
     * @param path File path
     * @return current Document builder
     * @throws IOException An issue with accessing a file
     */
    public static DocumentBuilder fromFile(final String path) throws IOException
    {
        final YAMLMapper mapper = new YAMLMapper();

        final byte[] bytes = FileUtils.readFileToByteArray(new File(path));
        final DocumentWorkerDocumentTask workerTask = mapper.readValue(bytes, DocumentWorkerDocumentTask.class);
        return new DocumentBuilder(workerTask);
    }

    /**
     * Specifies a reference value for the document being built.
     *
     * @param reference reference value for the document being built
     * @return current Document builder
     */
    public DocumentBuilder withReference(String reference)
    {
        workerTask.document.reference = reference;
        return this;
    }

    /**
     * Configures a new Document using provided map of Document Worker fields.
     *
     * @param fields Document worker fields map.
     * @return Document builder.
     */
    public DocumentBuilder withFields(final Map<String, List<DocumentWorkerFieldValue>> fields)
    {
        workerTask.document.fields = fields;
        return this;
    }

    /**
     * Configures Document fields.
     *
     * @return Document fields builder.
     */
    public FieldsBuilder withFields()
    {
        if (workerTask.document.fields == null) {
            workerTask.document.fields = new HashMap<>();
        }
        return new FieldsBuilder(workerTask.document.fields, this);
    }

    /**
     * Configures Document CustomData.
     *
     * @return CustomData builder.
     */
    public CustomDataBuilder withCustomData()
    {
        if (workerTask.customData == null) {
            workerTask.customData = new HashMap<>();
        }
        return new CustomDataBuilder(workerTask.customData, this);
    }

    /**
     * Configures sub-documents for this Document.
     * <p>
     * Each document can have a sub-documents. When documentBuilders are supplied, they will be used to build sub-documents associated
     * with this document.
     *
     * @param documentBuilders sub-document builders
     * @return current Document builder
     */
    public DocumentBuilder withSubDocuments(DocumentBuilder... documentBuilders)
    {
        if (documentBuilders == null) {
            return this;
        }
        if (workerTask.document.subdocuments == null) {
            workerTask.document.subdocuments = new ArrayList<>(documentBuilders.length);
        }
        for (DocumentBuilder documentBuilder : documentBuilders) {
            final DocumentWorkerDocumentTask subDocumentTask = documentBuilder.workerTask;
            workerTask.document.subdocuments.add(subDocumentTask.document);
        }
        return this;
    }

    /**
     * Configures services used by a worker.
     * <p>
     * Those services will be supplied to a Document implementation and will be available to a worker.
     *
     * @see TestServices
     * @param testServices Services to use
     * @return current Document builder
     */
    public DocumentBuilder withServices(final TestServices testServices)
    {
        this.services = testServices;
        return this;
    }

    /**
     * Constructs a configured Document instance.
     *
     * @return Document
     * @throws WorkerException if construction of a Document object fails
     */
    public Document build() throws WorkerException
    {
        if (services == null) {
            services = TestServices.createDefault();
        }
        final AbstractTask documentWorkerTask;
        try {
            documentWorkerTask = DocumentTask.create(
                new ApplicationImpl(services.getConfigurationSource(), services.getDataStore(), services.getCodec()),
                Mockito.mock(WorkerTaskData.class), workerTask);
        } catch (InvalidChangeLogException e) {
            //TODO: either introduce new (runtime) exception or change the signature.
            // I don't think we want to throw checked exceptions from the tests.
            // It's not something we can handle. If there's an exception, the test is misconfigured
            // and the test code needs to be fixed.
            throw new RuntimeException("Failed to create document worker task.", e);
        }
        final DocumentImpl document = documentWorkerTask.getDocument();

        return document;
    }
}
