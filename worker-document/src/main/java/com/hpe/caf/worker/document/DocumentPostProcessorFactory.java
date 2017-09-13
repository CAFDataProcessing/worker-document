/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development LP.
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

import com.google.common.base.Strings;
import com.hpe.caf.api.worker.DataStore;
import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.api.worker.TaskRejectedException;
import com.hpe.caf.worker.document.model.Document;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class DocumentPostProcessorFactory
{
    private static final Logger LOG = LoggerFactory.getLogger(DocumentPostProcessorFactory.class);
    private final Object syncObj = new Object();

    private final HashMap<String, String> cachedScripts = new HashMap<>();

    public DocumentPostProcessor create(Document document) throws TaskRejectedException
    {
        LOG.info("Executing post-processing - checking if script is provided... ");
        String postProcessingScriptReference = document.getCustomData("POST_PROCESSING_SCRIPT");
        if (!Strings.isNullOrEmpty(postProcessingScriptReference)) {
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

    private static String retrieveScript(String reference, Document document) throws TaskRejectedException
    {
        DataStore dataStore = document.getApplication().getService(DataStore.class);

        try (InputStream stream = dataStore.retrieve(reference)) {
            return IOUtils.toString(stream);
        }
        catch (DataStoreException | IOException e) {
            LOG.error("Could not retrieve post-processing script from DataStore.", e);
            throw new TaskRejectedException("Could not retrieve post-processing script from DataStore.", e);
        }
    }
}
