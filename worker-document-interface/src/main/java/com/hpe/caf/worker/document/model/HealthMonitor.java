package com.hpe.caf.worker.document.model;

/**
 * Used for reporting any health issues detected.
 */
public interface HealthMonitor extends DocumentWorkerObject
{
    /**
     * This method is used during a health check to report that a health issue has been detected with the worker.
     *
     * @param message a short description of the issue detected
     */
    void reportUnhealthy(String message);
}
