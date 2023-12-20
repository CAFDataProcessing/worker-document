/*
 * Copyright 2016-2024 Open Text.
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

import jakarta.annotation.Nonnull;

/**
 * Represents the document processing task.
 * <p>
 * The task includes a document or a partial document to be processed, as well as some custom data which may include processing
 * instructions.
 */
public interface Task extends DocumentWorkerObject
{
    /**
     * Used to retrieve any custom data that was sent with the task. The custom data could be used to send processing instructions to the
     * worker, to affect how the document is processed.
     *
     * @param dataKey the key of the data to be retrieved (note that the key lookup is case-sensitive)
     * @return the value retrieved if it was sent, or null if no data was sent with the specified key
     */
    String getCustomData(String dataKey);

    /**
     * Returns the document to be processed by this task.
     *
     * @return the document to be processed by this task
     */
    @Nonnull
    Document getDocument();

    /**
     * Gets the list of customization scripts associated with this task.
     * <p>
     * Scripts are ordered and can be accessed by index.
     *
     * @return an object which can be used to access the list of scripts
     */
    @Nonnull
    Scripts getScripts();

    /**
     * Returns the specified task-level service, or {@code null} if the service has not been registered.
     * <p>
     * This method can be used to access task-level services that are provided by the underlying Worker Framework but not exposed by the
     * Document Worker Framework.
     * <p>
     * For example, to access the Worker Framework {@code WorkerTaskData} object add the following dependency to the project POM:
     * <pre>{@code  <dependency>
     *      <groupId>com.github.workerframework</groupId>
     *      <artifactId>worker-api</artifactId>
     *      <scope>provided</scope>
     *  </dependency>}</pre>
     *
     * and then retrieve the object using this method:
     * <pre>{@code  WorkerTaskData workerTask = documentTask.getService(WorkerTaskData.class);}</pre>
     *
     * @param <S> the type of the service to be returned
     * @param service the interface or abstract class representing the service
     * @return the service provider
     * @see Application#getService(Class) Application.getService()
     */
    <S> S getService(Class<S> service);

    /**
     * Returns an object which can be used to customise the response to the document processing task.
     *
     * @return the response customization object for this task
     */
    @Nonnull
    Response getResponse();
}
