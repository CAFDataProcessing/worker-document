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
package com.github.cafdataprocessing.workers.lookup;

import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.extensibility.DocumentWorker;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.HealthMonitor;
import java.util.function.Function;

public final class LookupWorker<V> implements DocumentWorker
{
    private final Function<Document, V> function;
    private V value;

    public LookupWorker(final Function<Document, V> function)
    {
        this.function = function;
        this.value = null;
    }

    @Override
    public void checkHealth(final HealthMonitor healthMonitor)
    {
    }

    @Override
    public void processDocument(final Document document) throws InterruptedException, DocumentWorkerTransientException
    {
        value = function.apply(document);
    }

    public V getValue()
    {
        return value;
    }
}
