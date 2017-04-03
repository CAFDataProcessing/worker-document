package com.hpe.caf.worker.document.testing;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.hpe.caf.api.worker.WorkerException;
import com.hpe.caf.api.worker.WorkerTaskData;
import com.hpe.caf.worker.document.DocumentWorkerFieldValue;
import com.hpe.caf.worker.document.DocumentWorkerTask;
import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.impl.DocumentImpl;
import com.hpe.caf.worker.document.model.Document;
import org.apache.commons.io.FileUtils;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final DocumentWorkerTask workerTask;
    private TestServices services;

    private DocumentBuilder(final DocumentWorkerTask workerTask)
    {
        this.workerTask = workerTask;
    }

    /**
     * Configures a new Document.
     *
     * @return Document object builder.
     */
    public static DocumentBuilder configure()
    {
        return new DocumentBuilder(new DocumentWorkerTask());
    }

    /**
     * Configures a new Document using a file with JSON or YAML-serialized Document Worker task
     *
     * @param path File path
     * @return Document builder.
     * @throws IOException An issue with accessing a file.
     */
    public static DocumentBuilder fromFile(final String path) throws IOException
    {
        final YAMLMapper mapper = new YAMLMapper();

        final byte[] bytes = FileUtils.readFileToByteArray(new File(path));
        final DocumentWorkerTask workerTask = mapper.readValue(bytes, DocumentWorkerTask.class);
        return new DocumentBuilder(workerTask);
    }

    /**
     * Configures a new Document using provided map of Document Worker fields.
     *
     * @param fields Document worker fields map.
     * @return Document builder.
     */
    public DocumentBuilder withFields(final Map<String, List<DocumentWorkerFieldValue>> fields)
    {
        workerTask.fields = fields;
        return this;
    }

    /**
     * Configures Document fields.
     *
     * @return Document fields builder.
     */
    public FieldsBuilder withFields()
    {
        if (workerTask.fields == null) {
            workerTask.fields = new HashMap<>();
        }
        return new FieldsBuilder(workerTask.fields, this);
    }

    /**
     * Configures Document CustomData.
     *
     * @return CustomData builder.
     */
    public CustomDataBulder withCustomData()
    {
        if (workerTask.customData == null) {
            workerTask.customData = new HashMap<>();
        }

        return new CustomDataBulder(workerTask.customData, this);
    }

    /**
     * Configures services used by a worker.
     * <p>
     * Those services will be supplied to a Document implementation and will be available to a worker.
     *
     * @see TestServices
     * @param testServices Services to use.
     * @return Document builder.
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
     * @throws WorkerException If construction of a Document object fails.
     */
    public Document build() throws WorkerException
    {
        if (services == null) {
            services = TestServices.createDefault();
        }
        final WorkerTaskData workerTaskData = Mockito.mock(WorkerTaskData.class);

        final DocumentImpl document = new DocumentImpl(new ApplicationImpl(
            services.getConfigurationSource(), services.getDataStore(), services.getCodec()), workerTaskData, workerTask);

        return document;
    }
}
