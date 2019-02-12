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
package com.hpe.caf.worker.document.impl;

import com.hpe.caf.worker.document.model.Application;
import com.hpe.caf.worker.document.model.DocumentWorkerObject;
import javax.annotation.Nonnull;

public class DocumentWorkerObjectImpl implements DocumentWorkerObject
{
    protected final ApplicationImpl application;

    public DocumentWorkerObjectImpl(final ApplicationImpl application)
    {
        this.application = application;
    }

    @Nonnull
    @Override
    public final Application getApplication()
    {
        return application;
    }
}
