/*
 * Copyright 2015-2017 EntIT Software LLC, a Micro Focus company.
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
package com.hpe.caf.worker.document.config;

public final class InputMessageConfiguration
{
    /**
     * Whether composite document messages are accepted.
     */
    private Boolean documentTasksAccepted;

    /**
     * Whether the fields-only style messages are accepted.
     */
    private Boolean fieldEnrichmentTasksAccepted;

    /**
     * Whether subdocuments should be passed to the worker implementation separately.
     */
    private Boolean processSubdocumentsSeparately;

    public InputMessageConfiguration()
    {
    }

    public Boolean getDocumentTasksAccepted()
    {
        return documentTasksAccepted;
    }

    public void setDocumentTasksAccepted(Boolean documentTasksAccepted)
    {
        this.documentTasksAccepted = documentTasksAccepted;
    }

    public Boolean getFieldEnrichmentTasksAccepted()
    {
        return fieldEnrichmentTasksAccepted;
    }

    public void setFieldEnrichmentTasksAccepted(Boolean fieldEnrichmentTasksAccepted)
    {
        this.fieldEnrichmentTasksAccepted = fieldEnrichmentTasksAccepted;
    }

    public Boolean getProcessSubdocumentsSeparately()
    {
        return processSubdocumentsSeparately;
    }

    public void setProcessSubdocumentsSeparately(Boolean processSubdocumentsSeparately)
    {
        this.processSubdocumentsSeparately = processSubdocumentsSeparately;
    }
}
