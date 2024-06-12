#### Version Number
${version-number}

#### New Features
- US914108: Version Currency: JUnit 5 migration
- US915147: Liveness and readiness check support has been added to the `DocumentWorker` interface.
  - The `DocumentWorker` interface contains a new `checkLiveness` method, which has a default implementation that does nothing. A worker 
    may optionally override this method to provide their own implementation of liveness.
  - See the Worker Framework [documentation](https://github.com/WorkerFramework/worker-framework/tree/develop/worker-core#health-checks-within-the-worker-framework)
    for more details.

#### Known Issues
- None
