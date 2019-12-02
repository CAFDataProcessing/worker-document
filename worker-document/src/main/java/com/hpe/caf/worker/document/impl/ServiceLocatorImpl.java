/*
 * Copyright 2016-2020 Micro Focus or one of its affiliates.
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

import com.hpe.caf.worker.document.model.ServiceLocator;
import java.util.HashMap;
import java.util.Map;

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
    public <S> S getService(final Class<S> service)
    {
        return (S) serviceMap.get(service);
    }
}
