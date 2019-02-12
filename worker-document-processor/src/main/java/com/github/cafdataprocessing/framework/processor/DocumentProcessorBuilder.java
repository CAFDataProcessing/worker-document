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

import com.hpe.caf.worker.document.extensibility.DocumentWorkerFactory;
import javax.annotation.Nonnull;

public final class DocumentProcessorBuilder
{
    private DocumentProcessorConfiguration config;
    private RemoteDataStore dataStore;
    private ServiceProvider serviceProvider;
    private DocumentWorkerFactory workerFactory;

    public DocumentProcessorBuilder()
    {
        this.config = null;
        this.dataStore = null;
        this.serviceProvider = null;
        this.workerFactory = null;
    }

    @Nonnull
    public DocumentProcessor build() throws FailedToInstantiateWorkerException
    {
        return new DocumentProcessor(
            Defaults.defaultIfNull(workerFactory),
            Defaults.defaultIfNull(dataStore),
            Defaults.defaultIfNull(config),
            Defaults.defaultIfNull(serviceProvider));
    }

    @Nonnull
    public DocumentProcessorBuilder setConfiguration(final DocumentProcessorConfiguration config)
    {
        this.config = config;
        return this;
    }

    @Nonnull
    public DocumentProcessorBuilder setRemoteDataStore(final RemoteDataStore dataStore)
    {
        this.dataStore = dataStore;
        return this;
    }

    @Nonnull
    public DocumentProcessorBuilder setServiceProvider(final ServiceProvider serviceProvider)
    {
        this.serviceProvider = serviceProvider;
        return this;
    }

    @Nonnull
    public DocumentProcessorBuilder setWorkerFactory(final DocumentWorkerFactory workerFactory)
    {
        this.workerFactory = workerFactory;
        return this;
    }
}
