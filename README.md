## Document Worker
A Document Worker can be used to alter the field names and values contained within the metadata of a document supplied to it. Each instance of a Document Worker will be required to be configured when implementing it to give instructions as when to change document metadata and what to change it to. An example use case for a Document Worker is given below, followed by a brief overview of the modules that make up this repository.

### Example use case for a Document Worker
A Document Worker can be completely configured for each individual instance running.  This means that multiple Document Workers could be operating and performing different functions simultaneously. In the case of this example let us assume that a case has arisen that required a lookup to check the `metadata` fields on documents for a specific value, field or encoding type. Once found the worker would then evaluate its response based on the logic set forth within its implementation. For this example we will assume that it was looking for a field called `JOB_TITLE` that contains a value of `C-Level-Exec`. Once our example worker looks through the meta data of the document and has found this field and value, it will then implement the custom logic that was set out during its implementation, which for this case will be that if `JOB_TITLE` is equal to `C-Level-Exec` add a new field to the metadata called `IGNORE_FILE` and set the value of this field to `true`. The Document Worker will then pass back a result object to the Document Worker converter. The `DocumentWorkerResult` object will then contain a map of field names to Lists of `DocumentWorkerFieldValue` objects. Each `DocumentWorkerFieldValue` object will contain a value and an encoding. The Document Worker has two actions that can be used to achieve three goals. The first goal is `add`, this action can be used to add values onto a current field as well as to add a new field to a document's metadata. The second is `replace`, and this action can be used to achieve two goals: to replace a current field or data values with information specified by the worker, or to remove metadata from a document. This is achieved by the Document Worker passing back a field name with an action of `replace` and a data value of `null`. The converter will then recognise that replacing a field with a value of `null` is a signal to delete the field and do so. The new metadata fields or values added to a document can then be used in other workers to enable decision making. With regards to this example the new field added to the document `IGNORE_FILE` can be used in other workers to make the decision to ignore documents linked to a person with a `JOB_TITLE` of `C-Level-Exec`.

Below is an illustration showing the communication between a document-worker and `Policy` using `RabbitMQ`:

![Overview](images/Document-Worker.png)

## Modules

### worker-document-utility
This repository defines the public classes that facilitate the creation of mutable and read only document objects. The project can be found in [worker-document-utility](worker-document-utility).

### worker-document

This repository defines public base classes which facilitate the creation of projects generated from the [worker-document-archetype](worker-document-archetype). The project can be found in [worker-document](worker-document).

### worker-document-archetype

This repository is a Maven Archetype template for the generation of a new generic CAF Document Worker project. Generation of a worker project with the Maven Archetype will produce a generic Document Worker with minimal functionality. The project can be found in [worker-document-archetype](worker-document-archetype).

### worker-document-framework

This project is used for centralizing dependency information for a Document Worker. The project can be found in [worker-document-framework](worker-document-framework).

### worker-document-interface

This library defines public interfaces to assist with the implementation of a Document Worker. The project can be found in [worker-document-interface](worker-document-interface).

### worker-document-shared

This is the shared library defining public classes that constitute the worker interface to be used by consumers of the Document Worker. The project can be found in [worker-document-shared](worker-document-shared).

### worker-document-testing

This contains implementations of the testing framework to allow for integration testing of the Document Worker. The project can be found in [worker-document-testing](worker-document-testing).

## Workflow event handlers

The below are the various event handlers from the workflow scripts.

#### [onProcessTask](#on-process-task)

This is the first function called by worker on the task message.
This function is passed `TaskEventObject` with `Task` as an argument.

An example of the `TaskEventObject` is below. The variables will be initialized with values from task message and it will be sent to the series of other functions in the workflow for further processing.
```
TaskEventObject(Task task){
    application=task.application;
    task = task;
    rootDocument=task.document;
}
```
For more details of the `TaskEventObject` refer the java implementation of the class from package [TaskEventObject.java](https://github.com/CAFDataProcessing/worker-document/blob/develop/worker-document/src/main/java/com/hpe/caf/worker/document/scripting/events/TaskEventObject.java) 


```
/* global thisScript */

function onProcessTask(e)
{	
    // e.application  (read-only)
    // e.task         (read-only)
    // e.rootDocument (read-only)
}
```

#### onBeforeProcessDocument

This event will be executed after `onProcessTask` and before processing of a document. 

This function is passed `CancelableDocumentEventObject`  with `Document` as an argument.

An example of the `CancelableDocumentEventObject` is below.
```
CancelableDocumentEventObject(Document document)
{
    super(document);
    cancel=false;
}
```
For more details of the event object refer the Java implementation of for the class from package 
[CancelableDocumentEventObject.java](https://github.com/CAFDataProcessing/worker-document/blob/develop/worker-document/src/main/java/com/hpe/caf/worker/document/scripting/events/CancelableDocumentEventObject.java) 

```
function onBeforeProcessDocument(e)
{
    // e.application  (read-only)
    // e.task         (read-only)
    // e.rootDocument (read-only)
    // e.document     (read-only)
    // e.cancel       (writable)  (default: false)
}
```
Output of this function would be the boolean value of event's cancel flag.
This flag is used to determine if that individual document should be processed by the worker.

#### onProcessDocument

This function will be called to process a document. 
This function is passed `DocumentEventObject` with `Document` as an argument.
The `onBeforeProcessDocument`  event has already been triggered to check for cancellation flag.

An example of the `DocumentEventObject` is below.
```
DocumentEventObject(Document document){
    document = document;
}
```
For more details  of the event object refer to the java implementation for the class [DocumentEventObject](https://github.com/CAFDataProcessing/worker-document/blob/develop/worker-document/src/main/java/com/hpe/caf/worker/document/scripting/events/DocumentEventObject.java)
```
function onProcessDocument(e)
{
    // e.application  (read-only)
    // e.task         (read-only)
    // e.rootDocument (read-only)
    // e.document     (read-only)
}
```
This function calls the customization scripts, and if none of them have set the cancellation flag then calls the implementation's `DocumentWorker#processDocument processDocument()` function. The response of this function will be the processed document that should be add to the batch of documents.

#### onAfterProcessDocument

This function will be called once the processing of the document completed successfully.
This function is passed `DocumentEventObject` with `Document` as an argument.

An example of the `DocumentEventObject` is explained in `onProcessDocument` section.

For more details  of the event object,  refer the [onProcessDocument](#on-process-document) section. 

```
function onAfterProcessDocument(e)
{
    // e.application  (read-only)
    // e.task         (read-only)
    // e.rootDocument (read-only)
    // e.document     (read-only)
}
```
#### onAfterProcessTask

This is the last function called by worker on the task message.
This function is passed `TaskEventObject` with `Task` as an argument.

An example of `TaskEventObject` explained in `onProcessTask` section. 

The variables will be initialized with values from task message and it will be sent to the series of other functions in the workflow for further processing.

For more details of the TaskEventObject, refer the [onProcessTask](#on-process-task)

```
function onAfterProcessTask(e)
{
    // e.application  (read-only)
    // e.task         (read-only)
    // e.rootDocument (read-only)
    // onProcessDocument(e.rootDocument)
}
```
#### onError

This function should be called in case of a failure in the worker that is not handled by the worker code. In chained workers, this will allow continuing to process the document.
This function is passed `ErrorEventObject` with `Task`and `RuntimeException` as an argument.

```
ErrorEventObject(Task task, RuntimeException error){
    super(task);
    error = error;
    handled = false;
}
```
For more details of the ErrorEventObject refer the java implementation of the class for package `com.hpe.caf.worker.document.scripting.events.ErrorEventObject`.

```
function onError(errorEvent)
{
     // errorEvent.application  (read-only)
     // errorEvent.task         (read-only)
     // errorEvent.rootDocument (read-only)
     // errorEvent.handled      (writable)  (default: false)
}
```
Output of the function would be the boolean flag whether the failure was handled or leave it to workflow worker to handle.

