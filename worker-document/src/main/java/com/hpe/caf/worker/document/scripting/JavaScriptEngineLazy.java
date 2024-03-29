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
package com.hpe.caf.worker.document.scripting;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.hpe.caf.worker.document.scripting.specs.AbstractScriptSpec;
import jakarta.annotation.Nonnull;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;

public final class JavaScriptEngineLazy implements ObjectCodeProvider
{
    private final Supplier<JavaScriptEngine> scriptEngine;

    public JavaScriptEngineLazy()
    {
        this.scriptEngine = Suppliers.memoize(JavaScriptEngine::new);
    }

    @Nonnull
    public Bindings createNewGlobal()
    {
        return scriptEngine.get().createNewGlobal();
    }

    @Nonnull
    @Override
    public CompiledScript getObjectCode(final String name, final AbstractScriptSpec scriptSpec) throws ScriptException
    {
        return scriptEngine.get().getObjectCode(name, scriptSpec);
    }
}
