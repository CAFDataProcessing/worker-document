/*
 * copyright 2016 Hewlett Packard Enterprise
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
        DocumentWorkerTask testTask = new DocumentWorkerTask();
        testTask.fields = new HashMap<>();
        testTask.fields.put("Name", createDataList("base64 encoding", DocumentWorkerEncoding.base64));

        String testString = serialiseTask(testTask);

        String expectedJson
            = "{`fields`:{`Name`:[{`data`:`base64 encoding`,`encoding`:`base64`}]}}"
            .replace('`', '"');

        assertEquals(expectedJson, testString);
    }

    @Test
    public void testDocumentWorkerSerializationTest1() throws Exception
    {
        DocumentWorkerTask testTask = new DocumentWorkerTask();
        testTask.fields = new HashMap<>();
        testTask.fields.put("Name", createDataList("utf8 encoding", DocumentWorkerEncoding.utf8));

        String testString = serialiseTask(testTask);

        String expectedJson
            = "{`fields`:{`Name`:[{`data`:`utf8 encoding`,`encoding`:`utf8`}]}}"
            .replace('`', '"');

        assertEquals(expectedJson, testString);
    }

    @Test
    public void testDocumentWorkerSerializationTest2() throws Exception
    {
        DocumentWorkerTask testTask = new DocumentWorkerTask();
        testTask.fields = new HashMap<>();
        testTask.fields.put("Name", createDataList("null encoding"));

        String testString = serialiseTask(testTask);

        String expectedJson
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

        DocumentWorkerTask dsTestTask = deserialiseTask(jsonString);

        DocumentWorkerData dataOb = dsTestTask.fields.get("Name").get(0);

        assertEquals("base64 encoding", dataOb.data);
        assertEquals(DocumentWorkerEncoding.base64, dataOb.encoding);
    }

    @Test
    public void testDocumentWorkerDeserializationTest1() throws Exception
    {
        final String jsonString
            = "{`fields`:{`Name`:[{`data`:`utf8 encoding`,`encoding`:`utf8`}]}}"
            .replace('`', '"');

        DocumentWorkerTask dsTestTask = deserialiseTask(jsonString);

        DocumentWorkerData dataOb = dsTestTask.fields.get("Name").get(0);

        assertEquals("utf8 encoding", dataOb.data);
        assertEquals(DocumentWorkerEncoding.utf8, dataOb.encoding);
    }

    @Test
    public void testDocumentWorkerDeserializationTest2() throws Exception
    {
        final String jsonString
            = "{`fields`:{`Name`:[{`data`:`null encoding`}]}}"
            .replace('`', '"');

        DocumentWorkerTask dsTestTask = deserialiseTask(jsonString);

        DocumentWorkerData dataOb = dsTestTask.fields.get("Name").get(0);

        assertEquals("null encoding", dataOb.data);
        assertEquals(null, dataOb.encoding);
    }
}
