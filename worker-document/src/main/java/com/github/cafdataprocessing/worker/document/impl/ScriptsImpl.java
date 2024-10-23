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
package com.github.cafdataprocessing.worker.document.impl;

import com.github.cafdataprocessing.worker.document.DocumentWorkerScript;
import com.github.cafdataprocessing.worker.document.exceptions.InvalidScriptException;
import com.github.cafdataprocessing.worker.document.tasks.AbstractTask;
import com.github.cafdataprocessing.worker.document.model.Script;
import com.github.cafdataprocessing.worker.document.model.Scripts;
import com.github.cafdataprocessing.worker.document.model.Task;
import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.script.ScriptException;

public final class ScriptsImpl extends DocumentWorkerObjectImpl implements Scripts
{
    private final AbstractTask task;

    private final ArrayList<ScriptImpl> scripts;

    public ScriptsImpl(
        final ApplicationImpl application,
        final AbstractTask task
    )
    {
        super(application);
        this.task = Objects.requireNonNull(task);
        this.scripts = createEmptyScriptList();
    }

    public ScriptsImpl(
        final ApplicationImpl application,
        final AbstractTask task,
        final List<DocumentWorkerScript> scripts
    ) throws InvalidScriptException
    {
        super(application);
        this.task = Objects.requireNonNull(task);
        this.scripts = createScriptList(application, task, scripts);
    }

    @Nonnull
    @Override
    public Script add()
    {
        final ScriptImpl newScript = new ScriptImpl(application, task);
        scripts.add(newScript);

        return newScript;
    }

    @Nonnull
    @Override
    public Script add(final int index)
    {
        final ScriptImpl newScript = new ScriptImpl(application, task);
        scripts.add(index, newScript);

        return newScript;
    }

    @Nonnull
    @Override
    public Script get(final int index)
    {
        return scripts.get(index);
    }

    @Nonnull
    @Override
    public Task getTask()
    {
        return task;
    }

    @Override
    public boolean isEmpty()
    {
        return scripts.isEmpty();
    }

    @Override
    public int size()
    {
        return scripts.size();
    }

    @Nonnull
    @Override
    public Stream<Script> stream()
    {
        return scripts.stream().map(script -> script);
    }

    @Nonnull
    public Stream<ScriptImpl> streamImpls()
    {
        return scripts.stream();
    }

    @Nonnull
    @Override
    public Iterator<Script> iterator()
    {
        return stream().iterator();
    }

    @Override
    public void uninstallAll()
    {
        scripts.forEach(Script::uninstall);
    }

    /**
     * A script's position in the list could change if new scripts are inserted before it. This function takes the script's last known
     * list position and finds its current position. There is no facility for the script object to be removed from the list or to move to
     * an earlier position in the list so it should always be found, and we can start the search from the last known position.
     *
     * @param previousIndex the index to start searching from; use 0 if it is not known
     * @param script the script to look for, which must be in the list of scripts
     * @return the current index of the script
     */
    public int getCurrentScriptIndex(final int previousIndex, final ScriptImpl script)
    {
        final int numberOfScripts = scripts.size();

        for (int i = previousIndex; i < numberOfScripts; i++) {
            if (scripts.get(i) == script) {
                return i;
            }
        }

        throw new RuntimeException("Logical error: the script has disappeared from the list");
    }

    public void loadAll() throws ScriptException
    {
        // Take a copy of the current list of scripts in case more are added whilst loading
        final ArrayList<Script> currentScripts = new ArrayList<>(scripts);

        // Load the current list of scripts
        for (final Script script : currentScripts) {
            script.load();
        }
    }

    public void unloadAll()
    {
        scripts.forEach(Script::unload);
    }

    /**
     * Raises the specified event in all loaded scripts.
     *
     * @param event the event to be raised
     * @param useReverseOrder controls the order in which the loaded scripts are called
     * @param args the arguments to be passed to the specified event
     */
    public void raiseEvent(final String event, final boolean useReverseOrder, final Object... args)
    {
        raiseEventUntil(event, useReverseOrder, () -> false, args);
    }

    /**
     * Raises the specified event in all of the loaded scripts, or until the specified condition is met.
     *
     * @param event the event to be raised
     * @param useReverseOrder controls the order in which the loaded scripts are called
     * @param condition a condition which causes the event to stop being raised in the remaining scripts
     * @param args the arguments to be passed to the specified event
     */
    public void raiseEventUntil(final String event, final boolean useReverseOrder, final BooleanSupplier condition, final Object... args)
    {
        final Consumer<ScriptImpl> raiseEventAction = script -> {
            script.raiseEvent(event, args);
        };

        if (useReverseOrder) {
            reverseForEachScriptUntil(raiseEventAction, condition);
        } else {
            forEachScriptUntil(raiseEventAction, condition);
        }
    }

    @Nonnull
    private static ArrayList<ScriptImpl> createEmptyScriptList()
    {
        return new ArrayList<>(0);
    }

    @Nonnull
    private static ArrayList<ScriptImpl> createScriptList(
        final ApplicationImpl application,
        final AbstractTask task,
        final List<DocumentWorkerScript> scripts
    ) throws InvalidScriptException
    {
        if (scripts == null) {
            return new ArrayList<>(0);
        }

        final ArrayList<ScriptImpl> scriptsOut = new ArrayList<>(scripts.size());

        for (final DocumentWorkerScript script : scripts) {
            if (script != null) {
                scriptsOut.add(new ScriptImpl(application, task, script));
            }
        }

        return scriptsOut;
    }

    /**
     * Performs the specified action for each script in the list without throwing an exception if the specified action causes new scripts
     * to be added to the list. If new scripts are added to the list during the iteration then the action will be performed on them if
     * they are added beyond the cursor position and will not be performed on them if they are added before the cursor position.
     *
     * @param action the action to be performed for each script
     * @param condition a condition which is checked after the action is performed and if true causes the iteration to end immediately
     */
    private void forEachScriptUntil(final Consumer<ScriptImpl> action, final BooleanSupplier condition)
    {
        int i = 0;

        while (i < scripts.size()) {
            // Get the script
            final ScriptImpl script = scripts.get(i);

            // Perform the action
            action.accept(script);

            // Check the condition
            if (condition.getAsBoolean()) {
                return;
            }

            // Check the script's current index in case it was changed by the script code
            i = getCurrentScriptIndex(i, script) + 1;
        }
    }

    /**
     * This is the same as the {@link #forEachScriptUntil} method except that the iteration happens in reverse, i.e. from the end of the
     * list to the start of the list.
     */
    private void reverseForEachScriptUntil(final Consumer<ScriptImpl> action, final BooleanSupplier condition)
    {
        int i = scripts.size() - 1;

        while (i >= 0) {
            // Get the script
            final ScriptImpl script = scripts.get(i);

            // Perform the action
            action.accept(script);

            // Check the condition
            if (condition.getAsBoolean()) {
                return;
            }

            // Check the script's current index in case it was changed by the script code
            i = getCurrentScriptIndex(i, script) - 1;
        }
    }
}
