/*
 * Copyright 2016-2020 Micro Focus or one of its affiliates.
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
package com.hpe.caf.worker.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpe.caf.worker.testing.validation.CustomPropertyValidator;

public class DocumentWorkerFieldChangesValidator extends CustomPropertyValidator
{
    private final DocumentWorkerFieldValueValidator fieldValueValidator;
    private final ObjectMapper mapper = new ObjectMapper();

    public DocumentWorkerFieldChangesValidator(final DocumentWorkerFieldValueValidator fieldValueValidator)
    {
        this.fieldValueValidator = fieldValueValidator;
    }

    @Override
    public boolean canValidate(final String propertyName, final Object sourcePropertyValue, final Object validatorPropertyValue)
    {
        return tryToConvert(sourcePropertyValue, DocumentWorkerFieldChanges.class) != null
            && tryToConvert(validatorPropertyValue, DocumentWorkerFieldChangesExpectation.class) != null;
    }

    private <T> T tryToConvert(final Object value, final Class<T> classToConvert)
    {
        try {
            return mapper.convertValue(value, classToConvert);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    protected boolean isValid(final Object testedPropertyValue, final Object validatorPropertyValue)
    {
        final DocumentWorkerFieldChanges testedFieldChanges = tryToConvert(testedPropertyValue, DocumentWorkerFieldChanges.class);
        final DocumentWorkerFieldChangesExpectation validatorFieldChanges
            = tryToConvert(validatorPropertyValue, DocumentWorkerFieldChangesExpectation.class);

        if (testedFieldChanges.values == null && validatorFieldChanges == null) {
            return true;
        }
        if (testedFieldChanges.values == null || validatorFieldChanges == null) {
            return false;
        }
        if (testedFieldChanges.action != validatorFieldChanges.action) {
            return false;
        }
        if (testedFieldChanges.values.size() != validatorFieldChanges.values.size()) {
            return false;
        }

        for (final DocumentWorkerFieldValue testedValue : testedFieldChanges.values) {
            boolean isValid = false;
            for (final DocumentWorkerFieldValueExpectation validatorValue : validatorFieldChanges.values) {
                if (fieldValueValidator.isValid(testedValue, validatorValue)) {
                    isValid = true;
                    break;
                }
            }
            if (!isValid) {
                return false;
            }
        }
        return true;
    }
}
