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
package com.hpe.caf.worker.document.impl;

import com.hpe.caf.worker.document.DocumentWorkerScript;
import com.hpe.caf.worker.document.exceptions.InvalidScriptException;
import com.hpe.caf.worker.document.model.Script;
import com.hpe.caf.worker.document.model.Task;
import com.hpe.caf.worker.document.scripting.JavaScriptManager;
import com.hpe.caf.worker.document.scripting.specs.AbstractScriptSpec;
import com.hpe.caf.worker.document.scripting.specs.InlineScriptSpec;
import com.hpe.caf.worker.document.scripting.specs.StorageRefScriptSpec;
import com.hpe.caf.worker.document.scripting.specs.UrlScriptSpec;
import com.hpe.caf.worker.document.tasks.AbstractTask;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.JSObject;

public final class ScriptImpl extends DocumentWorkerObjectImpl implements Script
{
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
        final Bindings newGlobal = javaScriptManager.createNewGlobal();

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
            loadedScriptBindings = null;
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
        throwIfLoaded();
        this.scriptSpec = new StorageRefScriptSpec(application.getDataStore(), reference);
    }

    @Override
    public void setScriptByUrl(final URL url)
    {
        throwIfLoaded();
        try {
            this.scriptSpec = new UrlScriptSpec(url);
        } catch (final URISyntaxException ex) {
            throw new RuntimeException("URL is not strictly formatted in accordance with RFC2396", ex);
        }
    }

    @Override
    public void setScriptInline(final String script)
    {
        throwIfLoaded();
        this.scriptSpec = new InlineScriptSpec(script);
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
