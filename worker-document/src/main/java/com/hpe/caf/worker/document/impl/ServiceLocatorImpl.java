package com.hpe.caf.worker.document.impl;

import java.util.HashMap;
import java.util.Map;
import com.hpe.caf.worker.document.model.ServiceLocator;

public final class ServiceLocatorImpl extends DocumentWorkerObjectImpl implements ServiceLocator
{
    private final Map<Class, Object> serviceMap;

    public ServiceLocatorImpl(final ApplicationImpl application)
    {
        super(application);
        serviceMap = new HashMap<>();
    }

    public <S> void register(final Class<S> service, final S serviceProvider)
    {
        serviceMap.put(service, serviceProvider);
    }

    @Override
    public <S> S getService(Class<S> service)
    {
        return (S) serviceMap.get(service);
    }
}
