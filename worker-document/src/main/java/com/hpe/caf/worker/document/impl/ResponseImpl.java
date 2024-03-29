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
package com.hpe.caf.worker.document.impl;

import com.hpe.caf.worker.document.model.Response;
import com.hpe.caf.worker.document.model.Task;
import com.hpe.caf.worker.document.tasks.AbstractTask;
import jakarta.annotation.Nonnull;

public final class ResponseImpl extends DocumentWorkerObjectImpl implements Response
{
    private final AbstractTask task;
    private final ResponseCustomDataImpl customData;
    private final ResponseQueueImpl failureQueue;
    private final ResponseQueueImpl successQueue;

    public ResponseImpl(
        final ApplicationImpl application,
        final AbstractTask task
    )
    {
        super(application);
        this.task = task;
        this.customData = new ResponseCustomDataImpl(application, this);
        this.failureQueue = new ResponseQueueImpl(application, this, application.getFailureQueue());
        this.successQueue = new ResponseQueueImpl(application, this, application.getSuccessQueue());
    }

    @Nonnull
    @Override
    public ResponseCustomDataImpl getCustomData()
    {
        return customData;
    }

    @Nonnull
    @Override
    public ResponseQueueImpl getFailureQueue()
    {
        return failureQueue;
    }

    @Nonnull
    @Override
    public ResponseQueueImpl getSuccessQueue()
    {
        return successQueue;
    }

    @Nonnull
    @Override
    public Task getTask()
    {
        return task;
    }

    public String getOutputQueue(final boolean hasFailures)
    {
        return (hasFailures ? failureQueue : successQueue).getQueueName();
    }
}
