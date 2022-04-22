/*
 * Copyright 2016-2022 Micro Focus or one of its affiliates.
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

import static com.hpe.caf.worker.document.DocumentWorkerUtilClass.*;
import java.util.HashMap;
import org.junit.*;
import static org.junit.Assert.assertEquals;

public final class DocumentWorkerTaskTest
{
    @Test
    public void testDocumentWorkerSerializationTest() throws Exception
    {
        final DocumentWorkerTask testTask = new DocumentWorkerTask();
        testTask.fields = new HashMap<>();
        testTask.fields.put("Name", createDataList("base64 encoding", DocumentWorkerFieldEncoding.base64));

        final String testString = serialiseTask(testTask);

        final String expectedJson
            = "{`fields`:{`Name`:[{`data`:`base64 encoding`,`encoding`:`base64`}]}}"
                .replace('`', '"');

        assertEquals(expectedJson, testString);
    }

    @Test
    public void testDocumentWorkerSerializationTest1() throws Exception
    {
        final DocumentWorkerTask testTask = new DocumentWorkerTask();
        testTask.fields = new HashMap<>();
        testTask.fields.put("Name", createDataList("utf8 encoding", DocumentWorkerFieldEncoding.utf8));

        final String testString = serialiseTask(testTask);

        final String expectedJson
            = "{`fields`:{`Name`:[{`data`:`utf8 encoding`,`encoding`:`utf8`}]}}"
                .replace('`', '"');

        assertEquals(expectedJson, testString);
    }

    @Test
    public void testDocumentWorkerSerializationTest2() throws Exception
    {
        final DocumentWorkerTask testTask = new DocumentWorkerTask();
        testTask.fields = new HashMap<>();
        testTask.fields.put("Name", createDataList("null encoding"));

        final String testString = serialiseTask(testTask);

        final String expectedJson
            = "{`fields`:{`Name`:[{`data`:`null encoding`}]}}"
                .replace('`', '"');

        assertEquals(expectedJson, testString);
    }

    @Test
    public void testDocumentWorkerDeserializationTest() throws Exception
    {
        final String jsonString
            = "{`fields`:{`Name`:[{`data`:`base64 encoding`,`encoding`:`base64`}]}}"
                .replace('`', '"');

        final DocumentWorkerTask dsTestTask = deserialiseTask(jsonString);

        final DocumentWorkerFieldValue dataOb = dsTestTask.fields.get("Name").get(0);

        assertEquals("base64 encoding", dataOb.data);
        assertEquals(DocumentWorkerFieldEncoding.base64, dataOb.encoding);
    }

    @Test
    public void testDocumentWorkerDeserializationTest1() throws Exception
    {
        final String jsonString
            = "{`fields`:{`Name`:[{`data`:`utf8 encoding`,`encoding`:`utf8`}]}}"
                .replace('`', '"');

        final DocumentWorkerTask dsTestTask = deserialiseTask(jsonString);

        final DocumentWorkerFieldValue dataOb = dsTestTask.fields.get("Name").get(0);

        assertEquals("utf8 encoding", dataOb.data);
        assertEquals(DocumentWorkerFieldEncoding.utf8, dataOb.encoding);
    }

    @Test
    public void testDocumentWorkerDeserializationTest2() throws Exception
    {
        final String jsonString
            = "{`fields`:{`Name`:[{`data`:`null encoding`}]}}"
                .replace('`', '"');

        final DocumentWorkerTask dsTestTask = deserialiseTask(jsonString);

        final DocumentWorkerFieldValue dataOb = dsTestTask.fields.get("Name").get(0);

        assertEquals("null encoding", dataOb.data);
        assertEquals(null, dataOb.encoding);
    }
}
