/*
 * Copyright 2016-2023 Open Text.
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
package com.hpe.caf.worker.document.config;

import com.hpe.caf.api.worker.WorkerConfiguration;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class DocumentWorkerConfiguration extends WorkerConfiguration
{
    /**
     * Output queue to return results to RabbitMQ.
     */
    private String outputQueue;

    /**
     * Failure queue to return failed documents to RabbitMQ.
     */
    private String failureQueue;

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

    /**
     * Configuration for the input message processor
     */
    private InputMessageConfiguration inputMessageProcessing;

    /**
     * Configuration for script caching
     */
    private ScriptCachingConfiguration scriptCaching;

    /**
     * Enable returning Exception on failure
     */
    private boolean enableExceptionOnFailure;

    public String getOutputQueue()
    {
        return outputQueue;
    }

    public void setOutputQueue(final String outputQueue)
    {
        this.outputQueue = outputQueue;
    }

    public String getFailureQueue()
    {
        return failureQueue;
    }

    public void setFailureQueue(final String failureQueue)
    {
        this.failureQueue = failureQueue;
    }

    public int getThreads()
    {
        return threads;
    }

    public void setThreads(final int threads)
    {
        this.threads = threads;
    }

    public int getMaxBatchSize()
    {
        return maxBatchSize;
    }

    public void setMaxBatchSize(final int maxBatchSize)
    {
        this.maxBatchSize = maxBatchSize;
    }

    public long getMaxBatchTime()
    {
        return maxBatchTime;
    }

    public void setMaxBatchTime(final long maxBatchTime)
    {
        this.maxBatchTime = maxBatchTime;
    }

    public InputMessageConfiguration getInputMessageProcessing()
    {
        return inputMessageProcessing;
    }

    public void setInputMessageProcessing(final InputMessageConfiguration inputMessageProcessing)
    {
        this.inputMessageProcessing = inputMessageProcessing;
    }

    public ScriptCachingConfiguration getScriptCaching()
    {
        return scriptCaching;
    }

    public void setScriptCaching(final ScriptCachingConfiguration scriptCaching)
    {
        this.scriptCaching = scriptCaching;
    }

    public boolean getEnableExceptionOnFailure()
    {
        return enableExceptionOnFailure;
    }

    public void setEnableExceptionOnFailure(boolean enableExceptionOnFailure)
    {
        this.enableExceptionOnFailure = enableExceptionOnFailure;
    }
}
