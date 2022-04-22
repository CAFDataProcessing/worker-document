/*
 * Copyright 2016-2022 Micro Focus or one of its affiliates.
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
package com.microfocus.caf.worker.document.schema.validator;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonNodeReader;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.microfocus.caf.worker.document.schema.model.SchemaResource;
import com.worldturner.medeia.api.JsonSchemaVersion;
import com.worldturner.medeia.api.SchemaSource;
import com.worldturner.medeia.api.StreamSchemaSource;
import com.worldturner.medeia.api.jackson.MedeiaJacksonApi;
import com.worldturner.medeia.schema.validation.SchemaValidator;

/**
 * Validates a Document.
 */
public final class DocumentValidator
{
    public static void validate(final String document) throws InvalidDocumentException
    {
        try (final InputStream inputStream = SchemaResource.getUrl().openStream();
             final Reader documentReader = new StringReader(document)) {
            final JsonNode documentSchema = new JsonNodeReader().fromInputStream(inputStream);
            final JsonNode documentNode = new JsonNodeReader().fromReader(documentReader);
            final ProcessingReport report = JsonSchemaFactory.byDefault().getValidator().validateUnchecked(documentSchema, documentNode);
            final boolean isSuccess = report.isSuccess();
            if (!isSuccess) {
                final StringBuilder errMsg = new StringBuilder("Document validation errors:");
                for (final ProcessingMessage msg : report) {
                    if (msg.getLogLevel() == LogLevel.ERROR || msg.getLogLevel() == LogLevel.FATAL) {
                        errMsg.append('\n').append(msg.getMessage());
                    }
                }
                throw new InvalidDocumentException(errMsg.toString());
            }
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static JsonParser getValidatingParser(final InputStream document) throws JsonParseException, IOException
    {
        final MedeiaJacksonApi api = new MedeiaJacksonApi();
        final SchemaSource source = new StreamSchemaSource(
            SchemaResource.getUrl().openStream(),
            JsonSchemaVersion.DRAFT07);
        final SchemaValidator validator = api.loadSchema(source);
        final JsonFactory factory = new JsonFactory();
        factory.configure(Feature.FLUSH_PASSED_TO_STREAM, false);
        factory.configure(Feature.AUTO_CLOSE_TARGET, false);
        final JsonParser unvalidatedParser = factory.createParser(document);
        return api.decorateJsonParser(validator, unvalidatedParser);
    }

}
