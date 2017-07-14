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
package com.hpe.caf.worker.document.model;

import com.hpe.caf.worker.document.extensibility.BulkDocumentWorker;
import javax.annotation.Nonnull;

/**
 * Represents the entire service. This object can expose configuration, worker information, and other worker-level details.
 */
public interface Application extends DocumentWorkerObject
{
    /**
     * Returns the batch size controller.
     * <p>
     * This object controls the sizes of the batches passed to implementations of the {@link BulkDocumentWorker} interface.
     *
     * @return the batch size controller
     */
    @Nonnull
    BatchSizeController getBatchSizeController();

    /**
     * Returns the input message processor.
     * <p>
     * This object controls how input messages are processed.
     *
     * @return the input message processor
     */
    @Nonnull
    InputMessageProcessor getInputMessageProcessor();

    /**
     * Returns the specified service, or {@code null} if the service has not been registered.
     * <p>
     * This method can be used to retrieve services provided by the underlying Worker Framework, such as the {@code DataStore} or
     * {@code ConfigurationSource} services.
     * <p>
     * For example, to use the Worker Framework DataStore service:
     * <pre>
     * Add the following dependency to the project POM:
     * {@code
     * <dependency>
     *      <groupId>com.github.workerframework</groupId>
     *      <artifactId>worker-api</artifactId>
     *      <scope>provided</scope>
     *  </dependency>}
     *
     * And then retrieve the service using this method:
     * {@code DataStore dataStore = document.getApplication().getService(DataStore.class);}</pre>
     *
     * @param <S> the type of the service to be returned
     * @param service the interface or abstract class representing the service
     * @return the service provider
     * @see ServiceLocator#getService(Class) ServiceLocator.getService()
     */
    <S> S getService(Class<S> service);

    /**
     * Returns the service locator object. This can be used to retrieve extra services provided by the host framework.
     *
     * @return the service locator
     */
    @Nonnull
    ServiceLocator getServiceLocator();
}
