package com.hpe.caf.worker.document;

import com.hpe.caf.api.worker.TaskMessage;
import com.hpe.caf.worker.testing.*;
import com.hpe.caf.worker.testing.configuration.ValidationSettings;
import com.hpe.caf.worker.testing.validation.PropertyValidatingProcessor;
import org.testng.Assert;

import java.util.Map;

/**
 * Processor for validation of the worker result, compares with the expected result in the test item.
 */
public class DocumentWorkerResultValidationProcessor extends PropertyValidatingProcessor<DocumentWorkerResult, DocumentWorkerTestInput, DocumentWorkerTestExpectation> {

    public DocumentWorkerResultValidationProcessor(TestConfiguration<DocumentWorkerTask, DocumentWorkerResult, DocumentWorkerTestInput, DocumentWorkerTestExpectation> testConfiguration, WorkerServices workerServices) {
        super(testConfiguration, workerServices, ValidationSettings.configure().build());
    }

    /**
     * Validates the result by comparing the test expectation in the test item with the actual worker result.
     * First it asserts that the result has the correct worker status.
     * Then it passes the test item and worker result back to the superclass.
     * The superclass compares the referenced data in the worker result with the test item and calculates a similarity percentage
     * between the text in the worker result with the text in the expected result.
     * @param testItem
     * @param message
     * @param workerResult
     * @return boolean
     * @throws Exception
     */
    @Override
    protected boolean processWorkerResult(TestItem<DocumentWorkerTestInput, DocumentWorkerTestExpectation> testItem, TaskMessage message, DocumentWorkerResult workerResult) throws Exception {
        return super.processWorkerResult(testItem, message, workerResult);
    }

    @Override
    protected boolean isCompleted(TestItem<DocumentWorkerTestInput, DocumentWorkerTestExpectation> testItem, TaskMessage message, DocumentWorkerResult documentWorkerResult) {
        return true;
    }

    @Override
    protected Map<String, Object> getExpectationMap(TestItem<DocumentWorkerTestInput, DocumentWorkerTestExpectation> testItem, TaskMessage message, DocumentWorkerResult documentWorkerResult) {
        return (Map)testItem.getExpectedOutputData();
    }

    @Override
    protected Map<String, Object> getFailedExpectationMap(TestItem<DocumentWorkerTestInput, DocumentWorkerTestExpectation> testItem, TaskMessage message) {
        return (Map)testItem.getExpectedOutputData();
    }

    @Override
    protected Object getValidatedObject(TestItem<DocumentWorkerTestInput, DocumentWorkerTestExpectation> testItem, TaskMessage message, DocumentWorkerResult documentWorkerResult) {
        return documentWorkerResult;
    }
}
