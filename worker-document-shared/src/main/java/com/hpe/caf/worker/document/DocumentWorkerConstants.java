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
package com.hpe.caf.worker.document;

/**
 * DocumentWorkerConstants constants including API version and the name of the worker.
 */
public final class DocumentWorkerConstants
{
    public static final String WORKER_NAME = "DocumentWorker";
    public static final int WORKER_API_VER = 1;

    public static final String DOCUMENT_TASK_NAME = "DocumentWorkerTask";
    public static final int DOCUMENT_TASK_API_VER = 1;

    /**
     * Custom data setting for the post processing JavaScript.
     * The setting value is expected to be a data store reference.
     */
    public static final String POST_PROCESSING_SCRIPT_CUSTOM_DATA = "postProcessingScript";

    private DocumentWorkerConstants()
    {
    }
}
