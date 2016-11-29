package com.hpe.caf.worker.document;

import com.hpe.caf.worker.document.DocumentWorkerResult;
import com.hpe.caf.worker.document.DocumentWorkerTask;
import com.hpe.caf.worker.testing.TestConfiguration;
import com.hpe.caf.worker.testing.TestItem;
import com.hpe.caf.worker.testing.preparation.PreparationItemProvider;

import java.nio.file.Path;

/**
 * Result preparation provider for preparing test items.
 * Generates Test items from the yaml serialised test case files.
 */
public class DocumentWorkerResultPreparationProvider  extends PreparationItemProvider<DocumentWorkerTask, DocumentWorkerResult, DocumentWorkerTestInput, DocumentWorkerTestExpectation> {

    public DocumentWorkerResultPreparationProvider(TestConfiguration<DocumentWorkerTask, DocumentWorkerResult, DocumentWorkerTestInput, DocumentWorkerTestExpectation> configuration) {
        super(configuration);
    }

    /**
     * Method for generating test items from the yaml testcases.
     * Creates DocumentExampleWorkerTestInput and DocumentExampleWorkerTestExpectation objects (which contain DocumentWorkerTask and DocumentWorkerResult).
     * The DocumentWorkerTask found in DocumentExampleWorkerTestInput is fed into the worker for the integration test, and the result is
     * compared with the DocumentWorkerResult found in the DocumentExampleWorkerTestExpectation.
     * @param inputFile
     * @param expectedFile
     * @return TestItem
     * @throws Exception
     */
    @Override
    protected TestItem createTestItem(Path inputFile, Path expectedFile) throws Exception {
        TestItem<DocumentWorkerTestInput, DocumentWorkerTestExpectation> item = super.createTestItem(inputFile, expectedFile);
        DocumentWorkerTask task = getTaskTemplate();

        // if the task is null, put in default values
        if(task==null){
            task=new DocumentWorkerTask();
//            task. = DocumentExampleWorkerAction.VERBATIM;
        }

        item.getInputData().setTask(task);
        return item;
    }
}
