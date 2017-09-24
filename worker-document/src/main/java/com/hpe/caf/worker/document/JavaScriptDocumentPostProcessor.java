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

import com.hpe.caf.worker.document.exceptions.PostProcessingFailedException;
import com.hpe.caf.worker.document.model.Document;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link DocumentPostProcessor} which executes a Java Script for post-processing.
 */
public class JavaScriptDocumentPostProcessor implements DocumentPostProcessor
{
    private static final Logger LOG = LoggerFactory.getLogger(JavaScriptDocumentPostProcessor.class);

    private final String postProcessingScript;

    public JavaScriptDocumentPostProcessor(final String postProcessingScript)
    {
        this.postProcessingScript = postProcessingScript;
    }

    @Override
    public void postProcessDocument(final Document document) throws PostProcessingFailedException
    {
        LOG.trace("Executing post-processing script.");
        final ScriptEngineManager engineManager = new ScriptEngineManager();
        final ScriptEngine engine = engineManager.getEngineByName("nashorn");
        try {
            engine.eval(postProcessingScript);
            final Invocable invocable = (Invocable) engine;
            invocable.invokeFunction("processDocument", document);
            LOG.trace("Executed post-processing script. ");
        } catch (ScriptException | NoSuchMethodException e) {
            throw new PostProcessingFailedException("Could not execute the post-processing script", e);
        }
    }
}
