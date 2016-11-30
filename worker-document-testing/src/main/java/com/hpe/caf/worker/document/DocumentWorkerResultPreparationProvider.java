package com.hpe.caf.worker.document;

import com.hpe.caf.worker.document.DocumentWorkerResult;
import com.hpe.caf.worker.document.DocumentWorkerTask;
import com.hpe.caf.worker.testing.TestConfiguration;
import com.hpe.caf.worker.testing.TestItem;
import com.hpe.caf.worker.testing.TestItemProvider;
import com.hpe.caf.worker.testing.preparation.PreparationItemProvider;

import java.nio.file.Path;
import java.util.*;

/**
 * Result preparation provider for preparing test items.
 * Generates Test items from the yaml serialised test case files.
 */
public class DocumentWorkerResultPreparationProvider  implements TestItemProvider {

    @Override
    public Collection<TestItem> getItems() throws Exception {
        //Creates a Test Item Collection with hardcoded values

        //Set Document Worker Test Input
        DocumentWorkerData workerData = new DocumentWorkerData();
        workerData.data = "This is an Example";
        workerData.encoding = DocumentWorkerEncoding.utf8;

        List<DocumentWorkerData> workerDataList = new ArrayList<>();
        workerDataList.add(workerData);

        Map<String, List<DocumentWorkerData>> fields = new HashMap<>();
        fields.put("ReferenceField", workerDataList);

        DocumentWorkerTask task = new DocumentWorkerTask();
        task.fields = fields;

        DocumentWorkerTestInput testInput = new DocumentWorkerTestInput();
        testInput.setTask(task);

        //Set Document Worker Test Expectation
        DocumentWorkerFieldChanges fieldChanges = new DocumentWorkerFieldChanges();
        fieldChanges.action = DocumentWorkerAction.add;
        fieldChanges.values = workerDataList;

        List<DocumentWorkerFieldChanges> fieldChangesList = new ArrayList<>();
        fieldChangesList.add(fieldChanges);

        Map<String, DocumentWorkerFieldChanges> resultFieldChanges = new HashMap<>();
        resultFieldChanges.put("This is an example", fieldChanges);


        DocumentWorkerResult result = new DocumentWorkerResult();
        result.fieldChanges = resultFieldChanges;

        DocumentWorkerTestExpectation testExpectation = new DocumentWorkerTestExpectation();
        testExpectation.setResult(result);

        TestItem item = new TestItem("This is an example", testInput, testExpectation);

        Collection<TestItem> testItems = new ArrayList<>();
        testItems.add(item);

        return testItems;
    }
}
