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
package com.github.cafdataprocessing.workers.compound;

import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.extensibility.DocumentWorker;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.HealthMonitor;
import java.util.List;
import java.util.Objects;

final class CompoundWorker implements DocumentWorker
{
    private final List<DocumentWorker> constituentWorkers;

    public CompoundWorker(final List<DocumentWorker> constituentWorkers)
    {
        Objects.requireNonNull(constituentWorkers);

        this.constituentWorkers = constituentWorkers;
    }

    @Override
    public void checkHealth(final HealthMonitor healthMonitor)
    {
        for (final DocumentWorker worker : constituentWorkers) {
            worker.checkHealth(healthMonitor);
        }
    }

    @Override
    public void processDocument(final Document document) throws InterruptedException, DocumentWorkerTransientException
    {
        for (final DocumentWorker worker : constituentWorkers) {
            worker.processDocument(document);
        }
    }

    @Override
    public void close() throws Exception
    {
        // TODO: We should really be closing in reverse order
        // TODO: Exception handling is not great here - we should continue to try to close other workers and have some sort of compound
        //       exception if necessary
        for (final DocumentWorker worker : constituentWorkers) {
            worker.close();
        }
    }
}
