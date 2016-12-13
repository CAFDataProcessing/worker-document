/*
 * copyright 2016 Hewlett Packard Enterprise
 */
package com.hpe.caf.worker.document;

import com.hpe.caf.api.CodecException;
import com.hpe.caf.codec.JsonCodec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class DocumentWorkerUtilClass
{
    private DocumentWorkerUtilClass()
    {
    }

    static DocumentWorkerFieldValue createData(String data, DocumentWorkerFieldEncoding encoding)
    {
        DocumentWorkerFieldValue documentWorkerFieldValue = new DocumentWorkerFieldValue();
        documentWorkerFieldValue.data = data;
        documentWorkerFieldValue.encoding = encoding;
        return documentWorkerFieldValue;
    }

    static DocumentWorkerFieldValue createData(String data)
    {
        DocumentWorkerFieldValue documentWorkerFieldValue = new DocumentWorkerFieldValue();
        documentWorkerFieldValue.data = data;
        return documentWorkerFieldValue;
    }

    static List<DocumentWorkerFieldValue> createDataList(String data, DocumentWorkerFieldEncoding encoding)
    {
        List<DocumentWorkerFieldValue> documentWorkerFieldValueList = new ArrayList<>();
        DocumentWorkerFieldValue documentWorkerFieldValue = createData(data, encoding);
        documentWorkerFieldValueList.add(documentWorkerFieldValue);
        return documentWorkerFieldValueList;
    }

    static List<DocumentWorkerFieldValue> createDataList(String data)
    {
        List<DocumentWorkerFieldValue> documentWorkerFieldValueList = new ArrayList<>();
        DocumentWorkerFieldValue documentWorkerFieldValue = createData(data);
        documentWorkerFieldValueList.add(documentWorkerFieldValue);
        return documentWorkerFieldValueList;
    }

    static DocumentWorkerFieldChanges createFieldChanges(DocumentWorkerAction action, List<DocumentWorkerFieldValue> values)
    {
        DocumentWorkerFieldChanges documentWorkerFieldChanges = new DocumentWorkerFieldChanges();
        documentWorkerFieldChanges.action = action;
        documentWorkerFieldChanges.values = values;
        return documentWorkerFieldChanges;
    }

    static String serialiseTask(DocumentWorkerTask testTask) throws CodecException
    {
        JsonCodec jsonCodec = new JsonCodec();
        byte[] testByteArray = jsonCodec.serialise(testTask);
        String testString = new String(testByteArray, StandardCharsets.UTF_8);
        return testString;
    }

    static DocumentWorkerTask deserialiseTask(String jsonString) throws CodecException
    {
        JsonCodec jsonCodec = new JsonCodec();
        byte[] testByteArray = jsonString.getBytes(StandardCharsets.UTF_8);
        DocumentWorkerTask dsTestTask = jsonCodec.deserialise(testByteArray, DocumentWorkerTask.class);
        return dsTestTask;
    }

    static String serialiseResult(DocumentWorkerResult testResult) throws CodecException
    {
        JsonCodec jsonCodec = new JsonCodec();
        byte[] testByteArray = jsonCodec.serialise(testResult);
        String testString = new String(testByteArray, StandardCharsets.UTF_8);
        return testString;
    }

    static DocumentWorkerResult deserialiseResult(String jsonString) throws CodecException
    {
        JsonCodec jsonCodec = new JsonCodec();
        byte[] testByteArray = jsonString.getBytes(StandardCharsets.UTF_8);
        DocumentWorkerResult dsTestResult = jsonCodec.deserialise(testByteArray, DocumentWorkerResult.class);
        return dsTestResult;
    }
}
