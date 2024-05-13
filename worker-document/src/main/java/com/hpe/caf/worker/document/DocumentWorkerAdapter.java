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
package com.hpe.caf.worker.document;

import com.hpe.caf.api.HealthResult;
import com.hpe.caf.api.worker.InvalidTaskException;
import com.hpe.caf.api.worker.TaskRejectedException;
import com.hpe.caf.api.worker.Worker;
import com.hpe.caf.api.worker.WorkerConfiguration;
import com.hpe.caf.api.worker.WorkerFactory;
import com.hpe.caf.api.worker.WorkerTaskData;
import com.hpe.caf.worker.document.config.DocumentWorkerConfiguration;
import com.hpe.caf.worker.document.extensibility.DocumentWorker;
import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.impl.HealthMonitorImpl;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows implementations of the DocumentWorker class can be used with the Worker Framework.
 */
public class DocumentWorkerAdapter implements WorkerFactory
{
    /**
     * Used for logging.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DocumentWorkerAdapter.class);

    /**
     * This object stores the global data that was initially passed to the WorkerFactoryProvider when it was called. It effectively acts
     * as a global object for the worker.
     */
    protected final ApplicationImpl application;

    /**
     * This is the actual implementation of the worker.<p>
     * This class is adapting its interface so that it can be used with the Worker Framework.
     */
    private final DocumentWorker documentWorker;

    /**
     * This object stores the configuration for the Document Worker. It is normally read from a JSON file with a name like
     * "cfg~worker-name~DocumentWorkerConfiguration".
     */
    private final DocumentWorkerConfiguration configuration;

    /**
     * Constructs the DocumentWorkerAdapter object, which adapts the DocumentWorker interface so that objects which implement it can be
     * used with the Worker Framework.
     *
     * @param application the global data for the worker
     * @param documentWorker the actual implementation of the worker
     */
    public DocumentWorkerAdapter(final ApplicationImpl application, final DocumentWorker documentWorker)
    {
        this.application = application;
        this.documentWorker = documentWorker;
        this.configuration = application.getConfiguration();
    }

    @Override
    public HealthResult livenessCheck()
    {
        final HealthMonitorImpl healthMonitor = new HealthMonitorImpl(application);
        documentWorker.checkLiveness(healthMonitor);

        return healthMonitor.getHealthResult();
    }

    @Override
    public HealthResult healthCheck()
    {
        final HealthMonitorImpl healthMonitor = new HealthMonitorImpl(application);
        documentWorker.checkHealth(healthMonitor);

        return healthMonitor.getHealthResult();
    }

    @Override
    public Worker getWorker(final WorkerTaskData workerTask)
        throws TaskRejectedException, InvalidTaskException
    {
        return new DocumentMessageProcessor(application, documentWorker, workerTask);
    }

    @Nonnull
    @Override
    public WorkerConfiguration getWorkerConfiguration()
    {
        return configuration;
    }

    @Override
    public String getInvalidTaskQueue()
    {
        return application.getFailureQueue();
    }

    @Override
    public int getWorkerThreads()
    {
        return configuration.getThreads();
    }

    @Override
    public void shutdown()
    {
        try {
            documentWorker.close();
        } catch (final RuntimeException ex) {
            throw ex;
        } catch (final Exception ex) {
            LOG.warn("Error closing DocumentWorker during shutdown", ex);
        }
    }
}
