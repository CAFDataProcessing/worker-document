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

import com.hpe.caf.worker.document.config.DocumentWorkerConfiguration;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class DocumentWorkerConfigurationBuilderTest
{
    @Test
    public void buildTest() throws Exception
    {
        final DocumentWorkerConfiguration configuration = DocumentWorkerConfigurationBuilder.configure()
            .withMaxBatchSize(5)
            .withMaxBatchTime(100)
            .withOutputQueue("out-queue")
            .withThreads(2)
            .withWorkerName("worker1")
            .withWorkerVersion("123").build();

        assertEquals(5, configuration.getMaxBatchSize());
        assertEquals(100, configuration.getMaxBatchTime());
        assertEquals("out-queue", configuration.getOutputQueue());
        assertEquals(2, configuration.getThreads());
        assertEquals("worker1", configuration.getWorkerName());
        assertEquals("123", configuration.getWorkerVersion());
    }
}
