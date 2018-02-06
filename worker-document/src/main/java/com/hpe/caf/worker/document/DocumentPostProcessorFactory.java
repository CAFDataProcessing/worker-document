/*
 * Copyright 2015-2017 EntIT Software LLC, a Micro Focus company.
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

import com.hpe.caf.api.worker.DataStore;
import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.api.worker.TaskRejectedException;
import com.hpe.caf.worker.document.model.Document;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for creating of {@link DocumentPostProcessor}. At the moment, this class will create a
 * {@link JavaScriptDocumentPostProcessor} if postProcessingScript custom data is provided, otherwise {@code null}.
 */
public class DocumentPostProcessorFactory
{
    private static final Logger LOG = LoggerFactory.getLogger(DocumentPostProcessorFactory.class);
    private final Object syncObj = new Object();

    private final HashMap<String, String> cachedScripts = new HashMap<>();

    public DocumentPostProcessor create(final Document document) throws TaskRejectedException
    {
        LOG.info("Executing post-processing - checking if script is provided... ");
        final String postProcessingScriptReference = document.getCustomData("postProcessingScript");
        if (postProcessingScriptReference != null && !postProcessingScriptReference.isEmpty()) {
            String postProcessingScript;
            synchronized (syncObj) {
                postProcessingScript = cachedScripts.get(postProcessingScriptReference);
                if (postProcessingScript == null) {
                    postProcessingScript = retrieveScript(postProcessingScriptReference, document);
                    cachedScripts.put(postProcessingScriptReference, postProcessingScript);
                }
            }
            return new JavaScriptDocumentPostProcessor(postProcessingScript);
        }
        return null;
    }

    private static String retrieveScript(final String reference, final Document document) throws TaskRejectedException
    {
        final DataStore dataStore = document.getApplication().getService(DataStore.class);

        try (final InputStream stream = dataStore.retrieve(reference)) {
            return IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (DataStoreException | IOException e) {
            LOG.error("Could not retrieve post-processing script from DataStore.", e);
            throw new TaskRejectedException("Could not retrieve post-processing script from DataStore.", e);
        }
    }
}
