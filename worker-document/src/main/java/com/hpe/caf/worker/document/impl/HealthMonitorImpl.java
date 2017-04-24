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

import com.hpe.caf.api.HealthResult;
import com.hpe.caf.api.HealthStatus;
import com.hpe.caf.worker.document.model.HealthMonitor;

public final class HealthMonitorImpl extends DocumentWorkerObjectImpl implements HealthMonitor
{
    /**
     * This is the result that is returned by the health check.<p>
     * It is only set if the health check fails; it remains null if there are no issues detected.
     */
    private HealthResult healthResult;

    /**
     * Constructs an object which receives health reports.
     *
     * @param application the global data for the worker
     */
    public HealthMonitorImpl(final ApplicationImpl application)
    {
        super(application);
        this.healthResult = null;
    }

    @Override
    public void reportUnhealthy(String message)
    {
        // If there has already been an unhealthy report then discard this new one (i.e. we assume it is better to return the original)
        if (healthResult != null) {
            return;
        }

        // Construct the new health result
        healthResult = new HealthResult(HealthStatus.UNHEALTHY, message);
    }

    public HealthResult getHealthResult()
    {
        if (healthResult == null) {
            return HealthResult.RESULT_HEALTHY;
        } else {
            return healthResult;
        }
    }
}
