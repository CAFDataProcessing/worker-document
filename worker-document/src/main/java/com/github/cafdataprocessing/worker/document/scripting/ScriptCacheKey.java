/*
 * Copyright 2016-2024 Open Text.
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
package com.github.cafdataprocessing.worker.document.scripting;

import com.github.cafdataprocessing.worker.document.scripting.specs.AbstractScriptSpec;

import java.util.Objects;

public final class ScriptCacheKey
{
    private final String name;
    private final AbstractScriptSpec scriptSpec;

    public ScriptCacheKey(final String name, final AbstractScriptSpec scriptSpec)
    {
        this.name = name;
        this.scriptSpec = Objects.requireNonNull(scriptSpec);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof ScriptCacheKey)) {
            return false;
        }

        final ScriptCacheKey other = (ScriptCacheKey) obj;

        return Objects.equals(this.name, other.name)
            && Objects.equals(this.scriptSpec, other.scriptSpec);
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(name);
        hash = 97 * hash + Objects.hashCode(scriptSpec);
        return hash;
    }

    public String getName()
    {
        return name;
    }

    public AbstractScriptSpec getScriptSpec()
    {
        return scriptSpec;
    }
}
