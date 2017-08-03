!not-ready-for-release!

#### Version Number
${version-number}

#### New Features
- [CAF-3113](https://jira.autonomy.com/browse/CAF-3113):  AbstractTask implementations can now control how general failures are handled by the worker.
    A DocumentTask will record the failure on the document and return a RESULT_SUCCESS DocumentWorkerDocumentTask. A FieldEnrichmentTask retains the previous behaviour, returning a RESULT_EXCEPTION task with the failure details in the task data.

#### Known Issues
