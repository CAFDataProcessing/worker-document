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
package com.hpe.caf.worker.document.testing.hamcrest;

import com.hpe.caf.worker.document.DocumentWorkerFieldEncoding;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.testing.DocumentBuilder;
import static com.hpe.caf.worker.document.testing.hamcrest.DocumentMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import org.junit.Test;

public class DocumentMatchersTest
{
    private static final String FIELD_NAME = "MyField";

    @Test
    public void containsStringValueTest() throws Exception
    {
        final Document document = DocumentBuilder.configure().withFields()
            .addFieldValues(FIELD_NAME, "value1", "value2").documentBuilder().build();

        assertThat(document, containsStringFieldValue(FIELD_NAME, is("value2")));
        assertThat(document, containsStringFieldValue(FIELD_NAME, "value2"));
        assertThat(document, containsStringFieldValue(FIELD_NAME, containsString("lue")));
    }

    @Test
    public void containsByteValueTest() throws Exception
    {
        final byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        final byte[] expectedBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        final Document document = DocumentBuilder.configure().withFields()
            .addFieldValue(FIELD_NAME, bytes).documentBuilder().build();

        assertThat(document, containsByteValue(FIELD_NAME, is(bytes)));
        assertThat(document, containsByteValue(FIELD_NAME, is(expectedBytes)));
        assertThat(document, containsByteValue(FIELD_NAME, bytes));
        assertThat(document, containsByteValue(FIELD_NAME, expectedBytes));
    }

    @Test
    public void containsReferenceTest() throws Exception
    {
        final Document document = DocumentBuilder.configure()
            .withFields()
            .addFieldValue(FIELD_NAME, "my-reference", DocumentWorkerFieldEncoding.storage_ref)
            .documentBuilder()
            .build();

        assertThat(document, containsReference(FIELD_NAME, "my-reference"));
        assertThat(document, containsReference(FIELD_NAME, containsString("ref")));
    }
}
