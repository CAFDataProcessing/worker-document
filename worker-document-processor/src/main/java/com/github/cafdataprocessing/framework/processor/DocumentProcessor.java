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

import com.hpe.caf.worker.document.DocumentWorkerDocumentTask;
import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.exceptions.InvalidChangeLogException;
import com.hpe.caf.worker.document.exceptions.InvalidScriptException;
import com.hpe.caf.worker.document.extensibility.BulkDocumentWorker;
import com.hpe.caf.worker.document.extensibility.DocumentWorker;
import com.hpe.caf.worker.document.extensibility.DocumentWorkerFactory;
import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.model.Application;
import com.hpe.caf.worker.document.tasks.DocumentTask;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DocumentProcessor implements AutoCloseable
{
    private static final Logger LOG = LoggerFactory.getLogger(DocumentProcessor.class);

    private final ApplicationImpl application;
    private final DocumentWorker documentWorker;

    DocumentProcessor(
        final DocumentWorkerFactory workerFactory,
        final RemoteDataStore dataStore,
        final DocumentProcessorConfiguration config,
        final ServiceProvider serviceProvider
    ) throws FailedToInstantiateWorkerException
    {
        // Check the arguments have been provided
        Objects.requireNonNull(workerFactory);
        Objects.requireNonNull(dataStore);
        Objects.requireNonNull(config);
        Objects.requireNonNull(serviceProvider);

        // Construct the application object
        this.application = new ApplicationImpl(config, dataStore, serviceProvider);

        // Construct the DocumentWorker implementation object
        this.documentWorker = createDocumentWorker(workerFactory, application);
    }

    @Override
    public void close()
    {
        try {
            documentWorker.close();
        } catch (final RuntimeException ex) {
            throw ex;
        } catch (final Exception ex) {
            LOG.warn("Error closing DocumentWorker during shutdown", ex);
        }
    }

    public boolean getBulkSupport()
    {
        return (documentWorker instanceof BulkDocumentWorker);
    }

    @Nonnull
    public TaskProcessor getTaskProcessor(
        final DocumentWorkerDocumentTask task
    ) throws InvalidTaskException
    {
        return getTaskProcessor(task, null);
    }

    @Nonnull
    public TaskProcessor getTaskProcessor(
        final DocumentWorkerDocumentTask task,
        final ServiceProvider taskServiceProvider
    ) throws InvalidTaskException
    {
        // Check the arguments have been provided
        Objects.requireNonNull(task);

        // Construct the task object
        final DocumentTask documentTask = createDocumentTask(task, Defaults.defaultIfNull(taskServiceProvider));

        // Create and return the task processor for the task
        return new TaskProcessor(application, documentWorker, documentTask);
    }

    @Nonnull
    public TaskResult processTask(final DocumentWorkerDocumentTask task)
        throws DocumentWorkerTransientException, InterruptedException, InvalidTaskException
    {
        return getTaskProcessor(task).process();
    }

    @Nonnull
    public TaskResult processTask(
        final DocumentWorkerDocumentTask task,
        final ServiceProvider taskServiceProvider
    ) throws DocumentWorkerTransientException, InterruptedException, InvalidTaskException
    {
        return getTaskProcessor(task, taskServiceProvider).process();
    }

    public void processTasks(final DequeueFunction<WorkerTask> dequeueFn) throws DocumentWorkerTransientException, InterruptedException
    {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void verifyHealthy() throws DocumentWorkerUnhealthyException
    {
        final HealthMonitorImpl healthMonitor = new HealthMonitorImpl(application);
        documentWorker.checkHealth(healthMonitor);

        healthMonitor.throwIfUnhealthy();
    }

    @Nonnull
    private DocumentTask createDocumentTask(
        final DocumentWorkerDocumentTask task,
        final ServiceProvider taskServiceProvider
    ) throws InvalidTaskException
    {
        try {
            return DocumentTask.create(application, taskServiceProvider, task);
        } catch (final InvalidChangeLogException ex) {
            throw new InvalidTaskException("Invalid change log", ex);
        } catch (final InvalidScriptException ex) {
            throw new InvalidTaskException("Invalid script", ex);
        }
    }

    @Nonnull
    private static DocumentWorker createDocumentWorker(final DocumentWorkerFactory workerFactory, final Application application)
        throws FailedToInstantiateWorkerException
    {
        final DocumentWorker documentWorker = workerFactory.createDocumentWorker(application);

        if (documentWorker == null) {
            throw new FailedToInstantiateWorkerException();
        }

        return documentWorker;
    }
}
