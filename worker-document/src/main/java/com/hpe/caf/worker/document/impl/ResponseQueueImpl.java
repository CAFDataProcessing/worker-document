/*
 * Copyright 2015-2017 EntIT Software LLC, a Micro Focus company.
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
package com.hpe.caf.worker.document.impl;

import com.hpe.caf.worker.document.model.ResponseQueue;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class ResponseQueueImpl extends DocumentWorkerObjectImpl implements ResponseQueue
{
    private final ResponseImpl response;
    private final State originalState;
    private State currentState;

    public ResponseQueueImpl(final ApplicationImpl application, final ResponseImpl response, final String queueName)
    {
        super(application);
        this.response = response;
        this.originalState = createState(queueName);
        this.currentState = originalState;
    }

    @Override
    public void disable()
    {
        this.currentState = DisabledState.INSTANCE;
    }

    @Nonnull
    @Override
    public String getName()
    {
        return currentState.getName();
    }

    @Nonnull
    @Override
    public ResponseImpl getResponse()
    {
        return response;
    }

    @Override
    public boolean isEnabled()
    {
        return currentState.isEnabled();
    }

    @Override
    public void reset()
    {
        this.currentState = originalState;
    }

    @Override
    public void set(final String name)
    {
        this.currentState = createState(name);
    }

    public String getQueueName()
    {
        return currentState.getQueueName();
    }

    private static State createState(final String name)
    {
        return (name == null)
            ? DisabledState.INSTANCE
            : new EnabledState(name);
    }

    private interface State
    {
        @Nonnull
        String getName();

        String getQueueName();

        boolean isEnabled();
    }

    private enum DisabledState implements State
    {
        INSTANCE;

        @Nonnull
        @Override
        public String getName()
        {
            throw new RuntimeException("The response queue is not enabled.");
        }

        @Override
        public String getQueueName()
        {
            return null;
        }

        @Override
        public boolean isEnabled()
        {
            return false;
        }
    }

    private static final class EnabledState implements State
    {
        private final String name;

        public EnabledState(final String name)
        {
            this.name = Objects.requireNonNull(name);
        }

        @Nonnull
        @Override
        public String getName()
        {
            return name;
        }

        @Nonnull
        @Override
        public String getQueueName()
        {
            return name;
        }

        @Override
        public boolean isEnabled()
        {
            return true;
        }
    }
}
