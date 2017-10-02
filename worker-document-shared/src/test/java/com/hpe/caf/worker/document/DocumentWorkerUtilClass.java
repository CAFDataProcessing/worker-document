/*
 * Copyright 2015-2017 EntIT Software LLC, a Micro Focus company.
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
