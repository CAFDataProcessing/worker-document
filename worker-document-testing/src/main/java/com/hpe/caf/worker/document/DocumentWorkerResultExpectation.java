package com.hpe.caf.worker.document;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentWorkerResultExpectation {
    public Map<String, DocumentWorkerFieldChangesExpectation> fieldChanges;
    public List<DocumentWorkerFailure> failures;
}
