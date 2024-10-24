/*
 * Copyright 2016-2024 Open Text.
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
package com.github.cafdataprocessing.worker.document.schema.validator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cafdataprocessing.worker.document.schema.model.SchemaResource;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersionDetector;
import com.networknt.schema.ValidationMessage;
import com.worldturner.medeia.api.JsonSchemaVersion;
import com.worldturner.medeia.api.SchemaSource;
import com.worldturner.medeia.api.StreamSchemaSource;
import com.worldturner.medeia.api.jackson.MedeiaJacksonApi;
import com.worldturner.medeia.schema.validation.SchemaValidator;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Set;

/**
 * Validates a Document.
 */
public final class DocumentValidator
{
    private static ObjectMapper MAPPER = new ObjectMapper();
    private static final JsonSchema SCHEMA_VALIDATOR_1 = getJsonSchema();
    private static final SchemaValidator SCHEMA_VALIDATOR_2;

    static {
        final MedeiaJacksonApi api = new MedeiaJacksonApi();
        try {
            final SchemaSource source = new StreamSchemaSource(SchemaResource.getUrl().openStream(), JsonSchemaVersion.DRAFT07);
            SCHEMA_VALIDATOR_2 = api.loadSchema(source);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void validate(final String document) throws InvalidDocumentException
    {
        final JsonNode documentJson = parseDocument(document);
        final Set<ValidationMessage> errors = SCHEMA_VALIDATOR_1.validate(documentJson);

        if (!errors.isEmpty()) {
            final StringBuilder errMsg = new StringBuilder("Schema validation errors:");
            for (final ValidationMessage error : errors) {
                errMsg.append('\n').append(error.getMessage());
            }
            throw new InvalidDocumentException(errMsg.toString());
        }
    }

    public static JsonParser getValidatingParser(final InputStream document) throws JsonParseException, IOException
    {
        final MedeiaJacksonApi api = new MedeiaJacksonApi();
        final JsonFactory factory = new JsonFactory();
        factory.configure(Feature.FLUSH_PASSED_TO_STREAM, false);
        factory.configure(Feature.AUTO_CLOSE_TARGET, false);
        final JsonParser unvalidatedParser = factory.createParser(document);
        return api.decorateJsonParser(SCHEMA_VALIDATOR_2, unvalidatedParser);
    }

    private static JsonSchema getJsonSchema()
    {
        final JsonNode schemaNode = getSchemaNode();

        final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersionDetector.detect(schemaNode));
        return factory.getSchema(schemaNode);
    }

    private static JsonNode getSchemaNode()
    {
        try {
            return MAPPER.readTree(SchemaResource.getUrl());
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static JsonNode parseDocument(final String document) throws InvalidDocumentException
    {
        try {
            return MAPPER.readTree(document);
        } catch (final JsonProcessingException ex) {
            throw new InvalidDocumentException(ex);
        }
    }
}
