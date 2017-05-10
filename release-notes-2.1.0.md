#### Version Number
${version-number}

#### New Features
- [CAF-2701](https://jira.autonomy.com/browse/CAF-2701): Worker archetype container updated with a default JavaScript configuration file. If `CAF_APPNAME` and `CAF_CONFIG_PATH` are not set at runtime then these will be used. Allows for configuration of worker using environment variables alone.
- [CAF-2709](https://jira.autonomy.com/browse/CAF-2709): Extend Document Worker Archetype for Ease of Deployment. The archetype will now produce a `deployment` folder which contains a pre-generated compose overlay for a new worker.
