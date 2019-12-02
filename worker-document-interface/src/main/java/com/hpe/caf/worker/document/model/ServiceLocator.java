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
