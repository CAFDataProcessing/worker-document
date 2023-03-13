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
package com.hpe.caf.worker.document.impl;

import com.hpe.caf.api.Codec;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.ConfigurationException;
import com.hpe.caf.api.ConfigurationSource;
import com.hpe.caf.api.worker.DataStore;
import com.hpe.caf.api.worker.TaskFailedException;
import com.hpe.caf.api.worker.WorkerException;
import com.hpe.caf.worker.document.config.DocumentWorkerConfiguration;
import com.hpe.caf.worker.document.model.Application;
import com.hpe.caf.worker.document.model.ServiceLocator;
import com.hpe.caf.worker.document.scripting.JavaScriptManager;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ApplicationImpl implements Application
{
    private final ServiceLocatorImpl serviceLocator;
    private final ConfigurationSource configSource;
    private final DataStore dataStore;
    private final Codec codec;
    private final DocumentWorkerConfiguration configuration;
    private final BatchSizeControllerImpl batchSizeController;
    private final InputMessageProcessorImpl inputMessageProcessor;
    private final JavaScriptManager javaScriptManager;
    private final String successQueue;
    private final String failureQueue;

    public ApplicationImpl(final ConfigurationSource configSource, final DataStore dataStore, final Codec codec)
        throws WorkerException
    {
        this.serviceLocator = new ServiceLocatorImpl(this);
        this.configSource = Objects.requireNonNull(configSource);
        this.dataStore = Objects.requireNonNull(dataStore);
        this.codec = Objects.requireNonNull(codec);
        this.configuration = getConfiguration(configSource);
        this.batchSizeController = new BatchSizeControllerImpl(this, configuration);
        this.inputMessageProcessor = new InputMessageProcessorImpl(this, configuration.getInputMessageProcessing());
        this.javaScriptManager = new JavaScriptManager(configuration.getScriptCaching());
        this.successQueue = configuration.getOutputQueue();
        this.failureQueue = getFailureQueue(configuration);

        // Register services
        serviceLocator.register(Codec.class, codec);
        serviceLocator.register(DataStore.class, dataStore);
        serviceLocator.register(ConfigurationSource.class, configSource);
    }

    @Nonnull
    @Override
    public Application getApplication()
    {
        return this;
    }

    @Nonnull
    @Override
    public BatchSizeControllerImpl getBatchSizeController()
    {
        return batchSizeController;
    }

    @Nonnull
    @Override
    public InputMessageProcessorImpl getInputMessageProcessor()
    {
        return inputMessageProcessor;
    }

    @Override
    public String getName()
    {
        return configuration.getWorkerName();
    }

    @Override
    public <S> S getService(Class<S> service)
    {
        return serviceLocator.getService(service);
    }

    @Nonnull
    @Override
    public ServiceLocator getServiceLocator()
    {
        return serviceLocator;
    }

    @Override
    public String getVersion()
    {
        return configuration.getWorkerVersion();
    }

    @Nonnull
    public ConfigurationSource getConfigSource()
    {
        return configSource;
    }

    @Nonnull
    public DataStore getDataStore()
    {
        return dataStore;
    }

    @Nonnull
    public Codec getCodec()
    {
        return codec;
    }

    @Nonnull
    public DocumentWorkerConfiguration getConfiguration()
    {
        return configuration;
    }

    @Nonnull
    public JavaScriptManager getJavaScriptManager()
    {
        return javaScriptManager;
    }

    public String getSuccessQueue()
    {
        return successQueue;
    }

    public String getFailureQueue()
    {
        return failureQueue;
    }

    public <T> byte[] serialiseResult(final T result)
    {
        try {
            return codec.serialise(result);
        } catch (final CodecException e) {
            throw new TaskFailedException("Failed to serialise result", e);
        }
    }

    /**
     * This method retrieves the DocumentWorkerConfiguration or throws an exception.
     *
     * @return the DocumentWorkerConfiguration object
     * @throws WorkerException if there is a problem creating the configuration object
     */
    @Nonnull
    private static DocumentWorkerConfiguration getConfiguration(final ConfigurationSource configSource)
        throws WorkerException
    {
        try {
            return configSource.getConfiguration(DocumentWorkerConfiguration.class);
        } catch (final ConfigurationException ce) {
            throw new WorkerException("Failed to construct DocumentWorkerConfiguration object", ce);
        }
    }

    private static String getFailureQueue(final DocumentWorkerConfiguration configuration)
    {
        final String failureQueue = configuration.getFailureQueue();

        return (failureQueue == null || failureQueue.isEmpty())
            ? configuration.getOutputQueue()
            : failureQueue;
    }
}
