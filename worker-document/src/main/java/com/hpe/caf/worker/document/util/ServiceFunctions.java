/*
 * Copyright 2016-2022 Micro Focus or one of its affiliates.
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
package com.hpe.caf.worker.document.util;

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
    public static <S> S loadService(final Class<S> service)
    {
        final ServiceLoader<S> serviceLoader = ServiceLoader.load(service);

        return StreamSupport.stream(serviceLoader.spliterator(), false)
            .findFirst()
            .orElse(null);
    }
}
