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

import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.impl.DocumentWorkerObjectImpl;
import com.hpe.caf.worker.document.model.HealthMonitor;

final class HealthMonitorImpl extends DocumentWorkerObjectImpl implements HealthMonitor
{
    private DocumentWorkerUnhealthyException unhealthyException;

    public HealthMonitorImpl(final ApplicationImpl application)
    {
        super(application);
        this.unhealthyException = null;
    }

    @Override
    public void reportUnhealthy(String message)
    {
        // If there are multiple unhealthy reports then report the first one (i.e. assume the original one is the best)
        if (unhealthyException == null) {
            unhealthyException = new DocumentWorkerUnhealthyException(message);
        }
    }

    public void throwIfUnhealthy() throws DocumentWorkerUnhealthyException
    {
        if (unhealthyException != null) {
            throw unhealthyException;
        }
    }
}
