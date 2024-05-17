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
package com.hpe.caf.worker.document;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class GraalScriptEngineTest
{
    @Test
    public void testBinding() throws ScriptException
    {
        final ScriptEngine engine = GraalJSScriptEngine.create(
                null,
                Context.newBuilder("js")
                        .allowHostAccess(HostAccess.ALL)
        );
        final Bindings bindings = engine.createBindings();
        final CompiledScript compiledScript = ((Compilable) engine).compile(
                "function testFunction (a,b) { return a + b; } " +
                      "var testString = 'banana'; " +
                      "var testInt = 7; " +
                      "var testObj = {'key':'value'};");
        compiledScript.eval(bindings);
        //assert function maps correctly
        final Object testFunction = bindings.get("testFunction");
        assertTrue(testFunction instanceof Function);
        final Value parsed = Value.asValue(testFunction);
        assertTrue(parsed.canExecute());
        final Value result = parsed.execute(2, 2);
        assertEquals(4, result.asInt());
        //assert string maps correctly
        final Object testString = bindings.get("testString");
        assertEquals("banana", testString );
        //assert primitive maps correctly
        final Object testInt = bindings.get("testInt");
        assertEquals(7, testInt);
        //assert object maps correctly
        final Object testObj = bindings.get("testObj");
        assertTrue(testObj instanceof Map);
        assertTrue(((Map)testObj).containsKey("key"));
        assertEquals("value",((Map)testObj).get("key"));
        assertEquals("value", Value.asValue(testObj).getMember("key").asString());
    }
}
