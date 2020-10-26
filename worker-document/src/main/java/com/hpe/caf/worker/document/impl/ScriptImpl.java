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

import com.hpe.caf.worker.document.DocumentWorkerAdapter;
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
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
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
    private Method graalJSBindingsCloseMethod;
    private GraalJSScriptEngine graalJSScriptEngine;

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
    public void closeBindings()
    {
//        if (graalJSBindingsCloseMethod != null) {
//            try {
//                // This throws this exception every time its called - seems GraalJSBindings.close() context is already closed?
//                // c.h.c.w.c.BulkWorkerThreadPool: {"message":"Bulk Worker threw unhandled exception","exception":"com.oracle.truffle.api.TruffleStackTrace$LazyStackTrace: null\nWrapped by: java.lang.IllegalStateException: The Context is already closed.\n\tat com.oracle.truffle.polyglot.PolyglotEngineException.illegalState(PolyglotEngineException.java:126)\n\tat com.oracle.truffle.polyglot.PolyglotContextImpl.checkClosed(PolyglotContextImpl.java:741)\n\tat com.oracle.truffle.polyglot.PolyglotContextImpl.enterThreadChanged(PolyglotContextImpl.java:474)\n\tat com.oracle.truffle.polyglot.PolyglotEngineImpl.enter(PolyglotEngineImpl.java:1603)\n\tat com.oracle.truffle.polyglot.HostToGuestRootNode.execute(HostToGuestRootNode.java:87)\n\tat com.oracle.truffle.api.impl.DefaultCallTarget.call(DefaultCallTarget.java:102)\n\tat com.oracle.truffle.polyglot.PolyglotMap.entrySet(PolyglotMap.java:117)\n\tat com.oracle.truffle.js.scriptengine.GraalJSBindings.entrySet(GraalJSBindings.java:171)\n\tat java.base/java.util.AbstractMap.size(AbstractMap.java:85)\n\tat com.hpe.caf.worker.document.impl.ScriptImpl.closeBindings(ScriptImpl.java:104)\n\tat java.base/java.util.ArrayList.forEach(ArrayList.java:1541)\n\tat com.hpe.caf.worker.document.impl.ScriptsImpl.closeBindings(ScriptsImpl.java:84)\n\tat com.hpe.caf.worker.document.BulkDocumentMessageProcessor.lambda$closeBindings$0(BulkDocumentMessageProcessor.java:433)\n\tat java.base/java.util.ArrayList.forEach(ArrayList.java:1541)\n\tat com.hpe.caf.worker.document.BulkDocumentMessageProcessor.closeBindings(BulkDocumentMessageProcessor.java:433)\n\tat com.hpe.caf.worker.document.BulkDocumentWorkerAdapter.processTasks(BulkDocumentWorkerAdapter.java:48)\n\tat com.hpe.caf.worker.core.BulkWorkerThreadPool$BulkWorkerThread.execute(BulkWorkerThreadPool.java:89)\n\tat com.hpe.caf.worker.core.BulkWorkerThreadPool$BulkWorkerThread.run(BulkWorkerThreadPool.java:69)\n"}
//                LOG.warn("RORY - loadedScriptBindings.size() before close() " + loadedScriptBindings.size());
//                graalJSBindingsCloseMethod.invoke(loadedScriptBindings);
//             
//                LOG.warn("RORY - loadedScriptBindings.size() after close() " + loadedScriptBindings.size());
//            } catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
//                LOG.warn("RORY - Couldn't call close on bindings", ex);
//                throw new RuntimeException(ex);
//            }
//        }
        if (graalJSScriptEngine != null) {
            LOG.warn("RORY - calling graalJSScriptEngine.close()");
            graalJSScriptEngine.close();
            LOG.warn("RORY - called graalJSScriptEngine.close()");
        } else {
            LOG.error("RORY - graalJSScriptEngine shouldnt be null");
        }
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

        // Store a reference to the GraalJSBindings.close method needed to close the context associated with the engine.
        // See: https://github.com/graalvm/graaljs/issues/363
//        try {
//            graalJSBindingsCloseMethod = loadedScriptBindings.getClass().getMethod("close");
//            graalJSBindingsCloseMethod.setAccessible(true);
//            LOG.warn("RORY - Found close method found on loadedScriptBindings");
//        } catch (final Exception ignored) {
//            LOG.error("RORY - NO close method found on loadedScriptBindings");
//            // We may not be using the Graal engine, in which case loadedScriptBindings may not have a close method, which is fine.
//        }
        
        // Store a reference to the GraalJSScriptEngine (if present) to close the context associated with the engine.
        // See: https://github.com/graalvm/graaljs/issues/363
        final ScriptEngine engine = javaScriptManager.getScriptEngine(scriptSpec.getEngineType()).getEngine();
        if (engine instanceof GraalJSScriptEngine) {
            graalJSScriptEngine = (GraalJSScriptEngine)engine;
        }

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
