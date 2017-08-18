
#### Version Number
${version-number}

#### New Features
- [CAF-2931](https://jira.autonomy.com/browse/CAF-2931):  Add functionality to Document workers to receive Tracking Info    
  The Document object now exposes the Task associated with it, which allows access to the WorkerTaskData object.
- [CAF-3113](https://jira.autonomy.com/browse/CAF-3113):  Changed handling of failures for composite document tasks.  
  A message marked as poison will now add a failure to the document and return a success response for composite document tasks.
- [CAF-3257](https://jira.autonomy.com/browse/CAF-3257):  Document Worker Framework: Document composite document format  
  The Document Worker contract documentation has been updated to include the new alternative contract format which supports subdocuments.

#### Known Issues
