/*
 * Copyright 2016-2022 Micro Focus or one of its affiliates.
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

import com.hpe.caf.worker.document.model.ScriptEngineType;
import java.io.IOException;
import java.io.Reader;
import javax.annotation.Nonnull;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import org.apache.commons.io.IOUtils;

public abstract class RemoteScriptSpec extends AbstractScriptSpec
{
    protected RemoteScriptSpec(final ScriptEngineType engineType)
    {
        super(engineType);
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the scripting engine hits an IOException when using the Reader then it wraps it in a ScriptException. By doing the same here the
     * calling code can more easily handle both cases consistently.
     */
    @Nonnull
    @Override
    public CompiledScript compile(final Compilable compiler) throws ScriptException
    {
        try (final Reader reader = openReader()) {
            return compiler.compile(reader);
        } catch (final IOException ex) {
            throw new ScriptException(ex);
        }
    }

    @Nonnull
    @Override
    public String getScript() throws IOException
    {
        return IOUtils.toString(openReader());
    }

    @Nonnull
    protected abstract Reader openReader() throws IOException;
}
