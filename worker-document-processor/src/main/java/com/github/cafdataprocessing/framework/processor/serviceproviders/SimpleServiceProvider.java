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
package com.github.cafdataprocessing.framework.processor.serviceproviders;

import com.github.cafdataprocessing.framework.processor.ServiceProvider;
import java.util.HashMap;
import java.util.Map;

public final class SimpleServiceProvider implements ServiceProvider
{
    private final Map<Class, Object> serviceMap;

    public SimpleServiceProvider()
    {
        this.serviceMap = new HashMap<>();
    }

    public <S> void register(final Class<S> serviceType, final S service)
    {
        serviceMap.put(serviceType, service);
    }

    @Override
    public <S> S getService(final Class<S> serviceType)
    {
        return (S) serviceMap.get(serviceType);
    }
}
