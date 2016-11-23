/*
 * copyright 2016 Hewlett Packard Enterprise
 */
package com.hpe.caf.worker.document;

import static com.hpe.caf.worker.document.DocumentWorkerUtilClass.*;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public final class DocumentWorkerResultTest
{
    @Test
    public void testDocumentWorkerSerializationTest() throws Exception
    {
        DocumentWorkerResult testResult = new DocumentWorkerResult();
        testResult.fieldChanges = new HashMap<>();
        testResult.fieldChanges.put(
            "fieldName", createFieldChanges(
                DocumentWorkerAction.add, createDataList("Deserialization Test")));

        String testString = serialiseResult(testResult);

        String expectedJson
            = "{`fieldChanges`:{`fieldName`:{`action`:`add`,`values`:[{`data`:`Deserialization Test`}]}}}"
            .replace('`', '"');

        assertEquals(expectedJson, testString);
    }

    @Test
    public void testDocumentWorkerSerializationTest1() throws Exception
    {
        DocumentWorkerResult testResult = new DocumentWorkerResult();
        testResult.fieldChanges = new HashMap<>();
        testResult.fieldChanges.put(
            "fieldName", createFieldChanges(
                DocumentWorkerAction.add, createDataList("Deserialization Test", DocumentWorkerEncoding.utf8)));

        String testString = serialiseResult(testResult);

        String expectedJson
            = "{`fieldChanges`:{`fieldName`:{`action`:`add`,`values`:[{`data`:`Deserialization Test`,`encoding`:`utf8`}]}}}"
            .replace('`', '"');

        assertEquals(expectedJson, testString);
    }

    @Test
    public void testDocumentWorkerDeserializationTest() throws Exception
    {
        final String jsonString
            = "{`fieldChanges`:{`fieldName`:{`action`:`add`,`values`:[{`data`:`Deserialization Test`,`encoding`:`utf8`}]}}}"
            .replace('`', '"');

        DocumentWorkerResult dsTestResult = deserialiseResult(jsonString);

        DocumentWorkerFieldChanges changeOb = dsTestResult.fieldChanges.get("fieldName");
        DocumentWorkerData recoveredData = changeOb.values.get(0);

        assertEquals(changeOb.action, DocumentWorkerAction.add);
        assertEquals(recoveredData.data, "Deserialization Test");
        assertEquals(recoveredData.encoding, DocumentWorkerEncoding.utf8);
    }

    @Test
    public void testDocumentWorkerDeserializationTest1() throws Exception
    {
        final String jsonString
            = "{`fieldChanges`:{`fieldName`:{`action`:`add`,`values`:[{`data`:`Deserialization Test`}]}}}"
            .replace('`', '"');

        DocumentWorkerResult dsTestResult = deserialiseResult(jsonString);

        DocumentWorkerFieldChanges changeOb = dsTestResult.fieldChanges.get("fieldName");
        DocumentWorkerData recoveredData = changeOb.values.get(0);

        assertEquals(changeOb.action, DocumentWorkerAction.add);
        assertEquals(recoveredData.data, "Deserialization Test");
        assertEquals(recoveredData.encoding, null);
    }
}
