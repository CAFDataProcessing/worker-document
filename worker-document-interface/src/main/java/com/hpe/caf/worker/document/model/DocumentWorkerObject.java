package com.hpe.caf.worker.document.model;

import javax.annotation.Nonnull;

/**
 * This is the base object of the Document Worker Object Model. All object model interfaces derive from this interface. Any functionality
 * added here is available from all object model objects.
 */
public interface DocumentWorkerObject
{
    /**
     * Returns the Application object, which represents the Document Worker itself. This can be used to retrieve worker-scope details,
     * such as configuration, access to the remote data store, etc.
     *
     * @return the root Application object
     */
    @Nonnull
    Application getApplication();
}
