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
package com.hpe.caf.worker.document.impl;

import com.hpe.caf.worker.document.model.ResponseOptions;
import com.hpe.caf.worker.document.model.Task;
import com.hpe.caf.worker.document.tasks.AbstractTask;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public final class ResponseOptionsImpl extends DocumentWorkerObjectImpl implements ResponseOptions
{
    private final AbstractTask task;
    private String queueName;
    private Map<String, String> customData;

    public ResponseOptionsImpl(
        final ApplicationImpl application,
        final AbstractTask task
    )
    {
        super(application);
        this.task = task;
        this.queueName = null;
        this.customData = null;
    }

    @Override
    public String getQueueName()
    {
        return queueName;
    }

    @Override
    public void setQueueName(final String queueName)
    {
        this.queueName = queueName;
    }

    @Override
    public Map<String, String> getCustomData()
    {
        return customData;
    }

    @Override
    public void setCustomData(final Map<String, String> customData)
    {
        this.customData = (customData == null || customData.isEmpty())
            ? null
            : Collections.unmodifiableMap(new HashMap<>(customData));
    }

    @Nonnull
    @Override
    public Task getTask()
    {
        return task;
    }
}
