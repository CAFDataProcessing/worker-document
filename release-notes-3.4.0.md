!not-ready-for-release!

#### Version Number
${version-number}

#### New Features
 - New method for field value retrieval  
    A new `openInputStream` method has been added to the `FieldValue` interface to provide a consistent means to access the data in the field value, regardless of whether the data is available in place, or whether it needs to be retrieved from the remote data store.

#### Bug Fixes
 - [CAF-3255](https://jira.autonomy.com/browse/CAF-3255): Corrected batch document counting  
    Previously the `currentSize` method did not include subdocuments, even when the flag to process them separately was turned on.  Also subdocuments were not taken into account when checking the maximum batch size.

#### Known Issues
