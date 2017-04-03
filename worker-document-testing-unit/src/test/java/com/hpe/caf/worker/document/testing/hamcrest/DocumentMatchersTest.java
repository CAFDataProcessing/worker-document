package com.hpe.caf.worker.document.testing.hamcrest;

import com.hpe.caf.worker.document.DocumentWorkerFieldEncoding;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.testing.DocumentBuilder;
import org.junit.Test;

import static com.hpe.caf.worker.document.testing.hamcrest.DocumentMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class DocumentMatchersTest
{
    private static final String FIELD_NAME = "MyField";

    @Test
    public void containsStringValueTest() throws Exception
    {
        Document document = DocumentBuilder.configure().withFields()
            .addFieldValues(FIELD_NAME, "value1", "value2").documentBuilder().build();

        assertThat(document, containsStringFieldValue(FIELD_NAME, is("value2")));
        assertThat(document, containsStringFieldValue(FIELD_NAME, "value2"));
        assertThat(document, containsStringFieldValue(FIELD_NAME, containsString("lue")));
    }

    @Test
    public void containsByteValueTest() throws Exception
    {
        byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        byte[] expectedBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        Document document = DocumentBuilder.configure().withFields()
            .addFieldValue(FIELD_NAME, bytes).documentBuilder().build();

        assertThat(document, containsByteValue(FIELD_NAME, is(bytes)));
        assertThat(document, containsByteValue(FIELD_NAME, is(expectedBytes)));
        assertThat(document, containsByteValue(FIELD_NAME, bytes));
        assertThat(document, containsByteValue(FIELD_NAME, expectedBytes));
    }

    @Test
    public void containsReferenceTest() throws Exception
    {
        Document document = DocumentBuilder.configure()
            .withFields()
            .addFieldValue(FIELD_NAME, "my-reference", DocumentWorkerFieldEncoding.storage_ref)
            .documentBuilder()
            .build();

        assertThat(document, containsReference(FIELD_NAME, "my-reference"));
        assertThat(document, containsReference(FIELD_NAME, containsString("ref")));
    }
}
