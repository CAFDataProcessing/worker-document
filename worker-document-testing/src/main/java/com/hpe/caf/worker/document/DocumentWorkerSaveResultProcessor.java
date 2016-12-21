package com.hpe.caf.worker.document;

import com.hpe.caf.api.worker.DataStoreSource;
import com.hpe.caf.api.worker.TaskMessage;
import com.hpe.caf.util.ref.ReferencedData;
import com.hpe.caf.worker.document.DocumentWorkerResult;
import com.hpe.caf.worker.document.DocumentWorkerTask;
import com.hpe.caf.worker.testing.OutputToFileProcessor;
import com.hpe.caf.worker.testing.TestConfiguration;
import com.hpe.caf.worker.testing.TestItem;
import com.hpe.caf.worker.testing.WorkerServices;
import com.hpe.caf.worker.testing.preparation.PreparationResultProcessor;

import java.io.InputStream;
import java.nio.file.Path;

public class DocumentWorkerSaveResultProcessor extends OutputToFileProcessor<DocumentWorkerResult, DocumentWorkerTestInput, DocumentWorkerTestExpectation>
{
    private TestConfiguration configuration;

    public DocumentWorkerSaveResultProcessor(TestConfiguration<DocumentWorkerTask, DocumentWorkerResult, DocumentWorkerTestInput, DocumentWorkerTestExpectation> configuration, WorkerServices workerServices)
    {
        super(workerServices.getCodec(), configuration.getWorkerResultClass(), configuration.getTestDataFolder());
        this.configuration = configuration;
    }

    @Override
    protected byte[] getOutputContent(DocumentWorkerResult workerResult, TaskMessage message, TestItem<DocumentWorkerTestInput, DocumentWorkerTestExpectation> testItem)
            throws Exception
    {
        testItem.getExpectedOutputData().setResult(workerResult);
        return getSerializedTestItem(testItem, configuration);
    }
}
