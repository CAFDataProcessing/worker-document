/*
 * Copyright 2016-2019 Micro Focus or one of its affiliates.
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

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
                assertTrue("invalidScriptsDocumentTest", e.getMessage() != null);
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
                assertTrue("invalidPropertiesDocumentTest", e.getMessage() != null);
            }
        }
    }
}
