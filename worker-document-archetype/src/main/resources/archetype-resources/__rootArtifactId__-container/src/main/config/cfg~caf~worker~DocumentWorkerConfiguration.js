/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development LP.
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
 #set( $symbol_dollar = '$' )
({
    workerName: "${workerName}",
    workerVersion: "${symbol_dollar}{project.version}",
    outputQueue: getenv("CAF_WORKER_OUTPUT_QUEUE")
            || (getenv("CAF_WORKER_BASE_QUEUE_NAME") || getenv("CAF_WORKER_NAME") || "worker") + "-out",
    failureQueue: getenv("CAF_WORKER_FAILURE_QUEUE") || undefined,
    threads: getenv("CAF_WORKER_THREADS") || 1,
    maxBatchSize: getenv("CAF_WORKER_MAX_BATCH_SIZE") || undefined,
    maxBatchTime: getenv("CAF_WORKER_MAX_BATCH_TIME") || undefined
});