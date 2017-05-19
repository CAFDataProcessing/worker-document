/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development LP.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hpe.caf.worker.document.testing;

import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.api.worker.WorkerException;
import com.hpe.caf.worker.document.DocumentWorkerFieldEncoding;
import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.model.Document;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestDocumentWorkerTest
{
    @Test
    public void testTestDocumentWorkerCountWordsInContentAndTitle()
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