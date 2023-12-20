/*
 * Copyright 2016-2024 Open Text.
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

    static DocumentWorkerFieldValue createData(final String data, final DocumentWorkerFieldEncoding encoding)
    {
        final DocumentWorkerFieldValue documentWorkerFieldValue = new DocumentWorkerFieldValue();
        documentWorkerFieldValue.data = data;
        documentWorkerFieldValue.encoding = encoding;
        return documentWorkerFieldValue;
    }

    static DocumentWorkerFieldValue createData(final String data)
    {
        final DocumentWorkerFieldValue documentWorkerFieldValue = new DocumentWorkerFieldValue();
        documentWorkerFieldValue.data = data;
        return documentWorkerFieldValue;
    }

    static List<DocumentWorkerFieldValue> createDataList(final String data, final DocumentWorkerFieldEncoding encoding)
    {
        final List<DocumentWorkerFieldValue> documentWorkerFieldValueList = new ArrayList<>();
        final DocumentWorkerFieldValue documentWorkerFieldValue = createData(data, encoding);
        documentWorkerFieldValueList.add(documentWorkerFieldValue);
        return documentWorkerFieldValueList;
    }

    static List<DocumentWorkerFieldValue> createDataList(final String data)
    {
        final List<DocumentWorkerFieldValue> documentWorkerFieldValueList = new ArrayList<>();
        final DocumentWorkerFieldValue documentWorkerFieldValue = createData(data);
        documentWorkerFieldValueList.add(documentWorkerFieldValue);
        return documentWorkerFieldValueList;
    }

    static DocumentWorkerFieldChanges createFieldChanges(final DocumentWorkerAction action, final List<DocumentWorkerFieldValue> values)
    {
        final DocumentWorkerFieldChanges documentWorkerFieldChanges = new DocumentWorkerFieldChanges();
        documentWorkerFieldChanges.action = action;
        documentWorkerFieldChanges.values = values;
        return documentWorkerFieldChanges;
    }

    static String serialiseTask(final DocumentWorkerTask testTask) throws CodecException
    {
        final JsonCodec jsonCodec = new JsonCodec();
        final byte[] testByteArray = jsonCodec.serialise(testTask);
        final String testString = new String(testByteArray, StandardCharsets.UTF_8);
        return testString;
    }

    static DocumentWorkerTask deserialiseTask(final String jsonString) throws CodecException
    {
        final JsonCodec jsonCodec = new JsonCodec();
        final byte[] testByteArray = jsonString.getBytes(StandardCharsets.UTF_8);
        final DocumentWorkerTask dsTestTask = jsonCodec.deserialise(testByteArray, DocumentWorkerTask.class);
        return dsTestTask;
    }

    static String serialiseResult(final DocumentWorkerResult testResult) throws CodecException
    {
        final JsonCodec jsonCodec = new JsonCodec();
        final byte[] testByteArray = jsonCodec.serialise(testResult);
        final String testString = new String(testByteArray, StandardCharsets.UTF_8);
        return testString;
    }

    static DocumentWorkerResult deserialiseResult(final String jsonString) throws CodecException
    {
        final JsonCodec jsonCodec = new JsonCodec();
        final byte[] testByteArray = jsonString.getBytes(StandardCharsets.UTF_8);
        final DocumentWorkerResult dsTestResult = jsonCodec.deserialise(testByteArray, DocumentWorkerResult.class);
        return dsTestResult;
    }
}
