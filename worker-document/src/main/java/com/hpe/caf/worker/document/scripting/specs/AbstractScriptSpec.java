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
package com.hpe.caf.worker.document.scripting.specs;

import com.hpe.caf.worker.document.DocumentWorkerScript;
import com.hpe.caf.worker.document.exceptions.InvalidScriptException;
import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.model.ScriptEngineType;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptException;

public abstract class AbstractScriptSpec
{
    /**
     * Constructs the appropriate implementation for the specified script data.
     *
     * @param application the global data for the worker
     * @param script the object that contains the script data
     * @return an object that can be used to retrieve or compile the script, or {@code null} if the script data object is not initialized
     * @throws InvalidScriptException if the specified script data is invalid is some way
     */
    public static AbstractScriptSpec create(final ApplicationImpl application, final DocumentWorkerScript script)
        throws InvalidScriptException
    {
        Objects.requireNonNull(script);

        // Check that multiple script specification properties have not been specified
        final int numberOfSources = getNumberOfSources(script);
        if (numberOfSources == 0) {
            return null;
        } else if (numberOfSources != 1) {
            throw new InvalidScriptException(script, "Script must have a single source.");
        }

        // Check that a valid scripting engine has been specified
        final ScriptEngineType engineType;
        if (script.engine == null) {
            engineType = ScriptEngineType.NASHORN;
        } else {
            try {
                engineType = ScriptEngineType.valueOf(script.engine);
            } catch (final IllegalArgumentException e) {
                throw new InvalidScriptException(script, "Invalid engine type", e);
            }
        }

        // Construct the appropriate concreate implementation
        if (script.script != null) {
            return new InlineScriptSpec(script.script, engineType);
        } else if (script.storageRef != null) {
            return new StorageRefScriptSpec(application.getDataStore(), script.storageRef, engineType);
        } else if (script.url != null) {
            try {
                if (engineType != ScriptEngineType.NASHORN) {
                    return new UrlScriptSpec(new URL(script.url), engineType);
                } else {
                    return new NashornUrlScriptSpec(new URL(script.url));
                }
            } catch (final MalformedURLException | URISyntaxException ex) {
                throw new InvalidScriptException(script, "Script url is malformed or not standards compliant.", ex);
            }
        } else {
            throw new RuntimeException("Logical error: the script type is not recognised");
        }
    }

    /**
     * Compiles the script for later execution.
     *
     * @param compiler the engine to use to do the compilation
     * @return the object code for the script
     * @throws ScriptException if compilation fails
     */
    @Nonnull
    public abstract CompiledScript compile(Compilable compiler) throws ScriptException;

    /**
     * Returns the script represented by this specification.
     *
     * @return the script represented by this specification
     * @throws IOException if the script could not be retrieved
     */
    @Nonnull
    public abstract String getScript() throws IOException;

    /**
     * Returns whether the script specification represents a script that is static (i.e. it is always the same each time it is retrieved),
     * as opposed to one which is dynamic (i.e. it references a script which might change).
     *
     * @return true if the script specified is constant
     */
    public abstract boolean isStatic();

    /**
     * Returns the JavaScript engine to use for the script represented by this specification.
     *
     * @return the JavaScript engine to use for the script represented by this specification.
     */
    @Nonnull
    public abstract ScriptEngineType getEngineType();

    /**
     * Creates a new {@code DocumentWorkerScript} for the script specification.
     *
     * @param name the name of the script to use
     * @return the new DocumentWorkerScript object
     */
    @Nonnull
    public final DocumentWorkerScript toDocumentWorkerScript(final String name)
    {
        final DocumentWorkerScript script = new DocumentWorkerScript();
        script.name = name;

        setScriptSpecField(script);

        return script;
    }

    /**
     * Updates the relevant field in the specified script.
     *
     * @param script the script object to be updated
     */
    protected abstract void setScriptSpecField(DocumentWorkerScript script);

    /**
     * Returns a count of the script specifications set on the specified object.
     * <p>
     * If the object is properly initialized it will contain one and only one.
     *
     * @param script the script object to be examined
     * @return the number of script specifications
     */
    private static int getNumberOfSources(final DocumentWorkerScript script)
    {
        int sourceCount = 0;

        if (script.script != null) {
            sourceCount++;
        }

        if (script.storageRef != null) {
            sourceCount++;
        }

        if (script.url != null) {
            sourceCount++;
        }

        return sourceCount;
    }
}
