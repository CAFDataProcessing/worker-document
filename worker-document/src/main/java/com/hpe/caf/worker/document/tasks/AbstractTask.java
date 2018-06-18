/*
 * Copyright 2016-2018 Micro Focus or one of its affiliates.
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
package com.hpe.caf.worker.document.tasks;

import com.hpe.caf.api.worker.WorkerResponse;
import com.hpe.caf.api.worker.WorkerTaskData;
import com.hpe.caf.worker.document.DocumentWorkerScript;
import com.hpe.caf.worker.document.exceptions.DocumentWorkerTransientException;
import com.hpe.caf.worker.document.exceptions.InvalidScriptException;
import com.hpe.caf.worker.document.impl.ApplicationImpl;
import com.hpe.caf.worker.document.impl.DocumentImpl;
import com.hpe.caf.worker.document.impl.DocumentWorkerObjectImpl;
import com.hpe.caf.worker.document.impl.ResponseImpl;
import com.hpe.caf.worker.document.impl.ScriptsImpl;
import com.hpe.caf.worker.document.model.Document;
import com.hpe.caf.worker.document.model.Response;
import com.hpe.caf.worker.document.model.Task;
import com.hpe.caf.worker.document.scripting.events.CancelableDocumentEventObject;
import com.hpe.caf.worker.document.scripting.events.DocumentEventObject;
import com.hpe.caf.worker.document.scripting.events.ErrorEventObject;
import com.hpe.caf.worker.document.scripting.events.TaskEventObject;
import com.hpe.caf.worker.document.views.ReadOnlyDocument;
import java.net.SocketException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import javax.script.ScriptException;

public abstract class AbstractTask extends DocumentWorkerObjectImpl implements Task
{
    private final WorkerTaskData workerTask;
    protected final DocumentImpl document;
    protected final ResponseImpl response;
    private final Map<String, String> customData;
    protected final ScriptsImpl scripts;

    protected AbstractTask(
        final ApplicationImpl application,
        final WorkerTaskData workerTask,
        final ReadOnlyDocument effectiveDocument,
        final Map<String, String> customData
    )
    {
        super(application);
        this.workerTask = Objects.requireNonNull(workerTask);
        this.document = new DocumentImpl(application, this, effectiveDocument);
        this.response = new ResponseImpl(application, this);
        this.customData = customData;
        this.scripts = new ScriptsImpl(application, this);
    }

    protected AbstractTask(
        final ApplicationImpl application,
        final WorkerTaskData workerTask,
        final ReadOnlyDocument effectiveDocument,
        final Map<String, String> customData,
        final List<DocumentWorkerScript> scripts
    ) throws InvalidScriptException
    {
        super(application);
        this.workerTask = Objects.requireNonNull(workerTask);
        this.document = new DocumentImpl(application, this, effectiveDocument);
        this.response = new ResponseImpl(application, this);
        this.customData = customData;
        this.scripts = new ScriptsImpl(application, this, scripts);
    }

    @Nonnull
    @Override
    public final DocumentImpl getDocument()
    {
        return document;
    }

    @Override
    public final String getCustomData(final String dataKey)
    {
        if (customData == null) {
            return null;
        }

        return customData.get(dataKey);
    }

    @Nonnull
    @Override
    public final ScriptsImpl getScripts()
    {
        return scripts;
    }

    @Override
    public <S> S getService(final Class<S> service)
    {
        return (service == WorkerTaskData.class)
            ? (S) workerTask
            : null;
    }

    @Nonnull
    @Override
    public Response getResponse()
    {
        return this.response;
    }

    @Nonnull
    public final WorkerResponse createWorkerResponse()
    {
        return createWorkerResponseImpl();
    }

    @Nonnull
    protected abstract WorkerResponse createWorkerResponseImpl();

    /**
     * Returns appropriate WorkerResponse for the task in the event of a general task failure.
     *
     * @param failure detail of the failure that occurred
     * @return response with task appropriate failure details
     */
    @Nonnull
    public final WorkerResponse handleGeneralFailure(final Throwable failure)
    {
        return handleGeneralFailureImpl(failure);
    }

    @Nonnull
    protected abstract WorkerResponse handleGeneralFailureImpl(Throwable failure);

    /**
     * Load the customization scripts and if there is an exception then try to determine if it is a transient issue.
     *
     * @throws DocumentWorkerTransientException if one of the scripts could not be loaded due to a transient issue
     * @throws InterruptedException if any thread has interrupted the current thread
     */
    public void loadScripts() throws DocumentWorkerTransientException, InterruptedException
    {
        try {
            scripts.loadAll();
        } catch (final ScriptException ex) {
            final Throwable cause = ex.getCause();

            if (cause instanceof SocketException) {
                throw new DocumentWorkerTransientException(ex);
            } else if (cause instanceof DocumentWorkerTransientException) {
                throw (DocumentWorkerTransientException) cause;
            } else if (cause instanceof InterruptedException) {
                throw (InterruptedException) cause;
            } else {
                throw new RuntimeException(ex);
            }
        }
    }

    public void raiseProcessTaskEvent() throws DocumentWorkerTransientException, InterruptedException
    {
        raiseScriptEvents("onProcessTask", false, new TaskEventObject(this));
    }

    public boolean raiseBeforeProcessDocumentEvent(final Document document) throws DocumentWorkerTransientException, InterruptedException
    {
        final CancelableDocumentEventObject documentEventObj = new CancelableDocumentEventObject(document);

        raiseScriptEventsUntil("onBeforeProcessDocument", false, () -> documentEventObj.cancel, documentEventObj);

        return documentEventObj.cancel;
    }

    public void raiseProcessDocumentEvent(final Document document) throws DocumentWorkerTransientException, InterruptedException
    {
        raiseScriptEvents("onProcessDocument", false, new DocumentEventObject(document));
    }

    public void raiseAfterProcessDocumentEvent(final Document document) throws DocumentWorkerTransientException, InterruptedException
    {
        raiseScriptEvents("onAfterProcessDocument", true, new DocumentEventObject(document));
    }

    public void raiseAfterProcessTaskEvent() throws DocumentWorkerTransientException, InterruptedException
    {
        raiseScriptEvents("onAfterProcessTask", true, new TaskEventObject(this));
    }

    public boolean raiseOnErrorEvent(final RuntimeException ex) throws DocumentWorkerTransientException, InterruptedException
    {
        final ErrorEventObject errorEventObj = new ErrorEventObject(this, ex);

        raiseScriptEventsUntil("onError", true, () -> errorEventObj.handled, errorEventObj);

        return errorEventObj.handled;
    }

    private void raiseScriptEvents(final String event, final boolean useReverseOrder, final Object... args)
        throws DocumentWorkerTransientException, InterruptedException
    {
        unwrapCheckedExceptions(() -> {
            scripts.raiseEvent(event, useReverseOrder, args);
        });
    }

    private void raiseScriptEventsUntil(
        final String event,
        final boolean useReverseOrder,
        final BooleanSupplier condition,
        final Object... args
    ) throws DocumentWorkerTransientException, InterruptedException
    {
        unwrapCheckedExceptions(() -> {
            scripts.raiseEventUntil(event, useReverseOrder, condition, args);
        });
    }

    private static void unwrapCheckedExceptions(final Runnable action) throws DocumentWorkerTransientException, InterruptedException
    {
        try {
            action.run();
        } catch (final RuntimeException ex) {
            final Throwable cause = ex.getCause();

            if (cause instanceof InterruptedException) {
                throw (InterruptedException) cause;
            } else if (cause instanceof DocumentWorkerTransientException) {
                throw (DocumentWorkerTransientException) cause;
            } else {
                throw ex;
            }
        }
    }
}
