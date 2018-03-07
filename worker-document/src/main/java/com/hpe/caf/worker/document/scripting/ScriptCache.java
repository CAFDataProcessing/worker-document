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
package com.hpe.caf.worker.document.scripting;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hpe.caf.worker.document.config.ScriptCacheConfiguration;
import com.hpe.caf.worker.document.scripting.specs.AbstractScriptSpec;
import static com.hpe.caf.worker.document.util.ObjectFunctions.coalesce;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.script.CompiledScript;
import javax.script.ScriptException;

public final class ScriptCache implements ObjectCodeProvider
{
    private final LoadingCache<ScriptCacheKey, CompiledScript> cache;

    public ScriptCache(
        final ScriptCacheConfiguration config,
        final ScriptCacheConfiguration defaultConfig,
        final ObjectCodeProvider compiler
    )
    {
        this.cache = createCacheBuilder(config, defaultConfig).build(createCacheLoader(compiler));
    }

    @Nonnull
    @Override
    public CompiledScript getObjectCode(final String name, final AbstractScriptSpec scriptSpec) throws ScriptException
    {
        // Put together the cache key
        final ScriptCacheKey key = new ScriptCacheKey(name, scriptSpec);

        // Retrieve the compiled script from the cache (unwrapping the exception if one was thrown)
        try {
            return cache.get(key);
        } catch (final ExecutionException ex) {
            final Throwable cause = ex.getCause();

            if (cause instanceof ScriptException) {
                throw (ScriptException) cause;
            } else {
                throw new RuntimeException(ex);
            }
        }
    }

    @Nonnull
    private static CacheBuilder createCacheBuilder(final ScriptCacheConfiguration config, final ScriptCacheConfiguration defaultConfig)
    {
        Objects.requireNonNull(defaultConfig);

        final CacheBuilder cacheBuilder = CacheBuilder.newBuilder()
            .concurrencyLevel(1);

        if (config == null) {
            setMaximumSize(cacheBuilder, defaultConfig.getMaximumSize());
            setExpireAfterAccess(cacheBuilder, defaultConfig.getExpireAfterAccess());
            setExpireAfterWrite(cacheBuilder, defaultConfig.getExpireAfterWrite());
        } else {
            setMaximumSize(cacheBuilder, coalesce(config.getMaximumSize(), defaultConfig.getMaximumSize()));
            setExpireAfterAccess(cacheBuilder, coalesce(config.getExpireAfterAccess(), defaultConfig.getExpireAfterAccess()));
            setExpireAfterWrite(cacheBuilder, coalesce(config.getExpireAfterWrite(), defaultConfig.getExpireAfterWrite()));
        }

        return cacheBuilder;
    }

    @Nonnull
    private static CacheLoader<ScriptCacheKey, CompiledScript> createCacheLoader(final ObjectCodeProvider compiler)
    {
        Objects.requireNonNull(compiler);

        return new CacheLoader<ScriptCacheKey, CompiledScript>()
        {
            @Nonnull
            @Override
            public CompiledScript load(final ScriptCacheKey key) throws ScriptException
            {
                final String name = key.getName();
                final AbstractScriptSpec scriptSpec = key.getScriptSpec();

                return compiler.getObjectCode(name, scriptSpec);
            }
        };
    }

    private static void setMaximumSize(final CacheBuilder cacheBuilder, final Long maxSize)
    {
        if (maxSize != null) {
            cacheBuilder.maximumSize(maxSize);
        }
    }

    private static void setExpireAfterAccess(final CacheBuilder cacheBuilder, final Long expireAfterAccess)
    {
        if (expireAfterAccess != null) {
            cacheBuilder.expireAfterAccess(expireAfterAccess, TimeUnit.SECONDS);
        }
    }

    private static void setExpireAfterWrite(final CacheBuilder cacheBuilder, final Long expireAfterWrite)
    {
        if (expireAfterWrite != null) {
            cacheBuilder.expireAfterWrite(expireAfterWrite, TimeUnit.SECONDS);
        }
    }
}