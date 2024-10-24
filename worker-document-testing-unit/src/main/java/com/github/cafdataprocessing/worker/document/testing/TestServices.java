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
package com.github.cafdataprocessing.worker.document.testing;

import com.github.cafapi.common.api.Codec;
import com.github.cafapi.common.codecs.jsonlzf.JsonCodec;
import com.github.workerframework.worker.api.DataStore;
import com.github.workerframework.worker.datastores.mem.InMemoryDataStore;
import java.util.Objects;

/**
 * Holds instances of common services used by workers during testing.
 */
public class TestServices
{
    private final DataStore dataStore;
    private final CodeConfigurationSource configurationSource;
    private final Codec codec;

    /**
     * Instantiates a new TestServices object.
     *
     * @param dataStore A DataStore instance.
     * @param configurationSource A ConfigurationSource instance.
     * @param codec A Codec instance.
     */
    public TestServices(final DataStore dataStore, final CodeConfigurationSource configurationSource, final Codec codec)
    {
        this.dataStore = Objects.requireNonNull(dataStore);
        this.configurationSource = Objects.requireNonNull(configurationSource);
        this.codec = Objects.requireNonNull(codec);
    }

    /**
     * Creates class using default implementations of services:
     * <ul>
     * <li>In-Memory Data Store ({@link InMemoryDataStore})</li>
     * <li>Json Codec ({@link JsonCodec}) </li>
     * <li>Code Configuration Source ({@link CodeConfigurationSource})</li>
     * </ul>
     *
     * @return a new TestServices object which uses the default implementations
     */
    public static TestServices createDefault()
    {
        return new TestServices(new InMemoryDataStore(),
                                new CodeConfigurationSource(DocumentWorkerConfigurationBuilder.configure().withDefaults().build()),
                                new JsonCodec());
    }

    /**
     * Gets a data store.
     *
     * @return Data store
     */
    public DataStore getDataStore()
    {
        return dataStore;
    }

    /**
     * Gets a configuration source
     *
     * @return Configuration source
     */
    public CodeConfigurationSource getConfigurationSource()
    {
        return configurationSource;
    }

    /**
     * Gets a codec
     *
     * @return Codec
     */
    public Codec getCodec()
    {
        return codec;
    }
}
