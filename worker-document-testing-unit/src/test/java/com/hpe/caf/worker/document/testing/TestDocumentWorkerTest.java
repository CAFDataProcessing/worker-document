package com.hpe.caf.worker.document.testing;

import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.api.worker.WorkerException;
import com.hpe.caf.worker.document.DocumentWorkerFieldEncoding;
import com.hpe.caf.worker.document.DocumentWorkerTask;
import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.impl.DocumentImpl;
import com.hpe.caf.worker.document.model.Document;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestDocumentWorkerTest
{
    @Test
    public void testTestDocumenntWorkerCountWordsInContentAndTitle()
        throws DocumentWorkerTransientException, InterruptedException, DataStoreException, WorkerException, CodecException, IOException
    {
        TestServices testServices = TestServices.createDefault();
        final String testContent = "The quick brown fox jumps over the lazy dog";

        final String testTitle = "My test document title";
        final String fieldValueToStay = "field-value-to-stay";
        final String expectedAddedFieldValue = "field-value";

        // Store testContent in data store
        // Prepare document with reference
        String reference = testServices.getDataStore().store(testContent.getBytes(), null);

        Document testDocument = DocumentBuilder.configure()
            .withServices(testServices)
            .withCustomData().add(TestDocumentWorker.CustomDataFieldValueToAdd, expectedAddedFieldValue)
            .documentBuilder()
            .withFields()
            .addFieldValue(TestDocumentWorker.CustomDataStorageReference, reference, DocumentWorkerFieldEncoding.storage_ref)
            .addFieldValue(TestDocumentWorker.FieldsTitle, testTitle)
            .addField(TestDocumentWorker.FieldToRemoveValue)
            .addValue(fieldValueToStay)
            .addValue(TestDocumentWorker.FieldValueToRemove).then()
            .addFieldValue(TestDocumentWorker.FieldToDelete, "some-data").documentBuilder().build();//create document

        TestDocumentWorker worker = new TestDocumentWorker();
        worker.processDocument(testDocument);

        DocumentImpl doc = (DocumentImpl) testDocument;

        DocumentWorkerTask documentWorkerTask = doc.getDocumentWorkerTask();
        byte[] bytes = testServices.getCodec().serialise(documentWorkerTask);

        File file = new File("task.json");
        FileUtils.writeByteArrayToFile(file, bytes);

        int testContentCount = testContent.split("\\s").length;
        int testTitleCount = testTitle.split("\\s").length;

        assertThat(testDocument.getField(TestDocumentWorker.ResultContentFieldWordCount).getStringValues(),
                   hasItem(String.valueOf(testContentCount)));
        assertThat(testDocument.getField(TestDocumentWorker.ResultTitleFieldWordCount).getStringValues(),
                   hasItem(String.valueOf(testTitleCount)));
        assertThat(testDocument.getField(TestDocumentWorker.CustomDataFieldValueToAdd).getStringValues(),
                   hasItem(expectedAddedFieldValue));

        assertThat(testDocument.getField(TestDocumentWorker.FieldToDelete).hasValues(), is(false));
        List<String> stringValues = testDocument.getField(TestDocumentWorker.FieldToRemoveValue).getStringValues();
        assertThat(stringValues.size(), is(1));
        assertThat(stringValues.get(0), is(fieldValueToStay));
    }
}
