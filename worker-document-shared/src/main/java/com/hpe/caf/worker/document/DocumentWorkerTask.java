package com.hpe.caf.worker.document;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Input message for a Document worker.
 */
@JsonInclude(Include.NON_NULL)
public final class DocumentWorkerTask
{
    /*
     * The source data for worker-document to act upon.
     */
    @NotNull
    public Map<String, List<DocumentWorkerData>> fields;

    /*
    *This is a method of providing further information to the document-worker.
     */
    public Map<String, String> customMap;
}
