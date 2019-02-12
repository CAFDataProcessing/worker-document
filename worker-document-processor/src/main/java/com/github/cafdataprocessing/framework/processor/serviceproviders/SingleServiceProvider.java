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

public final class SingleServiceProvider<T> implements ServiceProvider
{
    private final Class<T> serviceType;
    private final T service;

    public static <T> SingleServiceProvider<T> create(final Class<T> serviceType, final T service)
    {
        return new SingleServiceProvider<>(serviceType, service);
    }

    private SingleServiceProvider(final Class<T> serviceType, final T service)
    {
        this.serviceType = serviceType;
        this.service = service;
    }

    @Override
    public <S> S getService(final Class<S> serviceType)
    {
        return (serviceType == this.serviceType) ? (S) service : null;
    }
}
