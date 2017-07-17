#### Version Number
${version-number}

#### New Features
 - [CAF-3253](https://jira.autonomy.com/browse/CAF-3253): Compound document support  
    The Document Worker Framework has been enhanced to be able to manipulate a document's reference and subdocuments.  Previously it could only manipulate the fields of a document.  A new change-log message format has been introduced which is used to send and receive compound documents.

    When an existing Document Worker is upgraded to this version of the framework then it will automatically accept compound documents.  By default each of the documents in the compound document will be passed to the worker's `processDocument()` method individually.  This behavior is controllable through configuration.

 - [CAF-2890](https://jira.autonomy.com/browse/CAF-2890): Automatic suspension when unhealthy  
    The Worker Framework now stops taking in more work if any of its health checks begin to fail.  It will not begin to accept new work until all its health checks indicate that it has recovered.  This is to prevent trying to execute operations which are likely to fail and to minimize the requirement for later reprocessing.

 - [CAF-2890](https://jira.autonomy.com/browse/CAF-2890): Automatic suspension on transient failures  
    Transient failures (as indicated by the worker throwing [DocumentWorkerTransientException](https://cafdataprocessing.github.io/data-processing-service/pages/en-us/Creating_an_Action/apidocs/com/hpe/caf/worker/document/exceptions/DocumentWorkerTransientException.html) exceptions) are now treated as an indication that the worker is unhealthy.  When they occur processing is temporarily suspended to allow the failing downstream service time to recover, and to ensure that there is not an attempt to process more documents whilst there is an issue that is causing transient failures.

 - [CAF-2952](https://jira.autonomy.com/browse/CAF-2952): Priority queue support  
    The Worker Framework now supports using priority queues with RabbitMQ.  When this feature is turned on the priority of a document being processed is gradually increased as it is passed through the workers (up to a configurable maximum).  This produces a more even workload across the workers and allows documents to exit the pipeline more quickly.

 - [CAF-2942](https://jira.autonomy.com/browse/CAF-2942): Simplified log level configuration  
    By default the `CAF_LOG_LEVEL` environment variable can now be used to specify the logging level.  Previously a separate logging configuration file had to be provided in order to adjust the logging level.

#### Breaking Changes
 - The CAF Storage module (i.e. `worker-store-cs`) is no longer supported.

 - The Filesystem Storage module (i.e. `worker-store-fs`) replaces the CAF Storage module as the default that is included via the `standard-worker-container` module.

#### Known Issues
 - None
