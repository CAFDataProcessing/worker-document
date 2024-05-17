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
package com.microfocus.caf.worker.document.schema.validator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.worldturner.medeia.api.ValidationFailedException;

public final class DocumentValidatorTest
{
    @Test
    public void validEmptyDocumentTest() throws Exception
    {
        System.out.println("validEmptyDocumentTest...");
        try (
            final InputStreamReader stream = new InputStreamReader(
                DocumentValidatorTest.class.getResourceAsStream("/validEmptyDocument.json"),
                StandardCharsets.UTF_8);) {
            final String document = IOUtils.toString(stream);
            try {
                DocumentValidator.validate(document);
            } catch (final InvalidDocumentException e) {
                fail("validEmptyDocumentTest failed: " + e.getMessage());
            }
        }
    }

    @Test
    public void validDocumentOnlyTest() throws Exception
    {
        System.out.println("validDocumentOnlyTest...");
        try (
            final InputStreamReader stream = new InputStreamReader(
                DocumentValidatorTest.class.getResourceAsStream("/validDocumentOnly.json"),
                StandardCharsets.UTF_8);) {
            final String document = IOUtils.toString(stream);
            try {
                DocumentValidator.validate(document);
            } catch (final InvalidDocumentException e) {
                fail("validDocumentOnlyTest failed: " + e.getMessage());
            }
        }
    }

    @Test
    public void validDocAndChangeLogTest() throws Exception
    {
        System.out.println("validDocAndChangeLogTest...");
        try (
            final InputStreamReader stream = new InputStreamReader(
                DocumentValidatorTest.class.getResourceAsStream("/validDocAndChangeLog.json"),
                StandardCharsets.UTF_8);) {
            final String document = IOUtils.toString(stream);
            try {
                DocumentValidator.validate(document);
            } catch (final InvalidDocumentException e) {
                fail("validDocAndChangeLogTest failed: " + e.getMessage());
            }
        }
    }

    @Test
    public void validChangeLogOnlyTest() throws Exception
    {
        System.out.println("validChangeLogOnlyTest...");
        try (
            final InputStreamReader stream = new InputStreamReader(
                DocumentValidatorTest.class.getResourceAsStream("/validChangeLogOnly.json"),
                StandardCharsets.UTF_8);) {
            final String document = IOUtils.toString(stream);
            try {
                DocumentValidator.validate(document);
            } catch (final InvalidDocumentException e) {
                fail("validChangeLogOnlyTest failed: " + e.getMessage());
            }
        }
    }

    @Test
    public void invalidScriptsDocumentTest() throws Exception
    {
        System.out.println("invalidScriptsDocumentTest...");
        try (
            final InputStreamReader stream = new InputStreamReader(
                DocumentValidatorTest.class.getResourceAsStream("/invalidScriptsDocument.json"),
                StandardCharsets.UTF_8);) {
            final String document = IOUtils.toString(stream);
            try {
                DocumentValidator.validate(document);
                fail("invalidScriptsDocumentTest failed: Validation done incorectly");
            } catch (final InvalidDocumentException e) {
                System.out.println("invalidScriptsDocumentTest: " + e);
                assertNotNull(e.getMessage());
            }
        }
    }

    @Test
    public void invalidPropertiesDocumentTest() throws Exception
    {
        System.out.println("invalidPropertiesDocumentTest...");
        try (
            final InputStreamReader stream = new InputStreamReader(
                DocumentValidatorTest.class.getResourceAsStream("/invalidPropertiesDocument.json"),
                StandardCharsets.UTF_8);) {
            final String document = IOUtils.toString(stream);
            try {
                DocumentValidator.validate(document);
                fail("invalidPropertiesDocumentTest failed: Validation done incorectly");
            } catch (final InvalidDocumentException e) {
                System.out.println("invalidPropertiesDocumentTest: " + e);
                assertNotNull(e.getMessage());
            }
        }
    }

    @Test
    public void invalidDocumentStreamTest() throws Exception
    {
        System.out.println("invalidDocumentStreamTest...");
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        String testJson
            = "{"
            + "    'document': {"
            + "      'reference': 'batch2.msg',"
            + "      'fields': {"
            + "        'FROM': {'data': 'Mark Roberts'},"
            + "        'TO': {'data': 'Gene Simmons'},"
            + "        'SUBJECT': {'data': 'Favourite book'},"
            + "        'CONTENT': {'data': 'This is the book that popularised the use of the phrase Merry Christmas.'}"
            + "      }"
            + "    }"
            + "  }";
        testJson = testJson.replaceAll("'", "\"");
        final InputStream inputStream = new ByteArrayInputStream(testJson.getBytes("UTF-8"));
        final JsonFactory factory = new JsonFactory();
        factory.configure(Feature.FLUSH_PASSED_TO_STREAM, false);
        factory.configure(Feature.AUTO_CLOSE_TARGET, false);
        final JsonParser parser = DocumentValidator.getValidatingParser(inputStream);
        try {
            try (final JsonGenerator gen = factory.createGenerator(outStream)) {
                while (parser.nextToken() != null) {
                    gen.copyCurrentEvent(parser);
                }
            }
            fail("invalidDocumentStreamTest failed: Validation done incorectly");
        } catch (final ValidationFailedException e) {
            System.out.println("invalidDocumentStreamTest: " + e);
            assertTrue(e.getMessage().contains("Type mismatch, data has object and schema has array"));
        }
    }

    @Test
    public void invalidRequiredIndexTest() throws Exception
    {
        System.out.println("invalidRequiredIndexTest...");
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        String testJson
            = "{"
            + "          'changeLog': ["
            + "             {"
            + "                'name': 'resetExistingField:1.0.0',"
            + "                'changes': ["
            + "                   {"
            + "                      'updateSubdocument': {"
            + "                         'reference': '1',"
            + "                         'changes': ["
            + "                            {"
            + "                               'setFields': {"
            + "                                  'SAMPLE_FIELD': ["
            + "                                     {"
            + "                                        'data': 'Subdocument 1: Sample field updated value'"
            + "                                     }"
            + "                                  ]"
            + "                               }"
            + "                            }"
            + "                         ]"
            + "                      }"
            + "                   }"
            + "                ]"
            + "             }"
            + "           ]"
            + "       }";
        testJson = testJson.replaceAll("'", "\"");
        final InputStream inputStream = new ByteArrayInputStream(testJson.getBytes("UTF-8"));
        final JsonFactory factory = new JsonFactory();
        factory.configure(Feature.FLUSH_PASSED_TO_STREAM, false);
        factory.configure(Feature.AUTO_CLOSE_TARGET, false);
        final JsonParser parser = DocumentValidator.getValidatingParser(inputStream);
        try {
            try (final JsonGenerator gen = factory.createGenerator(outStream)) {
                while (parser.nextToken() != null) {
                    gen.copyCurrentEvent(parser);
                }
            }
            fail("invalidRequiredIndexTest failed: Validation done incorectly");
        } catch (final ValidationFailedException e) {
            System.out.println("invalidRequiredIndexTest: " + e);
            assertTrue(e.getMessage().contains("Required property index is missing from object"));
        }
    }

    @Test
    public void invalidOneOfChangeTest() throws Exception
    {
        System.out.println("invalidOneOfChangeTest...");
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        String testJson
            = "{"
            + "          'changeLog': ["
            + "             {"
            + "                'name': 'resetExistingField:1.0.0',"
            + "                'changes': ["
            + "                   {"
            + "                      'setFields': {"
            + "                         'SAMPLE_FIELD': ["
            + "                           {"
            + "                               'data': 'Subdocument 1: Sample field updated value'"
            + "                           }"
            + "                         ]"
            + "                      }, "
            + "                      'updateSubdocument': {"
            + "                         'reference': '1',"
            + "                         'index': 0,"
            + "                         'changes': ["
            + "                            {"
            + "                               'setFields': {"
            + "                                  'SAMPLE_FIELD': ["
            + "                                     {"
            + "                                        'data': 'Subdocument 1: Sample field updated value'"
            + "                                     }"
            + "                                  ]"
            + "                               }"
            + "                            }"
            + "                         ]"
            + "                      }"
            + "                   }"
            + "                ]"
            + "             }"
            + "           ]"
            + "       }";
        testJson = testJson.replaceAll("'", "\"");
        final InputStream inputStream = new ByteArrayInputStream(testJson.getBytes("UTF-8"));
        final JsonFactory factory = new JsonFactory();
        factory.configure(Feature.FLUSH_PASSED_TO_STREAM, false);
        factory.configure(Feature.AUTO_CLOSE_TARGET, false);
        final JsonParser parser = DocumentValidator.getValidatingParser(inputStream);
        try {
            try (final JsonGenerator gen = factory.createGenerator(outStream)) {
                while (parser.nextToken() != null) {
                    gen.copyCurrentEvent(parser);
                }
            }
            fail("invalidOneOfChangeTest failed: Validation done incorectly");
        } catch (final ValidationFailedException e) {
            System.out.println("invalidOneOfChangeTest: " + e);
            assertTrue(e.getMessage().contains("2 of the oneOf validations succceeded"));
        }
    }
}
