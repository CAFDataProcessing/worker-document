package com.hpe.caf.worker.document;

import com.hpe.caf.worker.document.DocumentWorkerResult;
import com.hpe.caf.worker.testing.ContentFileTestExpectation;

/**
 * DocumentExampleWorkerTestExpectation forms a component of the test item, and contains the expected DocumentWorkerResult, used to compare
 * with the actual worker result.
 */
public class DocumentWorkerTestExpectation  extends ContentFileTestExpectation {

    /**
     * DocumentWorkerResult read in from the yaml test case, used to validate the result of the worker is as expected.
     */
    private DocumentWorkerResult result;

    public DocumentWorkerTestExpectation() {
    }

    public DocumentWorkerResult getResult() {
        return result;
    }

    public void setResult(DocumentWorkerResult result) {
        this.result = result;
    }
}
