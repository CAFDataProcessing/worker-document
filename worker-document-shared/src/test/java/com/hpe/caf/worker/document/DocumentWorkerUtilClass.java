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

    static DocumentWorkerData createData(String data, DocumentWorkerEncoding encoding)
    {
        DocumentWorkerData documentWorkerData = new DocumentWorkerData();
        documentWorkerData.data = data;
        documentWorkerData.encoding = encoding;
        return documentWorkerData;
    }

    static DocumentWorkerData createData(String data)
    {
        DocumentWorkerData documentWorkerData = new DocumentWorkerData();
        documentWorkerData.data = data;
        return documentWorkerData;
    }

    static List<DocumentWorkerData> createDataList(String data, DocumentWorkerEncoding encoding)
    {
        List<DocumentWorkerData> documentWorkerDataList = new ArrayList<>();
        DocumentWorkerData documentWorkerData = createData(data, encoding);
        documentWorkerDataList.add(documentWorkerData);
        return documentWorkerDataList;
    }

    static List<DocumentWorkerData> createDataList(String data)
    {
        List<DocumentWorkerData> documentWorkerDataList = new ArrayList<>();
        DocumentWorkerData documentWorkerData = createData(data);
        documentWorkerDataList.add(documentWorkerData);
        return documentWorkerDataList;
    }

    static DocumentWorkerFieldChanges createFieldChanges(DocumentWorkerAction action, List<DocumentWorkerData> values)
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
