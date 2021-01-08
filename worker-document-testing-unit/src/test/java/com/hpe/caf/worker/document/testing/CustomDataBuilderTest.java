/*
 * Copyright 2016-2021 Micro Focus or one of its affiliates.
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

import java.util.HashMap;
import java.util.Map;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class CustomDataBuilderTest
{
    @Test
    public void testAdd() throws Exception
    {
        final Map<String, String> map = new HashMap<>();
        final CustomDataBuilder builder = new CustomDataBuilder(map, null);

        builder.add("data-1", "value-1").add("data-2", "value-2");

        assertThat(map.size(), is(2));
        assertThat(map, hasEntry("data-1", "value-1"));
        assertThat(map, hasEntry("data-2", "value-2"));
    }
}
