package com.hpe.caf.worker.document;

import com.hpe.caf.api.worker.WorkerConfiguration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class DocumentWorkerConfiguration extends WorkerConfiguration
{
    /**
     * Output queue to return results to RabbitMQ.
     */
    private String outputQueue;

    /**
     * Number of threads to use in the worker.
     */
    @Min(1)
    @Max(20)
    private int threads;

    /**
     * Maximum number of documents to include in a batch.
     */
    private int maxBatchSize;

    /**
     * Maximum length of time (in milliseconds) to build up a batch.
     */
    private long maxBatchTime;

    public DocumentWorkerConfiguration()
    {
    }

    public String getOutputQueue()
    {
        return outputQueue;
    }

    public void setOutputQueue(String outputQueue)
    {
        this.outputQueue = outputQueue;
    }

    public int getThreads()
    {
        return threads;
    }

    public void setThreads(int threads)
    {
        this.threads = threads;
    }

    public int getMaxBatchSize()
    {
        return maxBatchSize;
    }

    public void setMaxBatchSize(int maxBatchSize)
    {
        this.maxBatchSize = maxBatchSize;
    }

    public long getMaxBatchTime()
    {
        return maxBatchTime;
    }

    public void setMaxBatchTime(long maxBatchTime)
    {
        this.maxBatchTime = maxBatchTime;
    }
}
