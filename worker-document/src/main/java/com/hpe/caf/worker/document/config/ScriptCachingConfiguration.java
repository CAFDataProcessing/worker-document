/*
 * Copyright 2018-2017 EntIT Software LLC, a Micro Focus company.
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
package com.hpe.caf.worker.document.config;

public final class ScriptCachingConfiguration
{
    /**
     * Configuration for caching static (unchanging) scripts
     */
    private ScriptCacheConfiguration staticScriptCache;

    /**
     * Configuration for caching dynamic scripts
     */
    private ScriptCacheConfiguration dynamicScriptCache;

    public ScriptCacheConfiguration getStaticScriptCache()
    {
        return staticScriptCache;
    }

    public void setStaticScriptCache(final ScriptCacheConfiguration staticScriptCache)
    {
        this.staticScriptCache = staticScriptCache;
    }

    public ScriptCacheConfiguration getDynamicScriptCache()
    {
        return dynamicScriptCache;
    }

    public void setDynamicScriptCache(final ScriptCacheConfiguration dynamicScriptCache)
    {
        this.dynamicScriptCache = dynamicScriptCache;
    }
}
