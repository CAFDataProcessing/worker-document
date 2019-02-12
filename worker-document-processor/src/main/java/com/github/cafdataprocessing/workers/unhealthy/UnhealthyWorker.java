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
package com.github.cafdataprocessing.workers.unhealthy;

import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.extensibility.DocumentWorker;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.HealthMonitor;

public final class UnhealthyWorker implements DocumentWorker
{
    private final String unhealthyMessage;

    public UnhealthyWorker(final String unhealthyMessage)
    {
        this.unhealthyMessage = unhealthyMessage;
    }

    @Override
    public void checkHealth(final HealthMonitor healthMonitor)
    {
        healthMonitor.reportUnhealthy(unhealthyMessage);
    }

    @Override
    public void processDocument(final Document document) throws DocumentWorkerTransientException
    {
        throw new DocumentWorkerTransientException(unhealthyMessage);
    }
}
