/*
 * copyright 2016 Hewlett Packard Enterprise
 */
package com.hpe.caf.worker.document;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class DocumentWorkerFieldValue
{
    public String data;
    public DocumentWorkerFieldEncoding encoding;
}
