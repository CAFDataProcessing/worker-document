package com.hpe.caf.worker.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hpe.caf.worker.testing.ContentFileTestExpectation;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentWorkerFieldValueExpectation {
    public String data;
    public ContentFileTestExpectation content;
    public DocumentWorkerFieldEncoding encoding;
}
