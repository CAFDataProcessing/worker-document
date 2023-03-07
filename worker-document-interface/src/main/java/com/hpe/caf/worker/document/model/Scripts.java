/*
 * Copyright 2016-2023 Open Text.
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

import java.util.stream.Stream;
import javax.annotation.Nonnull;

/**
 * Represents the list of customization scripts which are attached to a task.
 */
public interface Scripts extends DocumentWorkerObject, Iterable<Script>
{
    /**
     * Creates a new uninitialized customization script and appends it to the end of this list. The newly created script is not installed
     * and it is not loaded, so it can be adjusted without having to first unload it.
     *
     * @return the newly created script
     */
    @Nonnull
    Script add();

    /**
     * Creates a new uninitialized customization script and inserts it at the specified position in this list. The newly created script is
     * not installed and it is not loaded, so it can be adjusted without having to first unload it.
     *
     * @param index index at which the newly created script is to be inserted
     * @return the newly created script
     */
    @Nonnull
    Script add(int index);

    /**
     * Retrieves the script at the specified position in this list.
     *
     * @param index index of the script to return
     * @return the script at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= size())
     */
    @Nonnull
    Script get(int index);

    /**
     * Returns the task that this list of scripts is associated with.
     *
     * @return the task that this list of scripts is associated with
     */
    @Nonnull
    Task getTask();

    /**
     * Returns true if there are no scripts in this list.
     *
     * @return true if there are no scripts in this list
     */
    boolean isEmpty();

    /**
     * Returns the number of scripts in this list.
     *
     * @return the number of scripts in this list
     */
    int size();

    /**
     * Returns a sequential {@code Stream} with this list of scripts as its source.
     *
     * @return a sequential {@code Stream} over the list of scripts
     */
    @Nonnull
    Stream<Script> stream();

    /**
     * Uninstalls all of the scripts in this list.
     * <p>
     * This is equivalent to calling {@link Script#uninstall() uninstall()} for each of the scripts in the list.
     */
    void uninstallAll();
}
