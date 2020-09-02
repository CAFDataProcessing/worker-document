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
package com.hpe.caf.worker.document.scripting;

import com.hpe.caf.worker.document.scripting.specs.AbstractScriptSpec;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import javax.annotation.Nonnull;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

public final class JavaScriptEngine implements ObjectCodeProvider
{
    private final ScriptEngine scriptEngine;
    private final Bindings scriptEngineBindings;
    private final Object scriptEngineBindingsLock;

    public JavaScriptEngine()
    {

        this.scriptEngine = GraalJSScriptEngine.create(null,
                Context.newBuilder("js")
                        .allowExperimentalOptions(true) //Needed for loading from classpath
                        .allowHostAccess(HostAccess.ALL) //Allow JS access to public Java methods/members
                        .allowHostClassLookup(s -> true) //Allow JS access to public Java classes
                        .allowIO(true) //Allow JS IO access to load additional scripts
                        .option("js.load-from-classpath", "true") //Allow JS to load files from the classpath
                        .option("js.syntax-extensions", "true"));
        this.scriptEngineBindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        this.scriptEngineBindingsLock = new Object();
    }

    @Nonnull
    public Bindings createNewGlobal()
    {
        return scriptEngine.createBindings();
    }

    @Nonnull
    @Override
    public CompiledScript getObjectCode(final String name, final AbstractScriptSpec scriptSpec) throws ScriptException
    {
        // Synchronize compilations so that the correct filename is set whilst the compilation is occurring
        synchronized (scriptEngineBindingsLock) {

            // Set the name of the script to be compiled
            // Unfortunately it seems that it has to be put into the script engine context
            if (name != null) {
                scriptEngineBindings.put(ScriptEngine.FILENAME, name);
            }

            try {
                // Compile the script
                return scriptSpec.compile((Compilable) scriptEngine);

            } finally {
                // Reset the script name
                if (name != null) {
                    scriptEngineBindings.remove(ScriptEngine.FILENAME);
                }
            }
        }
    }
}
