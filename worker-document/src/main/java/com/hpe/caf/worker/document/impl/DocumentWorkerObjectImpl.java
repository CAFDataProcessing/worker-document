package com.hpe.caf.worker.document.impl;

import com.hpe.caf.worker.document.model.Application;
import com.hpe.caf.worker.document.model.DocumentWorkerObject;

public class DocumentWorkerObjectImpl implements DocumentWorkerObject
{
    protected final ApplicationImpl application;

    public DocumentWorkerObjectImpl(final ApplicationImpl application)
    {
        this.application = application;
    }

    @Override
    public Application getApplication()
    {
        return application;
    }
}
