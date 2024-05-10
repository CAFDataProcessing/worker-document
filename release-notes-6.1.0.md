!not-ready-for-release!

#### Version Number
${version-number}

#### New Features
- None

#### Breaking Changes
- US915147: Liveness and readiness check support has been added to the `DocumentWorker` interface.
  - The `healthCheck` method has been removed from the `DocumentWorker` interface, and replaced by new `checkAlive` and `checkReady`methods.
  - See the [Worker Framework documentation](https://github.com/WorkerFramework/worker-framework/tree/develop/worker-core#liveness-and-readiness-checks-within-the-worker-framework)
    for more details.

#### Known Issues
- None
