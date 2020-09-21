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

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

public final class GraalJSEngine extends JavaScriptEngine
{
    public GraalJSEngine()
    {
        super(GraalJSScriptEngine.create(
            null,
            Context.newBuilder("js")
                .allowExperimentalOptions(true) // Needed for loading from classpath
                .allowHostAccess(HostAccess.ALL) // Allow JS access to public Java methods/members
                .allowHostClassLookup(s -> true) // Allow JS access to public Java classes
                .option("js.load-from-classpath", "true")
        ));
    }
}
