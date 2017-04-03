package com.hpe.caf.worker.document.testing;

import com.hpe.caf.api.worker.WorkerException;
import com.hpe.caf.worker.document.DocumentWorkerFieldEncoding;
import com.hpe.caf.worker.document.DocumentWorkerFieldValue;
import com.hpe.caf.worker.document.DocumentWorkerTask;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.Field;
import com.hpe.caf.worker.document.model.FieldValue;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DocumentBuilderTest
{
    private static void assertDocumentFields(Document document)
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

    private static DocumentWorkerTask createTestTask()
    {
        DocumentWorkerTask task = new DocumentWorkerTask();

        task.fields = new HashMap<String, List<DocumentWorkerFieldValue>>()
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

    private static DocumentWorkerFieldValue createValue(String data, DocumentWorkerFieldEncoding encoding)
    {
        DocumentWorkerFieldValue value = new DocumentWorkerFieldValue();
        value.data = data;
        value.encoding = encoding;
        return value;
    }

    @Test
    public void testBuildDocumentWithFieldsMap() throws Exception
    {
        DocumentWorkerTask task = createTestTask();
        Document document = DocumentBuilder.configure().withFields(task.fields).build();
        assertDocumentFields(document);
    }

    @Test
    public void testWithFieldBuilder() throws Exception
    {
        Document document = DocumentBuilder.configure().withFields()
            .addField("field1").addValue("value1-1").addValue("value1-2").then()
            .addField("field2")
            .addValue(Base64.encodeBase64String("value2-1".getBytes()), DocumentWorkerFieldEncoding.base64)
            .addValue("value2-2", DocumentWorkerFieldEncoding.storage_ref).then().documentBuilder().build();

        assertDocumentFields(document);
    }

    @Test
    public void testFromJsonFile() throws URISyntaxException, IOException, WorkerException
    {
        URL resource = this.getClass().getResource("/test-task.json");
        Document document = DocumentBuilder.fromFile(Paths.get(new URI(resource.toString())).toString()).build();
        assertDocumentFields(document);
    }

    @Test
    public void testFromYamlFile() throws URISyntaxException, IOException, WorkerException
    {
        URL resource = this.getClass().getResource("/test-task.yaml");
        Document document = DocumentBuilder.fromFile(Paths.get(new URI(resource.toString())).toString()).build();
        assertDocumentFields(document);
    }
}
