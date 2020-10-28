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
package com.hpe.caf.worker.document.impl;

import com.hpe.caf.worker.document.DocumentWorkerScript;
import com.hpe.caf.worker.document.exceptions.InvalidScriptException;
import com.hpe.caf.worker.document.model.Script;
import com.hpe.caf.worker.document.model.ScriptEngineType;
import com.hpe.caf.worker.document.model.Task;
import com.hpe.caf.worker.document.scripting.JavaScriptManager;
import com.hpe.caf.worker.document.scripting.specs.AbstractScriptSpec;
import com.hpe.caf.worker.document.scripting.specs.InlineScriptSpec;
import com.hpe.caf.worker.document.scripting.specs.NashornUrlScriptSpec;
import com.hpe.caf.worker.document.scripting.specs.StorageRefScriptSpec;
import com.hpe.caf.worker.document.scripting.specs.UrlScriptSpec;
import com.hpe.caf.worker.document.tasks.AbstractTask;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.JSObject;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ScriptImpl extends DocumentWorkerObjectImpl implements Script
{
    private static final Logger LOG = LoggerFactory.getLogger(ScriptImpl.class);

    private final AbstractTask task;

    private int lastKnownIndex;
    private String name;
    private AbstractScriptSpec scriptSpec;
    private boolean isInstalled;
    private Bindings loadedScriptBindings;

    public ScriptImpl(
        final ApplicationImpl application,
        final AbstractTask task
    )
    {
        super(application);
        this.task = Objects.requireNonNull(task);
        this.lastKnownIndex = 0;
        this.name = null;
        this.scriptSpec = null;
        this.isInstalled = false;
        this.loadedScriptBindings = null;
    }

    public ScriptImpl(
        final ApplicationImpl application,
        final AbstractTask task,
        final DocumentWorkerScript script
    ) throws InvalidScriptException
    {
        this(application, task);
        this.name = Objects.requireNonNull(script).name;
        this.scriptSpec = AbstractScriptSpec.create(application, script);
    }

    @Override
    public int getIndex()
    {
        final int currentIndex = task.getScripts().getCurrentScriptIndex(lastKnownIndex, this);

        lastKnownIndex = currentIndex;
        return currentIndex;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Object getObject()
    {
        final Bindings bindings = loadedScriptBindings;

        return (bindings == null)
            ? null
            : bindings.get("thisScriptObject");
    }

    @Override
    public String getScript() throws IOException
    {
        return (scriptSpec == null)
            ? null
            : scriptSpec.getScript();
    }

    @Nonnull
    @Override
    public Task getTask()
    {
        return task;
    }

    @Override
    public void install()
    {
        isInstalled = true;
    }

    @Override
    public boolean isInstalled()
    {
        return isInstalled;
    }

    @Override
    public boolean isLoaded()
    {
        return (loadedScriptBindings != null);
    }

    @Override
    public void load() throws ScriptException
    {
        // Check that the script isn't already loaded
        if (isLoaded()) {
            return;
        }

        // Check that the script has been initialised
        if (scriptSpec == null) {
            throw new RuntimeException("The script must be initialized before it can be loaded.");
        }

        // Get a reference to the script manager
        final JavaScriptManager javaScriptManager = application.getJavaScriptManager();

        // Get the object code for the script
        final CompiledScript scriptObjectCode = javaScriptManager.getObjectCode(name, scriptSpec);

        // Create a new global context for the script to run in
        final Bindings newGlobal = javaScriptManager.createNewGlobal(scriptSpec.getEngineType());

        // Add a reference to this script object into the script's global context
        newGlobal.put("thisScript", this);

        // Mark the script as loaded from this point (as the only time we want to distinuish between loaded and loading).
        // If the script throws an exception then we'll undo this to put the script back into an unloaded state.
        loadedScriptBindings = newGlobal;

        try {
            // Execute the script in the new context created for it
            scriptObjectCode.eval(newGlobal);

        } catch (final Exception ex) {
            // If there is an exception then unload the script before propagating the exception
            unloadScriptBindings();
            throw ex;
        }
    }

    @Override
    public void setName(final String name)
    {
        throwIfLoaded();
        this.name = name;
    }

    @Override
    public void setScriptByReference(final String reference)
    {
        setScriptByReference(reference, ScriptEngineType.NASHORN);
    }

    @Override
    public void setScriptByReference(final String reference, final ScriptEngineType engineType)
    {
        throwIfLoaded();
        this.scriptSpec = new StorageRefScriptSpec(application.getDataStore(), reference, engineType);
    }

    @Override
    public void setScriptByUrl(final URL url)
    {
        setScriptByUrl(url, ScriptEngineType.NASHORN);
    }

    @Override
    public void setScriptByUrl(final URL url, final ScriptEngineType engineType)
    {
        throwIfLoaded();
        try {
            if (engineType == ScriptEngineType.NASHORN) {
                this.scriptSpec = new NashornUrlScriptSpec(url);
            } else {
                this.scriptSpec = new UrlScriptSpec(url, engineType);
            }
        } catch (final URISyntaxException ex) {
            throw new RuntimeException("URL is not strictly formatted in accordance with RFC2396", ex);
        }
    }

    @Override
    public void setScriptInline(final String script)
    {
        setScriptInline(script, ScriptEngineType.NASHORN);
    }

    @Override
    public void setScriptInline(final String script, final ScriptEngineType engineType)
    {
        throwIfLoaded();
        this.scriptSpec = new InlineScriptSpec(script, engineType);
    }

    private void throwIfLoaded()
    {
        if (isLoaded()) {
            throw new RuntimeException("The script must be unloaded before it can be updated.");
        }
    }

    @Override
    public void uninstall()
    {
        isInstalled = false;
    }

    @Override
    public void unload()
    {
        LOG.warn("RORY - in unload");
        unloadScriptBindings();
    }

    private void unloadScriptBindings() {
        if (loadedScriptBindings instanceof AutoCloseable) {
            try {
                ((AutoCloseable) loadedScriptBindings).close();
                LOG.warn("RORY - called close on bindings");
            } catch (final Exception ex) {
                LOG.warn("Unable to close script bindings and associated context", ex);
            }
        }
        loadedScriptBindings = null;
    }

    /**
     * Raises the specified event in this script if it is currently loaded.
     * <p>
     * If the event throws a checked exception then this will be wrapped in a {@code RuntimeException}.
     *
     * @param event the event to raise
     * @param args the arguments to be passed to the event
     */
    public void raiseEvent(final String event, final Object... args)
    {
        // Check that the script is loaded
        final Bindings bindings = loadedScriptBindings;
        if (bindings == null) {
            return;
        }

        // Check if there is a JavaScript function event handler for the event
        final Object eventHandler = bindings.get(event);
        if (scriptSpec.getEngineType() == ScriptEngineType.GRAAL_JS) {
            graalHandleEvent(eventHandler, args);
        } else {
            nashornHandleEvent(eventHandler, args);
        }
    }

    private static void graalHandleEvent(final Object eventHandler, final Object[] args)
    {
        if (!(eventHandler instanceof Function)) {
            return;
        }

        final Value jsEventHandler = Value.asValue(eventHandler);
        if (!jsEventHandler.canExecute()) {
            return;
        }

        // Call the JavaScript function with the specified arguments
        // Graal automatically wraps checked exceptions in a PolyglotException
        jsEventHandler.executeVoid(args);
    }

    private static void nashornHandleEvent(final Object eventHandler, final Object[] args)
    {
        if (!(eventHandler instanceof JSObject)) {
            return;
        }

        final JSObject jsEventHandler = (JSObject) eventHandler;
        if (!jsEventHandler.isFunction()) {
            return;
        }

        // Call the JavaScript function with the specified arguments
        // Nashorn automatically wraps checked exceptions in a RuntimeException
        jsEventHandler.call(null, args);
    }

    public boolean shouldIncludeInResponse()
    {
        return isInstalled && (scriptSpec != null);
    }

    public DocumentWorkerScript toDocumentWorkerScript()
    {
        return (scriptSpec == null)
            ? null
            : scriptSpec.toDocumentWorkerScript(name);
    }
}
