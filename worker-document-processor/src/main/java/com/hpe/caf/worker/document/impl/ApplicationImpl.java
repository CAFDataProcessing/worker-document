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
package com.hpe.caf.worker.document.impl;

import com.github.cafdataprocessing.framework.processor.DocumentProcessorConfiguration;
import com.github.cafdataprocessing.framework.processor.RemoteDataStore;
import com.github.cafdataprocessing.framework.processor.ServiceProvider;
import com.hpe.caf.worker.document.model.Application;
import com.hpe.caf.worker.document.model.ServiceLocator;
import com.hpe.caf.worker.document.scripting.JavaScriptManager;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ApplicationImpl implements Application
{
    private final ServiceLocatorImpl serviceLocator;
    private final RemoteDataStore dataStore;
    private final DocumentProcessorConfiguration configuration;
    private final BatchSizeControllerImpl batchSizeController;
    private final InputMessageProcessorImpl inputMessageProcessor;
    private final JavaScriptManager javaScriptManager;
    private final String successQueue;
    private final String failureQueue;

    public ApplicationImpl(
        final DocumentProcessorConfiguration config,
        final RemoteDataStore dataStore,
        final ServiceProvider serviceProvider
    )
    {
        this.serviceLocator = new ServiceLocatorImpl(this, serviceProvider);
        this.dataStore = Objects.requireNonNull(dataStore);
        this.configuration = Objects.requireNonNull(config);
        this.batchSizeController = new BatchSizeControllerImpl(this, configuration);
        this.inputMessageProcessor = new InputMessageProcessorImpl(this, configuration.getInputMessageProcessing());
        this.javaScriptManager = new JavaScriptManager(configuration.getScriptCaching());
        this.successQueue = configuration.getOutputQueue();
        this.failureQueue = getFailureQueue(configuration);
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

    @Nonnull
    public RemoteDataStore getDataStore()
    {
        return dataStore;
    }

    @Nonnull
    public DocumentProcessorConfiguration getConfiguration()
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

    private static String getFailureQueue(final DocumentProcessorConfiguration configuration)
    {
        final String failureQueue = configuration.getFailureQueue();

        return (failureQueue == null || failureQueue.isEmpty())
            ? configuration.getOutputQueue()
            : failureQueue;
    }
}
