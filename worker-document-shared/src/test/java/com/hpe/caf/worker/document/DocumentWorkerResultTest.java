/*
 * Copyright 2016-2020 Micro Focus or one of its affiliates.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public final class DocumentWorkerResultTest
{
    @Test
    public void testDocumentWorkerSerializationTest() throws Exception
    {
        final DocumentWorkerResult testResult = new DocumentWorkerResult();
        testResult.fieldChanges = new HashMap<>();
        testResult.fieldChanges.put(
            "fieldName", createFieldChanges(
                DocumentWorkerAction.add, createDataList("Deserialization Test")));

        final String testString = serialiseResult(testResult);

        final String expectedJson
            = "{`fieldChanges`:{`fieldName`:{`action`:`add`,`values`:[{`data`:`Deserialization Test`}]}}}"
                .replace('`', '"');

        assertEquals(expectedJson, testString);
    }

    @Test
    public void testDocumentWorkerSerializationTest1() throws Exception
    {
        final DocumentWorkerResult testResult = new DocumentWorkerResult();
        testResult.fieldChanges = new HashMap<>();
        testResult.fieldChanges.put(
            "fieldName", createFieldChanges(
                DocumentWorkerAction.add, createDataList("Deserialization Test")));

        final DocumentWorkerFailure failure = new DocumentWorkerFailure();
        failure.failureId = "123456";
        failure.failureMessage = "Test of Failure Feature with null Stack Trace";
        testResult.failures = new ArrayList<>();
        testResult.failures.add(failure);
        final String testString = serialiseResult(testResult);

        final String expectedJson
            = ("{`fieldChanges`:{`fieldName`:{`action`:`add`,`values`:[{`data`:`Deserialization Test`}]}},"
                + "`failures`:[{`failureId`:`123456`,`failureMessage`:`Test of Failure Feature with null Stack Trace`}]}")
                .replace('`', '"');

        assertEquals(expectedJson, testString);
    }

    @Test
    public void testDocumentWorkerSerializationTest2() throws Exception
    {
        final DocumentWorkerResult testResult = new DocumentWorkerResult();
        testResult.fieldChanges = new HashMap<>();
        testResult.fieldChanges.put(
            "fieldName", createFieldChanges(
                DocumentWorkerAction.add, createDataList("Deserialization Test")));

        final DocumentWorkerFailure failure = new DocumentWorkerFailure();
        failure.failureId = "123456";
        failure.failureMessage = "Test of Failure Feature with Stack Trace";
        failure.failureStack = "This is a Test with a failure Stack Trace";
        testResult.failures = new ArrayList<>();
        testResult.failures.add(failure);
        final String testString = serialiseResult(testResult);

        final String expectedJson
            = ("{`fieldChanges`:{`fieldName`:{`action`:`add`,`values`:[{`data`:`Deserialization Test`}]}},"
                + "`failures`:[{`failureId`:`123456`,`failureMessage`:`Test of Failure Feature with Stack Trace`,"
                + "`failureStack`:`This is a Test with a failure Stack Trace`}]}")
                .replace('`', '"');

        assertEquals(expectedJson, testString);
    }

    @Test
    public void testDocumentWorkerSerializationTest3() throws Exception
    {
        final DocumentWorkerResult testResult = new DocumentWorkerResult();
        testResult.fieldChanges = new HashMap<>();
        testResult.fieldChanges.put(
            "fieldName", createFieldChanges(
                DocumentWorkerAction.add, createDataList("Deserialization Test", DocumentWorkerFieldEncoding.utf8)));

        final String testString = serialiseResult(testResult);

        final String expectedJson
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

        final DocumentWorkerResult dsTestResult = deserialiseResult(jsonString);

        final DocumentWorkerFieldChanges changeOb = dsTestResult.fieldChanges.get("fieldName");
        final DocumentWorkerFieldValue recoveredData = changeOb.values.get(0);

        assertEquals(DocumentWorkerAction.add, changeOb.action);
        assertEquals("Deserialization Test", recoveredData.data);
        assertEquals(DocumentWorkerFieldEncoding.utf8, recoveredData.encoding);
    }

    @Test
    public void testDocumentWorkerDeserializationTest1() throws Exception
    {
        final String jsonString
            = ("{`fieldChanges`:{`fieldName`:{`action`:`add`,`values`:[{`data`:`Deserialization Test`}]}},"
                + "`failures`:[{`failureId`:`123456`,`failureMessage`:`Test of Failure Feature with Stack Trace`}]}")
                .replace('`', '"');

        final DocumentWorkerResult dsTestResult = deserialiseResult(jsonString);

        final DocumentWorkerFieldChanges changeOb = dsTestResult.fieldChanges.get("fieldName");
        final DocumentWorkerFieldValue recoveredData = changeOb.values.get(0);
        final List<DocumentWorkerFailure> failure = dsTestResult.failures;

        assertEquals(DocumentWorkerAction.add, changeOb.action);
        assertEquals("Deserialization Test", recoveredData.data);
        assertEquals(null, recoveredData.encoding);
        assertEquals("123456", failure.get(0).failureId);
        assertEquals("Test of Failure Feature with Stack Trace", failure.get(0).failureMessage);
    }

    @Test
    public void testDocumentWorkerDeserializationTest2() throws Exception
    {
        final String jsonString
            = ("{`fieldChanges`:{`fieldName`:{`action`:`add`,`values`:[{`data`:`Deserialization Test`}]}},"
                + "`failures`:[{`failureId`:`123456`,`failureMessage`:`Test of Failure Feature with Stack Trace`,"
                + "`failureStack`:`This is a Test with a failure Stack Trace`}]}")
                .replace('`', '"');

        final DocumentWorkerResult dsTestResult = deserialiseResult(jsonString);

        final DocumentWorkerFieldChanges changeOb = dsTestResult.fieldChanges.get("fieldName");
        final DocumentWorkerFieldValue recoveredData = changeOb.values.get(0);
        final List<DocumentWorkerFailure> failure = dsTestResult.failures;

        assertEquals(DocumentWorkerAction.add, changeOb.action);
        assertEquals("Deserialization Test", recoveredData.data);
        assertEquals(null, recoveredData.encoding);
        assertEquals("123456", failure.get(0).failureId);
        assertEquals("Test of Failure Feature with Stack Trace", failure.get(0).failureMessage);
        assertEquals("This is a Test with a failure Stack Trace", failure.get(0).failureStack);
    }

    @Test
    public void testDocumentWorkerDeserializationTest3() throws Exception
    {
        final String jsonString
            = "{`fieldChanges`:{`fieldName`:{`action`:`add`,`values`:[{`data`:`Deserialization Test`}]}}}"
                .replace('`', '"');

        final DocumentWorkerResult dsTestResult = deserialiseResult(jsonString);

        final DocumentWorkerFieldChanges changeOb = dsTestResult.fieldChanges.get("fieldName");
        final DocumentWorkerFieldValue recoveredData = changeOb.values.get(0);

        assertEquals(DocumentWorkerAction.add, changeOb.action);
        assertEquals("Deserialization Test", recoveredData.data);
        assertEquals(null, recoveredData.encoding);
    }
}
