/*
 * Copyright 2016-2021 Micro Focus or one of its affiliates.
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
package com.hpe.caf.worker.document.model;

/**
 * Scripting engines supported by the Document Worker Framework.
 */
public enum ScriptEngineType
{
    /**
     * GraalVM JavaScript engine
     *
     * @see <a href="https://www.graalvm.org/">GraalVM</a>
     * @see <a href="https://www.graalvm.org/reference-manual/js/">GraalVM JavaScript Implementation</a>
     * @see <a href="https://www.graalvm.org/reference-manual/js/NashornMigrationGuide/">Nashorn Migration Guide</a>
     */
    GRAAL_JS,
    /**
     * Nashorn JavaScript engine
     *
     * @see <a href="https://en.wikipedia.org/wiki/Nashorn_(JavaScript_engine)">Nashorn (JavaScript engine)</a>
     */
    NASHORN,
}
