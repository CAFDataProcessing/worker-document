package com.hpe.caf.worker.document;

import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/**
 * Utility functions related to dynamically using services.
 */
public final class ServiceFunctions
{
    /**
     * Overrides the default constructor to ensure that no instances of this class are created.
     */
    private ServiceFunctions()
    {
    }

    /**
     * This method creates a new instance of the specified service type, using the current thread's
     * {@linkplain java.lang.Thread#getContextClassLoader context class loader}.
     *
     * @param <S> the class of the service type
     * @param service the interface or abstract class representing the service
     * @return a new instance of the service, or null if the service could not be loaded
     */
    public static <S> S loadService(Class<S> service)
    {
        ServiceLoader<S> documentWorkerFactoryLoader = ServiceLoader.load(service);

        return StreamSupport.stream(documentWorkerFactoryLoader.spliterator(), false)
            .findFirst()
            .orElse(null);
    }
}
