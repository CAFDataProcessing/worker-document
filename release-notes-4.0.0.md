!not-ready-for-release!

#### Version Number
${version-number}

#### New Features
 - [SCMOD-3092](https://jira.autonomy.com/browse/SCMOD-3092): Scripting support  
    The Document Worker Framework has been enhanced to support using JavaScript to customize documents.  Multiple scripts can be supplied with a document task and the event handlers that they contain will be raised at the appropriate time.  See SCMOD-3092 for the complete list of the event handlers added.

    The customization scripts can also be used to stop documents in a compound document from being passed to the worker's `processDocument()` method.  By default each document in a compound document is passed to the `processDocument()` method individually, and the customization script support adds a degree of control to this behavior.  It can be used to cause only a subset of the constituent documents to be processed by the worker.

    Note that if you try to send a document task that includes customization scripts to a worker that was built using a previous version of the framework that it will be rejected.

 - New method for field value retrieval  
    A new `openInputStream` method has been added to the `FieldValue` interface to provide a consistent means to access the data in the field value, regardless of whether the data is available in place, or whether it needs to be retrieved from the remote data store.

#### Bug Fixes
 - [CAF-3255](https://jira.autonomy.com/browse/CAF-3255): Corrected batch document counting  
    Previously the `currentSize` method did not include subdocuments, even when the flag to process them separately was turned on.  Also subdocuments were not taken into account when checking the maximum batch size.

#### Known Issues


#### Breaking Changes
- [SCMOD-4072](https://jira.autonomy.com/browse/SCMOD-4072): Updated to use the latest released version of Dropwizard
   Previously this project consumed version 0.8.4 of dropwizard, this update has increased this version to 1.3.2.
