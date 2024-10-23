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
package com.github.cafdataprocessing.worker.document.testing;

import com.github.cafdataprocessing.worker.document.DocumentWorkerDocument;
import com.github.cafdataprocessing.worker.document.DocumentWorkerDocumentTask;
import com.github.cafdataprocessing.worker.document.DocumentWorkerFieldEncoding;
import com.github.cafdataprocessing.worker.document.DocumentWorkerFieldValue;
import com.github.workerframework.worker.api.WorkerException;
import com.github.cafdataprocessing.worker.document.model.Document;
import com.github.cafdataprocessing.worker.document.model.Field;
import com.github.cafdataprocessing.worker.document.model.FieldValue;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;

public class DocumentBuilderTest
{
    private static void assertDocumentFields(final Document document)
    {
        // ***************************
        // * Assertions for "field1"
        // ***************************
        Field field = document.getField("field1");

        assertThat(field.getName(), is("field1"));
        assertThat(field.getValues().size(), is(2));
        Iterator<FieldValue> iterator = field.getValues().iterator();

        FieldValue next = iterator.next();
        String value = next.getStringValue();
        assertThat(value, is("value1-1"));

        next = iterator.next();
        value = next.getStringValue();
        assertThat(value, is("value1-2"));

        // ***************************
        // * Assertions for "field2"
        // ***************************
        field = document.getField("field2");

        assertThat(field.getName(), is("field2"));
        assertThat(field.getValues().size(), is(2));
        iterator = field.getValues().iterator();

        next = iterator.next();
        value = next.getStringValue();
        assertThat(value, is("value2-1"));

        next = iterator.next();
        value = next.getReference();
        assertThat(value, is("value2-2"));
    }

    private static DocumentWorkerDocumentTask createTestTask()
    {
        final DocumentWorkerDocumentTask task = new DocumentWorkerDocumentTask();
        task.document = new DocumentWorkerDocument();

        task.document.fields = new HashMap<String, List<DocumentWorkerFieldValue>>()
        {
            {
                put("field1",
                    new ArrayList<DocumentWorkerFieldValue>()
                {
                    {
                        add(createValue("value1-1", null));
                        add(createValue("value1-2", null));
                    }
                }
                );
                put("field2",
                    new ArrayList<DocumentWorkerFieldValue>()
                {
                    {
                        add(createValue(Base64.encodeBase64String("value2-1".getBytes()), DocumentWorkerFieldEncoding.base64));
                        add(createValue("value2-2", DocumentWorkerFieldEncoding.storage_ref));
                    }
                }
                );
            }
        };

        task.customData = new HashMap<String, String>()
        {
            {
                put("data1", "value1");
                put("data2", "value2");
            }
        };

        return task;
    }

    private static DocumentWorkerFieldValue createValue(final String data, final DocumentWorkerFieldEncoding encoding)
    {
        DocumentWorkerFieldValue value = new DocumentWorkerFieldValue();
        value.data = data;
        value.encoding = encoding;
        return value;
    }

    @Test
    public void testBuildDocumentWithFieldsMap() throws Exception
    {
        DocumentWorkerDocumentTask task = createTestTask();
        Document document = DocumentBuilder.configure().withFields(task.document.fields).build();
        assertDocumentFields(document);
    }

    @Test
    public void testWithFieldBuilder() throws Exception
    {
        final Document document = DocumentBuilder.configure().withFields()
            .addField("field1").addValue("value1-1").addValue("value1-2").then()
            .addField("field2")
            .addValue(Base64.encodeBase64String("value2-1".getBytes()), DocumentWorkerFieldEncoding.base64)
            .addValue("value2-2", DocumentWorkerFieldEncoding.storage_ref).then().documentBuilder().build();

        assertDocumentFields(document);
    }

    @Test
    public void testFromJsonFile() throws URISyntaxException, IOException, WorkerException
    {
        final URL resource = this.getClass().getResource("/test-task.json");
        final Document document = DocumentBuilder.fromFile(Paths.get(new URI(resource.toString())).toString()).build();
        assertDocumentFields(document);
    }

    @Test
    public void testFromYamlFile() throws URISyntaxException, IOException, WorkerException
    {
        final URL resource = this.getClass().getResource("/test-task.yaml");
        final Document document = DocumentBuilder.fromFile(Paths.get(new URI(resource.toString())).toString()).build();
        assertDocumentFields(document);
    }
}
