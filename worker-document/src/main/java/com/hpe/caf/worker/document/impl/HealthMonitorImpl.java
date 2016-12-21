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
