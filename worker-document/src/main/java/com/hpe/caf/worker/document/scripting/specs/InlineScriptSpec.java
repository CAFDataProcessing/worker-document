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
import com.hpe.caf.worker.document.model.ScriptEngineType;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptException;

public final class InlineScriptSpec extends AbstractScriptSpec
{
    private final String script;
    private final ScriptEngineType engineType;

    public InlineScriptSpec(final String script, final ScriptEngineType type)
    {
        this.script = Objects.requireNonNull(script);
        this.engineType = type;
    }

    @Nonnull
    @Override
    public CompiledScript compile(final Compilable compiler) throws ScriptException
    {
        return compiler.compile(script);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof InlineScriptSpec)) {
            return false;
        }

        final InlineScriptSpec other = (InlineScriptSpec) obj;

        return script.equals(other.script);
    }

    @Nonnull
    @Override
    public String getScript()
    {
        return script;
    }

    @Override
    public int hashCode()
    {
        return script.hashCode();
    }

    @Override
    public boolean isStatic()
    {
        return true;
    }

    @Override
    public ScriptEngineType getEngineType()
    {
        return engineType;
    }

    @Override
    protected void setScriptSpecField(final DocumentWorkerScript script)
    {
        script.script = this.script;
    }
}
