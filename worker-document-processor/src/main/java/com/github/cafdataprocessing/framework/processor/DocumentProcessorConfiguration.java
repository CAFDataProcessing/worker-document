/*
 * Copyright 2016-2019 Micro Focus or one of its affiliates.
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
package com.github.cafdataprocessing.framework.processor;

import com.hpe.caf.worker.document.config.InputMessageConfiguration;
import com.hpe.caf.worker.document.config.ScriptCachingConfiguration;

public final class DocumentProcessorConfiguration
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
     * Name to use for the change log entry.
     */
    private String changeLogEntryName;

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

    public String getChangeLogEntryName()
    {
        return changeLogEntryName;
    }

    public void setChangeLogEntryName(final String changeLogEntryName)
    {
        this.changeLogEntryName = changeLogEntryName;
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
}
