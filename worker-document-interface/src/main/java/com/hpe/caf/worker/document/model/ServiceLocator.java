package com.hpe.caf.worker.document.model;

/**
 * Used for caching and loading extra services that may be made available.
 */
public interface ServiceLocator extends DocumentWorkerObject
{
    /**
     * Returns the specified service, or {@code null} if the service has not been registered.
     *
     * @param <S> the type of the service to be returned
     * @param service the interface or abstract class representing the service
     * @return the service provider
     * @see Application#getService(Class) Application.getService()
     */
    <S> S getService(Class<S> service);

    /**
     * TODO: Consider adding a method which can be used to cycle through all the services that have been made available.
     */
    //Services getServices();
}
