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
import com.hpe.caf.worker.document.views.ReadOnlyDocument;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public abstract class AbstractTask
{
    protected final ApplicationImpl application;
    protected final DocumentImpl document;
    private final Map<String, String> customData;

    protected AbstractTask(
        final ApplicationImpl application,
        final WorkerTaskData workerTask,
        final ReadOnlyDocument effectiveDocument,
        final Map<String, String> customData
    )
    {
        Objects.requireNonNull(workerTask);
        this.application = application;
        this.document = new DocumentImpl(application, this, effectiveDocument);
        this.customData = customData;
    }

    @Nonnull
    public final DocumentImpl getDocument()
    {
        return document;
    }

    public final String getCustomData(final String dataKey)
    {
        if (customData == null) {
            return null;
        }

        return customData.get(dataKey);
    }

    @Nonnull
    public final WorkerResponse createWorkerResponse()
    {
        return createWorkerResponseImpl();
    }

    @Nonnull
    protected abstract WorkerResponse createWorkerResponseImpl();

    public final void handleRuntimeException(final RuntimeException re)
    {
        handleRuntimeExceptionImpl(re);
    }

    protected abstract void handleRuntimeExceptionImpl(RuntimeException re);
}
