/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development LP.
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

/**
 * Document worker configuration builder.
 */
public final class DocumentWorkerConfigurationBuilder
{
    private final DocumentWorkerConfiguration configuration;

    private DocumentWorkerConfigurationBuilder(final DocumentWorkerConfiguration configuration)
    {
        this.configuration = configuration;
    }

    /**
     * Configures a DocumentWorkerConfiguration
     *
     * @return This builder.
     */
    public static DocumentWorkerConfigurationBuilder configure()
    {
        return configure(new DocumentWorkerConfiguration());
    }

    /**
     * Configures supplied DocumentWorkerConfiguration.
     *
     * @param documentWorkerConfiguration Document worker configuration.
     * @return This builder.
     */
    public static DocumentWorkerConfigurationBuilder configure(final DocumentWorkerConfiguration documentWorkerConfiguration)
    {
        return new DocumentWorkerConfigurationBuilder(documentWorkerConfiguration);
    }

    /**
     * Builds a configuration object.
     *
     * @return Document Worker configuration.
     */
    public DocumentWorkerConfiguration build()
    {
        return this.configuration;
    }

    /**
     * Sets default values on a worker configuration object.
     *
     * @return This builder.
     */
    public DocumentWorkerConfigurationBuilder withDefaults()
    {
        configuration.setMaxBatchTime(1);
        configuration.setMaxBatchTime(1);
        configuration.setThreads(1);
        configuration.setOutputQueue("output-queue");
        configuration.setWorkerVersion("1.0.0");
        configuration.setWorkerName("worker-name");
        return this;
    }

    public DocumentWorkerConfigurationBuilder withOutputQueue(final String outputQueue)
    {
        this.configuration.setOutputQueue(outputQueue);
        return this;
    }

    public DocumentWorkerConfigurationBuilder withThreads(final int threads)
    {
        this.configuration.setThreads(threads);
        return this;
    }

    public DocumentWorkerConfigurationBuilder withMaxBatchSize(final int maxBatchSize)
    {
        this.configuration.setMaxBatchSize(maxBatchSize);
        return this;
    }

    public DocumentWorkerConfigurationBuilder withMaxBatchTime(final long maxBatchTime)
    {
        this.configuration.setMaxBatchTime(maxBatchTime);
        return this;
    }

    public DocumentWorkerConfigurationBuilder withWorkerName(final String workerName)
    {
        this.configuration.setWorkerName(workerName);
        return this;
    }

    public DocumentWorkerConfigurationBuilder withWorkerVersion(final String workerVersion)
    {
        this.configuration.setWorkerVersion(workerVersion);
        return this;
    }
}
