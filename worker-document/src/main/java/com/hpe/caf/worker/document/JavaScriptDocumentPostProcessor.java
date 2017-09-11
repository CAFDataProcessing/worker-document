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
import com.hpe.caf.api.worker.InvalidTaskException;
import com.hpe.caf.api.worker.TaskRejectedException;
import com.hpe.caf.worker.document.model.Document;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class JavaScriptDocumentPostProcessor implements DocumentPostProcessor
{
    private static final Logger LOG = LoggerFactory.getLogger(JavaScriptDocumentPostProcessor.class);

    private final Object syncObj = new Object();
    private String cachedScript;
    private String cachedScriptReference;

    @Override
    public void postProcessDocument(Document document) throws TaskRejectedException, InvalidTaskException
    {
        LOG.info("Executing post-processing - checking if script is provided... ");
        String postProcessingScriptReference = document.getCustomData("POST_PROCESSING_SCRIPT");
        if (!Strings.isNullOrEmpty(postProcessingScriptReference)) {
            LOG.info("Executing post-processing - script was provided... ");
            final ScriptEngineManager engineManager = new ScriptEngineManager();
            final ScriptEngine engine = engineManager.getEngineByName("nashorn");
            synchronized (syncObj) {
                if (!Objects.equals(cachedScriptReference, postProcessingScriptReference)) {
                    DataStore dataStore = document.getApplication().getService(DataStore.class);

                        try (InputStream stream = dataStore.retrieve(postProcessingScriptReference)) {
                            cachedScript = IOUtils.toString(stream);
                            cachedScriptReference = postProcessingScriptReference;
                        }
                        catch (DataStoreException | IOException e) {
                            LOG.error("Could not retrieve post-processing script from DataStore.", e);
                            throw new TaskRejectedException("Could not retrieve post-processing script from DataStore.", e);
                        }
                }
            }
            try {
                engine.eval(cachedScript);
                final Invocable invocable = (Invocable) engine;
                invocable.invokeFunction("processDocument", document);
                LOG.info("Executing post-processing - finished... ");
            }
            catch (ScriptException | NoSuchMethodException e) {
                LOG.error("Could not execute the post-processing script", e);
                throw new InvalidTaskException("Could not execute the post-processing script.", e);
            }
        }
    }
}
