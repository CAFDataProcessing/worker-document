package com.hpe.caf.worker.document;

import com.hpe.caf.worker.document.DocumentWorkerTask;
import com.hpe.caf.worker.testing.FileTestInputData;

/**
 * DocumentExampleWorkerTestInput is a component of test item, and contains a worker task used to provide test work to a worker.
 */
public class DocumentWorkerTestInput {

    /**
     * DocumentWorkerTask read in from the yaml test case and used as an input of test work to the worker.
     */
    private DocumentWorkerTask task;

    public DocumentWorkerTestInput() {
    }

    public DocumentWorkerTask getTask() {
        return task;
    }

    public void setTask(DocumentWorkerTask task) {
        this.task = task;
    }
}
