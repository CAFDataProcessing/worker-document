package com.hpe.caf.worker.document;

import com.hpe.caf.api.worker.TaskMessage;
import com.hpe.caf.worker.document.DocumentWorkerResult;
import com.hpe.caf.worker.document.DocumentWorkerTask;
import com.hpe.caf.worker.testing.TestConfiguration;
import com.hpe.caf.worker.testing.TestItem;
import com.hpe.caf.worker.testing.WorkerServices;
import com.hpe.caf.worker.testing.preparation.PreparationResultProcessor;

public class DocumentWorkerSaveResultProcessor extends PreparationResultProcessor<DocumentWorkerTask, DocumentWorkerResult, DocumentWorkerTestInput, DocumentWorkerTestExpectation>
{
    public DocumentWorkerSaveResultProcessor(TestConfiguration<DocumentWorkerTask, DocumentWorkerResult, DocumentWorkerTestInput, DocumentWorkerTestExpectation> configuration, WorkerServices workerServices)
    {

        super(configuration, workerServices.getCodec());
    }

    @Override
    protected byte[] getOutputContent(DocumentWorkerResult workerResult, TaskMessage message, TestItem<DocumentWorkerTestInput, DocumentWorkerTestExpectation> testItem)
            throws Exception
    {
        testItem.getExpectedOutputData().setResult(workerResult);
        return super.getOutputContent(workerResult, message, testItem);
    }
}
