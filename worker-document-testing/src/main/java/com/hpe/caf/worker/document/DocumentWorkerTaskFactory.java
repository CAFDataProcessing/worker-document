package com.hpe.caf.worker.document;

import com.hpe.caf.worker.testing.TestItem;
import com.hpe.caf.worker.testing.WorkerTaskFactory;

/**
 * Task factory for creating tasks from test item.
 */
public class DocumentWorkerTaskFactory implements WorkerTaskFactory<DocumentWorkerTask, DocumentWorkerTestInput, DocumentWorkerTestExpectation> {

    @Override
    public String getWorkerName() {
        return DocumentWorkerConstants.WORKER_NAME;
    }

    @Override
    public int getApiVersion() {
        return DocumentWorkerConstants.WORKER_API_VER;
    }

    @Override
    public DocumentWorkerTask createTask(TestItem<DocumentWorkerTestInput, DocumentWorkerTestExpectation> testItem) throws Exception {
        return testItem.getInputData().getTask();
    }
}
