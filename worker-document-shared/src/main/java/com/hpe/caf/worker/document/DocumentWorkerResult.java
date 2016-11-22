/*
 * copyright 2016 Hewlett Packard Enterprise
 */
package com.hpe.caf.worker.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class DocumentWorkerResult
{
    /**
     * The fields requiring change and the actions required.
     */
    public Map<String, DocumentWorkerFieldChanges> fieldChanges;

    /**
     * Worker failure report, including failure ID, failure message and failure trace.
     */
    public List<DocumentWorkerFailure> failures;
}
