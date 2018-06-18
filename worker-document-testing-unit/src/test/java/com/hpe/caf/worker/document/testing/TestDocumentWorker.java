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
package com.hpe.caf.worker.document.testing;

import com.hpe.caf.api.worker.DataStore;
import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.extensibility.DocumentWorker;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.Field;
import com.hpe.caf.worker.document.model.FieldValue;
import com.hpe.caf.worker.document.model.FieldValues;
import com.hpe.caf.worker.document.model.HealthMonitor;
import com.hpe.caf.worker.document.model.Subdocument;
import com.hpe.caf.worker.document.model.Subdocuments;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TestDocumentWorker implements DocumentWorker
{
    public static final String CustomDataStorageReference = "STORAGE_REFERENCE";
    public static final String CustomDataFieldValueToAdd = "FieldToAdd";
    public static final String FieldsTitle = "TITLE";
    public static final String ResultContentFieldWordCount = "ContentWordCount";
    public static final String ResultTitleFieldWordCount = "TitleWordCount";
    public static final String FieldToDelete = "FieldToRemove";
    public static final String FieldToRemoveValue = "FieldToRemoveValue";
    public static final String FieldValueToRemove = "FieldValueToRemove";

    @Override
    public void checkHealth(final HealthMonitor healthMonitor)
    {
    }

    @Override
    public void processDocument(final Document document) throws InterruptedException, DocumentWorkerTransientException
    {
        final DataStore dataStore = document.getApplication().getService(DataStore.class);
        //String storageReference = document.getCustomData(CustomDataStorageReference);
        final String storageReference = document.getField(CustomDataStorageReference).getValues().stream().findFirst().get().getReference();

        int words = 0;

        try (final InputStream inputFile = dataStore.retrieve(storageReference)) {
            final Scanner s = new Scanner(inputFile);

            while (s.hasNext("\\w+")) {
                s.next("\\w+");
                words++;
            }
        } catch (final DataStoreException | IOException e) {
            throw new DocumentWorkerTransientException(e);
        }

        final List<String> stringValues = document.getField(FieldsTitle).getStringValues();
        final String titleField = stringValues.get(0);

        final String[] split = titleField.split("\\s");

        document.getField(ResultTitleFieldWordCount).add(String.valueOf(split.length));
        document.getField(ResultContentFieldWordCount).add(String.valueOf(words));

        final String fieldValueToAdd = document.getCustomData(CustomDataFieldValueToAdd);
        document.getField(CustomDataFieldValueToAdd).add(fieldValueToAdd);

        document.getField(FieldToDelete).clear();
        final Field field = document.getField(FieldToRemoveValue);
        final FieldValues values = field.getValues();
        field.clear();
        final List<FieldValue> fieldValues = values.stream()
            .filter(v -> !v.getStringValue().equals(FieldValueToRemove))
            .collect(Collectors.toList());

        for (final FieldValue fieldValue : fieldValues) {
            field.add(fieldValue.getStringValue());
        }
        final Subdocuments subdocuments = document.getSubdocuments();
        for (int index = 0; index < subdocuments.size(); index++) {
            final Subdocument subdocument = subdocuments.get(index);
            processDocument(subdocument);
        }
    }
}
