/*
 * Copyright 2016-2018 Micro Focus or one of its affiliates.
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
package com.hpe.caf.worker.document.converters;

import com.hpe.caf.worker.document.DocumentWorkerDocument;
import com.hpe.caf.worker.document.DocumentWorkerFailure;
import com.hpe.caf.worker.document.DocumentWorkerFieldEncoding;
import com.hpe.caf.worker.document.DocumentWorkerFieldValue;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.Failure;
import com.hpe.caf.worker.document.model.Failures;
import com.hpe.caf.worker.document.model.Field;
import com.hpe.caf.worker.document.model.FieldValue;
import com.hpe.caf.worker.document.model.Fields;
import com.hpe.caf.worker.document.model.Subdocuments;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.codec.binary.Base64;

/**
 * This is a utility class which can be used for constructing {@link DocumentWorkerDocument} POJO objects, which can be easily serialised
 * to JSON, from {@link Document} object model objects.
 */
public final class DocumentConverter
{
    private DocumentConverter()
    {
    }

    @Nonnull
    public static DocumentWorkerDocument convert(final Document document)
    {
        final DocumentWorkerDocument documentOut = new DocumentWorkerDocument();
        documentOut.reference = document.getReference();
        documentOut.fields = convert(document.getFields());
        documentOut.failures = convert(document.getFailures());
        documentOut.subdocuments = convert(document.getSubdocuments());

        return documentOut;
    }

    private static Map<String, List<DocumentWorkerFieldValue>> convert(final Fields fields)
    {
        // I don't think there's anything wrong with this code but I re-wrote it just below in a way that I think is slightly simplier,
        // without the groupingBy() for example.
        //
        //final Map<String, List<DocumentWorkerFieldValue>> fieldsOut = fields.stream()
        //    .flatMap(field -> field.getValues().stream())
        //    .collect(Collectors.groupingBy(fieldValue -> fieldValue.getField().getName(),
        //                                   Collectors.mapping(DocumentConverter::convert, Collectors.toList())));

        final Map<String, List<DocumentWorkerFieldValue>> fieldsOut = fields.stream()
            .filter(Field::hasValues)
            .collect(Collectors.toMap(Field::getName,
                                      field -> field.getValues().stream().map(DocumentConverter::convert).collect(Collectors.toList())));

        return fieldsOut.isEmpty()
            ? null
            : fieldsOut;
    }

    @Nonnull
    private static DocumentWorkerFieldValue convert(final FieldValue fieldValue)
    {
        final DocumentWorkerFieldValue fieldValueOut = new DocumentWorkerFieldValue();

        if (fieldValue.isReference()) {
            fieldValueOut.data = fieldValue.getReference();
            fieldValueOut.encoding = DocumentWorkerFieldEncoding.storage_ref;
        } else if (fieldValue.isStringValue()) {
            fieldValueOut.data = fieldValue.getStringValue();
        } else {
            final byte[] data = fieldValue.getValue();
            fieldValueOut.data = Base64.encodeBase64String(data);
            fieldValueOut.encoding = DocumentWorkerFieldEncoding.base64;
        }

        return fieldValueOut;
    }

    private static List<DocumentWorkerFailure> convert(final Failures failures)
    {
        return failures.isEmpty()
            ? null
            : failures.stream().map(DocumentConverter::convert).collect(Collectors.toList());
    }

    @Nonnull
    private static DocumentWorkerFailure convert(final Failure failure)
    {
        final DocumentWorkerFailure failureOut = new DocumentWorkerFailure();
        failureOut.failureId = failure.getFailureId();
        failureOut.failureMessage = failure.getFailureMessage();
        failureOut.failureStack = failure.getFailureStack();

        return failureOut;
    }

    private static List<DocumentWorkerDocument> convert(final Subdocuments subdocuments)
    {
        return subdocuments.isEmpty() ? null : subdocuments
            .stream()
            .map(DocumentConverter::convert)
            .collect(Collectors.toList());
    }
}
