package com.hpe.caf.worker.document.testing;

import com.hpe.caf.worker.document.DocumentWorkerConfiguration;

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
