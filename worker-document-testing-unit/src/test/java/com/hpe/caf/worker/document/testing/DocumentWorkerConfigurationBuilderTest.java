package com.hpe.caf.worker.document.testing;

import com.hpe.caf.worker.document.DocumentWorkerConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DocumentWorkerConfigurationBuilderTest
{
    @Test
    public void buildTest() throws Exception
    {
        DocumentWorkerConfiguration configuration = DocumentWorkerConfigurationBuilder.configure()
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
