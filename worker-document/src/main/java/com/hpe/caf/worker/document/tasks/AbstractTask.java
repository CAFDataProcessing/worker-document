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
package com.hpe.caf.worker.document.tasks;

import com.hpe.caf.api.worker.WorkerResponse;
import com.hpe.caf.api.worker.WorkerTaskData;
import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.impl.DocumentImpl;
import com.hpe.caf.worker.document.impl.DocumentWorkerObjectImpl;
import com.hpe.caf.worker.document.model.Application;
import com.hpe.caf.worker.document.model.ResponseOptions;
import com.hpe.caf.worker.document.model.Task;
import com.hpe.caf.worker.document.views.ReadOnlyDocument;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public abstract class AbstractTask extends DocumentWorkerObjectImpl implements Task
{
    private final WorkerTaskData workerTask;
    protected final DocumentImpl document;
    private final Map<String, String> customData;
    private final ResponseOptions responseOptions;

    protected AbstractTask(
        final ApplicationImpl application,
        final WorkerTaskData workerTask,
        final ReadOnlyDocument effectiveDocument,
        final Map<String, String> customData
    )
    {
        super(application);
        this.workerTask = Objects.requireNonNull(workerTask);
        this.document = new DocumentImpl(application, this, effectiveDocument);
        this.responseOptions = new ResponseOptionsImpl(this);
        this.customData = customData;
    }

    @Nonnull
    @Override
    public final DocumentImpl getDocument()
    {
        return document;
    }

    @Override
    public final String getCustomData(final String dataKey)
    {
        if (customData == null) {
            return null;
        }

        return customData.get(dataKey);
    }

    @Override
    public <S> S getService(final Class<S> service)
    {
        return (service == WorkerTaskData.class)
            ? (S) workerTask
            : null;
    }

    @Nonnull
    @Override
    public ResponseOptions getResponseOptions()
    {
        return this.responseOptions;
    }

    @Nonnull
    public final WorkerResponse createWorkerResponse()
    {
        return createWorkerResponseImpl();
    }

    @Nonnull
    protected abstract WorkerResponse createWorkerResponseImpl();

    /**
     * Returns appropriate WorkerResponse for the task in the event of a general task failure.
     *
     * @param failure detail of the failure that occurred
     * @return response with task appropriate failure details
     */
    @Nonnull
    public final WorkerResponse handleGeneralFailure(final Throwable failure)
    {
        return handleGeneralFailureImpl(failure);
    }

    @Nonnull
    protected abstract WorkerResponse handleGeneralFailureImpl(final Throwable failure);

    private static class ResponseOptionsImpl implements ResponseOptions
    {
        private String queueName;
        private Map<String, String> customData;
        private final Task parentTask;
        private final Application application;

        private ResponseOptionsImpl(Task parentTask)
        {
            this.parentTask = parentTask;
            this.application = parentTask.getApplication();
        }

        @Override
        public String getQueueName()
        {
            return queueName;
        }

        @Override
        public void setQueueName(String queueName)
        {
            this.queueName = queueName;
        }

        /**
         * Gets the custom data.
         *
         * @return The custom data map.
         */
        @Override
        public Map<String, String> getCustomData()
        {
            return customData;
        }

        @Override
        public void setCustomData(Map<String, String> customData)
        {
            this.customData = customData;
        }

        @Override
        public Task getTask()
        {
            return parentTask;
        }

        @Nonnull
        @Override
        public Application getApplication()
        {
            return application;
        }
    }
}
