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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hpe.caf.worker.document.model.ScriptEngineType;
import com.hpe.caf.worker.document.scripting.specs.AbstractScriptSpec;
import javax.annotation.Nonnull;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;

public final class JavaScriptEngineLazy implements ObjectCodeProvider
{
    private final LoadingCache<ScriptEngineType, JavaScriptEngine> scriptEngine;

    public JavaScriptEngineLazy()
    {
        this.scriptEngine = CacheBuilder.newBuilder().concurrencyLevel(1).build(CacheLoader.from(JavaScriptEngineLazy::buildEngine));
    }

    @Nonnull
    public Bindings createNewGlobal(final ScriptEngineType engineType)
    {
        return scriptEngine.getUnchecked(engineType).createNewGlobal();
    }

    @Nonnull
    @Override
    public CompiledScript getObjectCode(final String name, final AbstractScriptSpec scriptSpec) throws ScriptException
    {
        return scriptEngine.getUnchecked(scriptSpec.getEngineType()).getObjectCode(name, scriptSpec);
    }

    @Nonnull
    private static JavaScriptEngine buildEngine(final ScriptEngineType engineType)
    {
        if (engineType == ScriptEngineType.GRAAL_JS) {
            return new GraalJSEngine();
        } else {
            return new NashornJSEngine();
        }
    }

    @Nonnull
    public JavaScriptEngine getEngine(final ScriptEngineType engineType) {
        return scriptEngine.getUnchecked(engineType);
    }
}
