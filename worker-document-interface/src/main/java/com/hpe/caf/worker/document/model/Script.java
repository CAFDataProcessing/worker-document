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
package com.hpe.caf.worker.document.model;

import java.io.IOException;
import java.net.URL;
import javax.annotation.Nonnull;
import javax.script.Bindings;
import javax.script.ScriptException;

/**
 * Represents a customization script which is attached to a task.
 * <p>
 * The customization script may or may not be loaded. If it is loaded then any event handlers that it contains will be executed when the
 * event occurs. The customization script can only be modified when it is not loaded.
 * <p>
 * The customization script may or may not be installed. If it is installed then it will be included in the response message. If the
 * response message is sent to another Document Worker then it will automatically be loaded by that worker before the task is processed.
 */
public interface Script extends DocumentWorkerObject
{   
    /**
     * Returns the current position of this customization script in the parent list.
     *
     * @return the current position of this customization script in the parent list
     */
    int getIndex();

    /**
     * Closes the bindings and context associated with this customization script. 
     */
    void closeBindings();

    /**
     * Returns the name of the customization script.
     * <p>
     * This can be used as an identifier for the script. It may appear in the log file, especially if there is an issue with the script.
     *
     * @return the name of the customization script
     */
    String getName();

    /**
     * This property allows for communication between scripts. When the customization script is loaded it may define a global variable
     * named "thisScriptObject". This property can be used to access the contents of that global variable, which could be an object of any
     * type, including a function. If the script is not loaded, or if the global variable is not set by the script, then {@code null} is
     * returned.
     *
     * @return an object that the script has elected to expose, or {@code null} if the script is not loaded or does not expose an object
     */
    Object getObject();

    /**
     * Retrieves the text of the script.
     * <p>
     * Note that there is no guarantee that the script code is cached. If the script is remote then calling this method multiple times may
     * result in it being fetched multiple times.
     *
     * @return the source code of the script
     * @throws IOException if the script is remote and cannot be retrieved
     */
    String getScript() throws IOException;

    /**
     * Returns the task that this customization script is associated with.
     *
     * @return the task that this customization script is associated with
     */
    @Nonnull
    Task getTask();

    /**
     * Installs this customization script. Calling this method has no effect if the script is already installed.
     * <p>
     * Installing this script means that it will be included in the response message.
     */
    void install();

    /**
     * Checks whether this customization script is currently installed.
     * <p>
     * If it is installed then it will be included in the response message, otherwise it will not be.
     *
     * @return true if this customization script is installed
     */
    boolean isInstalled();

    /**
     * Checks whether this customization script is currently loaded.
     * <p>
     * The customization script must be loaded in order for its event handlers to be fired. It can only be modified when it is not loaded.
     *
     * @return true if this customization script is loaded
     */
    boolean isLoaded();

    /**
     * Loads this customization script. Calling this method has no effect if the script is already loaded.
     *
     * @throws RuntimeException if the script has not been initialized
     * @throws ScriptException if the script cannot be retrieved or interpreted, or if it throws an error when loading
     */
    void load() throws ScriptException;

    /**
     * Sets the name of the customization script. This may be used as an identifier for the script. If there is an issue with the script
     * then it may appear in log files or in error messages.
     * <p>
     * This method will throw a RuntimeException if it is called when the script is currently loaded.
     *
     * @param name the name of the customization script
     * @throws RuntimeException if this customization script is currently loaded
     */
    void setName(String name);

    /**
     * Sets the customization script. This method may be used when the script has been stored in the remote data store using the UTF-8
     * encoding. It does not verify that the remote data store reference is valid. No attempt will be made to download the script from the
     * remote data store until the script is loaded.
     * <p>
     * This method will throw a RuntimeException if it is called when the script is currently loaded.
     *
     * @param reference the reference to the script in the remote data store
     * @throws RuntimeException if this customization script is currently loaded
     */
    void setScriptByReference(String reference);

    /**
     * Sets the customization script. This method may be used when the script has been stored in the remote data store using the UTF-8
     * encoding. It does not verify that the remote data store reference is valid. No attempt will be made to download the script from the
     * remote data store until the script is loaded.
     * <p>
     * This method will throw a RuntimeException if it is called when the script is currently loaded.
     *
     * @param reference the reference to the script in the remote data store
     * @param engineType the scripting engine to use
     * @throws RuntimeException if this customization script is currently loaded
     */
    void setScriptByReference(String reference, ScriptEngineType engineType);

    /**
     * Sets the customization script. This method may be used when the location of the script is identified by the specified URL. If the
     * script does not contain a recognized Unicode BOM then it is assumed to be UTF-8 encoded. No attempt will be made to download the
     * script until it is loaded.
     * <p>
     * This method will throw a RuntimeException if it is called when the script is currently loaded.
     *
     * @param url the reference to the location where the script can be retrieved from
     * @throws RuntimeException if this customization script is currently loaded, or if the specified URL is not strictly formatted in
     * accordance with RFC2396
     */
    void setScriptByUrl(URL url);

    /**
     * Sets the customization script. This method may be used when the location of the script is identified by the specified URL. If the
     * script does not contain a recognized Unicode BOM then it is assumed to be UTF-8 encoded. No attempt will be made to download the
     * script until it is loaded.
     * <p>
     * This method will throw a RuntimeException if it is called when the script is currently loaded.
     *
     * @param url the reference to the location where the script can be retrieved from
     * @param engineType the scripting engine to use
     * @throws RuntimeException if this customization script is currently loaded, or if the specified URL is not strictly formatted in
     * accordance with RFC2396
     */
    void setScriptByUrl(URL url, ScriptEngineType engineType);

    /**
     * Sets the customization script. This method may be used to directly specify a customization script so that it does not need to be
     * downloaded from an external location in order to be loaded.
     * <p>
     * This method will throw a RuntimeException if it is called when the script is currently loaded.
     *
     * @param script the customization script
     * @throws RuntimeException if this customization script is currently loaded
     */
    void setScriptInline(String script);

    /**
     * Sets the customization script. This method may be used to directly specify a customization script so that it does not need to be
     * downloaded from an external location in order to be loaded.
     * <p>
     * This method will throw a RuntimeException if it is called when the script is currently loaded.
     *
     * @param script the customization script
     * @param engineType the scripting engine to use
     * @throws RuntimeException if this customization script is currently loaded
     */
    void setScriptInline(String script, ScriptEngineType engineType);

    /**
     * Uninstalls this customization script. Calling this method has no effect if the script is already uninstalled.
     * <p>
     * Uninstalling this script means that it will not be included in the response message.
     */
    void uninstall();

    /**
     * Unloads this customization script. Calling this method has no effect if the script is already unloaded.
     * <p>
     * Unloading this script means that its event handlers will no longer be fired. It is required that the script is first unloaded in
     * order to make any modifications to it.
     */
    void unload();
}
