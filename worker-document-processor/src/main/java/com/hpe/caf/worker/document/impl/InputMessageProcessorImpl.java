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

import com.hpe.caf.worker.document.config.InputMessageConfiguration;
import com.hpe.caf.worker.document.model.InputMessageProcessor;
import com.hpe.caf.worker.document.util.BooleanFunctions;

public class InputMessageProcessorImpl extends DocumentWorkerObjectImpl implements InputMessageProcessor
{
    private static final boolean DEFAULT_PROCESS_SUBDOCUMENTS_SEPARATELY = true;

    private boolean processSubdocumentsSeparately;

    public InputMessageProcessorImpl(
        final ApplicationImpl application,
        final InputMessageConfiguration configuration
    )
    {
        super(application);

        this.processSubdocumentsSeparately = (configuration == null)
            ? DEFAULT_PROCESS_SUBDOCUMENTS_SEPARATELY
            : BooleanFunctions.valueOf(configuration.getProcessSubdocumentsSeparately(), DEFAULT_PROCESS_SUBDOCUMENTS_SEPARATELY);
    }

    @Override
    public boolean getProcessSubdocumentsSeparately()
    {
        return processSubdocumentsSeparately;
    }

    @Override
    public void setProcessSubdocumentsSeparately(final boolean processSubdocumentsSeparately)
    {
        this.processSubdocumentsSeparately = processSubdocumentsSeparately;
    }
}
