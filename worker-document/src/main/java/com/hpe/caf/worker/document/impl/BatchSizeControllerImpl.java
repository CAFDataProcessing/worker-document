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

import com.hpe.caf.worker.document.DocumentWorkerConfiguration;
import com.hpe.caf.worker.document.model.BatchSizeController;

public final class BatchSizeControllerImpl extends DocumentWorkerObjectImpl implements BatchSizeController
{
    private static final int DEFAULT_MAXIMUM_BATCH_SIZE = 100;

    private static final long DEFAULT_MAXIMUM_BATCH_TIME = 180000;

    private int maxBatchSize;

    private long maxBatchTime;

    public BatchSizeControllerImpl(final ApplicationImpl application, final DocumentWorkerConfiguration configuration)
    {
        super(application);

        setMaximumBatchSize(configuration.getMaxBatchSize());
        setMaximumBatchTime(configuration.getMaxBatchTime());
    }

    @Override
    public int getMaximumBatchSize()
    {
        return maxBatchSize;
    }

    @Override
    public long getMaximumBatchTime()
    {
        return maxBatchTime;
    }

    @Override
    public void setMaximumBatchSize(int maxBatchSize)
    {
        this.maxBatchSize = (maxBatchSize > 0) ? maxBatchSize : DEFAULT_MAXIMUM_BATCH_SIZE;
    }

    @Override
    public void setMaximumBatchTime(long maxBatchTime)
    {
        this.maxBatchTime = (maxBatchTime > 0) ? maxBatchTime : DEFAULT_MAXIMUM_BATCH_TIME;
    }
}
