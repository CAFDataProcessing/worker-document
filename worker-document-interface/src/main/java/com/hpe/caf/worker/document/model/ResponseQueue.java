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
package com.hpe.caf.worker.document.model;

import jakarta.annotation.Nonnull;

/**
 * Used for managing the queue that response messages are published to.
 * <p>
 * The queue may or may not be enabled. If the queue is not enabled then response messages published to it will be discarded.
 */
public interface ResponseQueue extends DocumentWorkerObject
{
    /**
     * Disables the response queue. This causes response messages that would otherwise have been published to the queue to be discarded.
     */
    void disable();

    /**
     * Returns the name of the response queue. The {@link #isEnabled()} call should be used before calling this method as it is only
     * available if the queue is enabled. This method will throw a RuntimeException if the queue is not enabled.
     *
     * @return the name of the response queue
     * @throws RuntimeException if the response queue is disabled
     */
    @Nonnull
    String getName();

    /**
     * Returns the response customization object that this response queue object is associated with.
     *
     * @return the response customization object that this response queue object is associated with
     */
    @Nonnull
    Response getResponse();

    /**
     * Returns true if the response queue is enabled.
     *
     * @return true if the response queue is enabled
     */
    boolean isEnabled();

    /**
     * Resets the response queue back to its original state, undoing any changes made to it using the {@link #disable() disable()} or
     * {@link #set(String) set()} methods.
     */
    void reset();

    /**
     * Sets the response queue. If the specified response queue name is non-null then the queue will be enabled and the response queue
     * name will be set. If the specified name is null then the response queue will be disabled. In this case the method is equivalent to
     * calling the {@link #disable() disable()} method.
     *
     * @param name the name of the response queue, or {@code null} to disable the queue
     */
    void set(String name);
}
