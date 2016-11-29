package com.hpe.caf.worker.document;

import com.hpe.caf.util.ref.ReferencedData;
import com.hpe.caf.worker.document.DocumentWorkerConstants;
import com.hpe.caf.worker.document.DocumentWorkerTask;
import com.hpe.caf.worker.testing.FileInputWorkerTaskFactory;
import com.hpe.caf.worker.testing.TestConfiguration;
import com.hpe.caf.worker.testing.TestItem;

/**
 * Task factory for creating tasks from test item.
 */
public class DocumentWorkerTaskFactory extends FileInputWorkerTaskFactory<DocumentWorkerTask, DocumentWorkerTestInput, DocumentWorkerTestExpectation> {
    public DocumentWorkerTaskFactory(TestConfiguration configuration) throws Exception {
        super(configuration);
    }

    /**
     * Creates a task from a test item (the test item is generated from the yaml test case).
     * @param testItem
     * @param sourceData
     * @return DocumentExampleWorkerTask
     */
    @Override
    protected DocumentWorkerTask createTask(TestItem<DocumentWorkerTestInput, DocumentWorkerTestExpectation> testItem, ReferencedData sourceData) {
        DocumentWorkerTask task = testItem.getInputData().getTask();

        //setting task source data to the source data parameter.
//        task.sourceData = sourceData;
//        task.datastorePartialReference = getContainerId();
//        task.action = testItem.getInputData().getTask().action;

        return task;
    }

    @Override
    public String getWorkerName() {
        return DocumentWorkerConstants.WORKER_NAME;
    }

    @Override
    public int getApiVersion() {
        return DocumentWorkerConstants.WORKER_API_VER;
    }
}
