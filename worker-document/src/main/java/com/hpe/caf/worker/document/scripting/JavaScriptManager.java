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

import com.hpe.caf.worker.document.config.ScriptCacheConfiguration;
import com.hpe.caf.worker.document.config.ScriptCachingConfiguration;
import com.hpe.caf.worker.document.scripting.specs.AbstractScriptSpec;
import javax.annotation.Nonnull;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;

public final class JavaScriptManager implements ObjectCodeProvider
{
    private static final long DEFAULT_SCRIPT_CACHE_SIZE = 50;
    private static final long DEFAULT_STATIC_SCRIPT_CACHE_DURATION = 30 * 60;   // 30 minutes
    private static final long DEFAULT_DYNAMIC_SCRIPT_CACHE_DURATION = 30 * 60;  // 30 minutes

    private final JavaScriptEngineLazy scriptEngine;
    private final ScriptCache staticScriptCache;
    private final ScriptCache dynamicScriptCache;

    public JavaScriptManager(final ScriptCachingConfiguration scriptCachingConfig)
    {
        final JavaScriptEngineLazy javaScriptEngine = new JavaScriptEngineLazy();

        this.scriptEngine = javaScriptEngine;
        this.staticScriptCache = new ScriptCache(
            (scriptCachingConfig == null) ? null : scriptCachingConfig.getStaticScriptCache(),
            getDefaultStaticScriptCacheConfig(),
            javaScriptEngine);
        this.dynamicScriptCache = new ScriptCache(
            (scriptCachingConfig == null) ? null : scriptCachingConfig.getDynamicScriptCache(),
            getDefaultDynamicScriptCacheConfig(),
            javaScriptEngine);
    }

    @Nonnull
    public Bindings createNewGlobal()
    {
        return scriptEngine.createNewGlobal();
    }

    @Nonnull
    @Override
    public CompiledScript getObjectCode(final String name, final AbstractScriptSpec scriptSpec) throws ScriptException
    {
        // Choose the cache
        final ScriptCache scriptCache = scriptSpec.isStatic()
            ? staticScriptCache
            : dynamicScriptCache;

        // Retrieve the compiled script from the cache
        return scriptCache.getObjectCode(name, scriptSpec);
    }

    @Nonnull
    private static ScriptCacheConfiguration getDefaultStaticScriptCacheConfig()
    {
        final ScriptCacheConfiguration cacheConfig = new ScriptCacheConfiguration();
        cacheConfig.setMaximumSize(DEFAULT_SCRIPT_CACHE_SIZE);
        cacheConfig.setExpireAfterAccess(DEFAULT_STATIC_SCRIPT_CACHE_DURATION);
        return cacheConfig;
    }

    @Nonnull
    private static ScriptCacheConfiguration getDefaultDynamicScriptCacheConfig()
    {
        final ScriptCacheConfiguration cacheConfig = new ScriptCacheConfiguration();
        cacheConfig.setMaximumSize(DEFAULT_SCRIPT_CACHE_SIZE);
        cacheConfig.setExpireAfterWrite(DEFAULT_DYNAMIC_SCRIPT_CACHE_DURATION);
        return cacheConfig;
    }
}
