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
