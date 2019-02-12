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
import com.hpe.caf.worker.document.extensibility.DocumentWorkerFactory;
import javax.annotation.Nonnull;

final class Defaults
{
    private Defaults()
    {
    }

    @Nonnull
    public static RemoteDataStore defaultIfNull(final RemoteDataStore dataStore)
    {
        return (dataStore == null) ? NullRemoteDataStore.INSTANCE : dataStore;
    }

    @Nonnull
    public static ServiceProvider defaultIfNull(final ServiceProvider serviceProvider)
    {
        return (serviceProvider == null) ? NullServiceProvider.INSTANCE : serviceProvider;
    }

    @Nonnull
    public static DocumentWorkerFactory defaultIfNull(final DocumentWorkerFactory workerFactory)
    {
        return (workerFactory == null) ? ServiceLoadedDocumentWorkerFactory.INSTANCE : workerFactory;
    }

    @Nonnull
    public static DocumentProcessorConfiguration defaultIfNull(final DocumentProcessorConfiguration config)
    {
        if (config != null) {
            return config;
        }

        final InputMessageConfiguration inputMessageConfiguration = new InputMessageConfiguration();
        inputMessageConfiguration.setProcessSubdocumentsSeparately(Boolean.FALSE);

        final DocumentProcessorConfiguration defaultConfig = new DocumentProcessorConfiguration();
        defaultConfig.setInputMessageProcessing(inputMessageConfiguration);

        return defaultConfig;
    }
}
