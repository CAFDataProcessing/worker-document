/*
 * Copyright 2018-2017 EntIT Software LLC, a Micro Focus company.
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
package com.hpe.caf.worker.document.testing;

import com.hpe.caf.worker.document.DocumentWorkerFieldEncoding;
import com.hpe.caf.worker.document.DocumentWorkerFieldValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class FieldsBuilderTest
{
    @Test
    public void testAddFieldValue() throws Exception
    {
        final Map<String, List<DocumentWorkerFieldValue>> map = new HashMap<>();
        final FieldsBuilder builder = new FieldsBuilder(map, null);

        builder
            .addFieldValue("field1", "value1")
            .addFieldValue("field2", "value2")
            .addFieldValue("field2", "value3", DocumentWorkerFieldEncoding.storage_ref);

        assertThat(map, hasKey("field1"));
        final List<DocumentWorkerFieldValue> valueList = map.get("field1");
        assertThat(valueList, hasSize(1));
        assertThat(valueList.get(0).data, is("value1"));
        assertThat(valueList.get(0).encoding, nullValue());

        assertThat(map, hasKey("field2"));
        final List<DocumentWorkerFieldValue> valueList2 = map.get("field2");
        assertThat(valueList2, hasSize(2));
        assertThat(valueList2.get(0).data, is("value2"));
        assertThat(valueList2.get(0).encoding, nullValue());
        assertThat(valueList2.get(1).data, is("value3"));
        assertThat(valueList2.get(1).encoding, is(DocumentWorkerFieldEncoding.storage_ref));
    }

    @Test
    public void testAddBase64FieldValue() throws Exception
    {
        final Map<String, List<DocumentWorkerFieldValue>> map = new HashMap<>();
        final FieldsBuilder builder = new FieldsBuilder(map, null);

        final String testContent = UUID.randomUUID().toString();

        builder.addFieldValue("field1", testContent.getBytes());

        assertThat(map.size(), is(1));

        final List<DocumentWorkerFieldValue> field1 = map.get("field1");
        assertThat(field1, notNullValue());
        assertThat(field1, hasSize(1));
        final DocumentWorkerFieldValue documentWorkerFieldValue = field1.get(0);
        assertThat(new String(Base64.decodeBase64(documentWorkerFieldValue.data)), is(testContent));
    }

    @Test
    public void testConfigureMultivalueFieldUsingFieldValueInnerBuilder() throws Exception
    {
        final Map<String, List<DocumentWorkerFieldValue>> map = new HashMap<>();
        final FieldsBuilder builder = new FieldsBuilder(map, null);

        builder.addField("field1")
            .addValue("value1", null)
            .addValue("value2", null)
            .addValue("value3", DocumentWorkerFieldEncoding.base64)
            .then()
            .addFieldValue("field2", "value2") /*.build()*/;

        assertThat(map.size(), is(2));

        // First field - 'field1'
        assertThat(map, hasKey("field1"));
        List<DocumentWorkerFieldValue> valueList = map.get("field1");

        assertThat(valueList, hasSize(3));
        assertThat(valueList.get(0).data, is("value1"));
        assertThat(valueList.get(0).encoding, nullValue());
        assertThat(valueList.get(1).data, is("value2"));
        assertThat(valueList.get(1).encoding, nullValue());
        assertThat(valueList.get(2).data, is("value3"));
        assertThat(valueList.get(2).encoding, is(DocumentWorkerFieldEncoding.base64));

        // Second field - 'field2'
        assertThat(map, hasKey("field2"));
        valueList = map.get("field2");

        assertThat(valueList, hasSize(1));
        assertThat(valueList.get(0).data, is("value2"));
        assertThat(valueList.get(0).encoding, nullValue());
    }
}
