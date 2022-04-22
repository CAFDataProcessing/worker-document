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

import com.hpe.caf.worker.document.DocumentWorkerScript;
import com.hpe.caf.worker.document.model.ScriptEngineType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class UrlScriptSpec extends RemoteScriptSpec
{
    private final URL url;
    private final URI uri;

    public UrlScriptSpec(final URL url, final ScriptEngineType engineType) throws URISyntaxException
    {
        super(engineType);
        this.url = Objects.requireNonNull(url);
        this.uri = url.toURI();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof UrlScriptSpec)) {
            return false;
        }

        final UrlScriptSpec other = (UrlScriptSpec) obj;

        return uri.equals(other.uri) && engineType.equals(other.engineType);
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 31 * hash + uri.hashCode();
        hash = 31 * hash + engineType.hashCode();
        return hash;
    }

    @Override
    public boolean isStatic()
    {
        return false;
    }

    @Nonnull
    @Override
    protected Reader openReader() throws IOException
    {
        return new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
    }

    @Override
    protected void setScriptSpecField(final DocumentWorkerScript script)
    {
        script.url = url.toString();
    }
}
