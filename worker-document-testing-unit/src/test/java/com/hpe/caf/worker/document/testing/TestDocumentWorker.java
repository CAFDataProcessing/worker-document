package com.hpe.caf.worker.document.testing;

import com.hpe.caf.api.worker.DataStore;
import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.extensibility.DocumentWorker;
import com.hpe.caf.worker.document.model.*;

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
    public void checkHealth(HealthMonitor healthMonitor)
    {
    }

    @Override
    public void processDocument(Document document) throws InterruptedException, DocumentWorkerTransientException
    {
        DataStore dataStore = document.getApplication().getService(DataStore.class);
        //String storageReference = document.getCustomData(CustomDataStorageReference);
        String storageReference = document.getField(CustomDataStorageReference).getValues().stream().findFirst().get().getReference();

        int words = 0;

        try (InputStream inputFile = dataStore.retrieve(storageReference)) {
            Scanner s = new Scanner(inputFile);

            while (s.hasNext("\\w+")) {
                String word = s.next("\\w+");
                words++;
            }
        } catch (DataStoreException | IOException e) {
            throw new DocumentWorkerTransientException(e);
        }

        List<String> stringValues = document.getField(FieldsTitle).getStringValues();
        String titleField = stringValues.get(0);

        String[] split = titleField.split("\\s");

        document.getField(ResultTitleFieldWordCount).add(String.valueOf(split.length));
        document.getField(ResultContentFieldWordCount).add(String.valueOf(words));

        String fieldValueToAdd = document.getCustomData(CustomDataFieldValueToAdd);
        document.getField(CustomDataFieldValueToAdd).add(fieldValueToAdd);

        document.getField(FieldToDelete).clear();
        Field field = document.getField(FieldToRemoveValue);
        FieldValues values = field.getValues();
        field.clear();
        List<FieldValue> fieldValues = values.stream()
            .filter(v -> !v.getStringValue().equals(FieldValueToRemove))
            .collect(Collectors.toList());

        for (FieldValue fieldValue : fieldValues) {
            field.add(fieldValue.getStringValue());
        }
    }
}
