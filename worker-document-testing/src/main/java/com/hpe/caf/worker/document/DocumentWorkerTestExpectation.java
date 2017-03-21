package com.hpe.caf.worker.document;

/**
 * DocumentExampleWorkerTestExpectation forms a component of the test item, and contains the expected DocumentWorkerResultExpectation,
 * used to compare with the actual worker result.
 */
public class DocumentWorkerTestExpectation {

    private DocumentWorkerResultExpectation result;

    public DocumentWorkerTestExpectation() {
    }

    public DocumentWorkerResultExpectation getResult() {
        return result;
    }

    public void setResult(DocumentWorkerResultExpectation result) {
        this.result = result;
    }
}
