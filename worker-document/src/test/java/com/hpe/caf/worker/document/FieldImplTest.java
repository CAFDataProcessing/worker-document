/*
 * Copyright 2016-2019 Micro Focus or one of its affiliates.
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
package com.hpe.caf.worker.document;

import com.hpe.caf.api.worker.WorkerTaskData;
import com.hpe.caf.worker.document.impl.*;
import com.hpe.caf.worker.document.tasks.FieldEnrichmentTask;
import java.util.*;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FieldImplTest
{
    /**
     * Test the fieldChanges property. Test by adding String and byte array values.
     */
    @Test
    public void fieldChangesTest()
    {
        final FieldImpl fieldImpl = createFieldImpl("NEW_FIELD");

        fieldImpl.add("/mnt/fs/docs/budget.doc");

        final DocumentWorkerFieldChanges fieldChanges = fieldImpl.getChanges();

        Assert.assertEquals("/mnt/fs/docs/budget.doc", fieldChanges.values.get(0).data);
        Assert.assertEquals(DocumentWorkerAction.add, fieldChanges.action);
    }

    @Test
    public void fieldChangesWithBytesTest()
    {
        final FieldImpl fieldImpl = createFieldImpl("NEW_FIELD");

        final byte[] b = "Test Data".getBytes();

        fieldImpl.add(b);

        final DocumentWorkerFieldChanges fieldChanges = fieldImpl.getChanges();

        Assert.assertEquals(Base64.encodeBase64String(b), fieldChanges.values.get(0).data);
        Assert.assertEquals(DocumentWorkerFieldEncoding.base64, fieldChanges.values.get(0).encoding);
    }

    @Test
    public void initialFieldValueTest()
    {
        //Tests the initial field values with a new field
        final FieldImpl fieldImpl = createFieldImpl("NEW_FIELD");
        Assert.assertEquals(0, fieldImpl.getValues().size());

        //Tests the initial field values with an already existing field 'REFERENCE'
        final FieldImpl fieldImpl2 = createFieldImpl("REFERENCE");
        Assert.assertEquals(1, fieldImpl2.getValues().size());
        Assert.assertEquals("/mnt/fs/docs/hr policy.doc", fieldImpl2.getStringValues().get(0));
    }

    @Test
    public void fieldSetTest()
    {
        final FieldImpl fieldImpl = createFieldImpl("NEW_FIELD");
        fieldImpl.add("Old value");

        final String newValue = "Cleared all values and added new one";
        fieldImpl.set(newValue);

        final DocumentWorkerFieldChanges fieldChanges = fieldImpl.getChanges();

        Assert.assertEquals(newValue, fieldChanges.values.get(0).data);
        Assert.assertEquals(null, fieldChanges.values.get(0).encoding);
        Assert.assertEquals(DocumentWorkerAction.replace, fieldChanges.action);
    }

    @Test
    public void fieldSetWithBytesTest()
    {
        final FieldImpl fieldImpl = createFieldImpl("NEW_FIELD");
        fieldImpl.add("Old value".getBytes());

        final byte[] newValue = "Cleared all values and added new one".getBytes();
        fieldImpl.set(newValue);

        final DocumentWorkerFieldChanges fieldChanges = fieldImpl.getChanges();

        Assert.assertEquals(Base64.encodeBase64String(newValue), fieldChanges.values.get(0).data);
        Assert.assertEquals(DocumentWorkerFieldEncoding.base64, fieldChanges.values.get(0).encoding);
        Assert.assertEquals(DocumentWorkerAction.replace, fieldChanges.action);
    }

    @Test
    public void fieldSetReferenceTest()
    {
        final FieldImpl fieldImpl = createFieldImpl("NEW_FIELD");
        fieldImpl.addReference("Old reference");

        final String newReference = "Cleared all values and added new reference";
        fieldImpl.setReference(newReference);

        final DocumentWorkerFieldChanges fieldChanges = fieldImpl.getChanges();

        Assert.assertEquals(newReference, fieldChanges.values.get(0).data);
        Assert.assertEquals(DocumentWorkerFieldEncoding.storage_ref, fieldChanges.values.get(0).encoding);
        Assert.assertEquals(DocumentWorkerAction.replace, fieldChanges.action);
    }

    private FieldImpl createFieldImpl(final String fileName)
    {
        final ApplicationImpl application = Mockito.mock(ApplicationImpl.class);
        final DocumentImpl document
            = createDocument("/mnt/fs/docs/hr policy.doc", "REFERENCE", DocumentWorkerFieldEncoding.utf8, application);

        return new FieldImpl(application, document, fileName);
    }

    /**
     * Create a DocumentImpl object that can be used by the tests.
     *
     * @param data the field value of the DocumentWorkerTask.
     * @param fieldName the field name of the DocumentWorkerTask.
     * @param encoding the field encoding of the DocumentWorkerTask.
     * @param application the worker application
     * @return
     */
    public static DocumentImpl createDocument(
        final String data,
        final String fieldName,
        final DocumentWorkerFieldEncoding encoding,
        final ApplicationImpl application
    )
    {
        final DocumentWorkerFieldValue workerData = new DocumentWorkerFieldValue();
        workerData.data = data;
        workerData.encoding = encoding;

        final List<DocumentWorkerFieldValue> workerDataList = new ArrayList<>();
        workerDataList.add(workerData);

        final Map<String, List<DocumentWorkerFieldValue>> fields = new HashMap<>();
        fields.put(fieldName, workerDataList);

        final DocumentWorkerTask task = new DocumentWorkerTask();
        task.fields = fields;

        final WorkerTaskData workerTaskData = Mockito.mock(WorkerTaskData.class);

        return FieldEnrichmentTask.create(application, workerTaskData, task).getDocument();
    }
}
