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
package com.hpe.caf.worker.document.scripting.specs;

import com.hpe.caf.api.worker.DataStore;
import com.hpe.caf.worker.document.DocumentWorkerScript;
import com.hpe.caf.worker.document.model.ScriptEngineType;
import com.hpe.caf.worker.document.util.DataStoreFunctions;
import jakarta.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class StorageRefScriptSpec extends RemoteScriptSpec
{
    private final DataStore dataStore;
    private final String storageRef;

    public StorageRefScriptSpec(final DataStore dataStore, final String storageRef, final ScriptEngineType engineType)
    {
        super(engineType);
        this.dataStore = Objects.requireNonNull(dataStore);
        this.storageRef = Objects.requireNonNull(storageRef);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof StorageRefScriptSpec)) {
            return false;
        }

        final StorageRefScriptSpec other = (StorageRefScriptSpec) obj;

        return dataStore.equals(other.dataStore)
            && storageRef.equals(other.storageRef)
            && engineType.equals(other.engineType);
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 53 * hash + dataStore.hashCode();
        hash = 53 * hash + storageRef.hashCode();
        hash = 53 * hash + engineType.hashCode();
        return hash;
    }

    @Override
    public boolean isStatic()
    {
        return true;
    }

    @Nonnull
    @Override
    protected Reader openReader() throws IOException
    {
        final InputStream inputStream = DataStoreFunctions.openInputStream(dataStore, storageRef);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        return new BufferedReader(inputStreamReader);
    }

    @Override
    protected void setScriptSpecField(final DocumentWorkerScript script)
    {
        script.storageRef = storageRef;
    }
}
