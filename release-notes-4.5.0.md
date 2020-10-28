!not-ready-for-release!

#### Version Number
${version-number}

#### New Features
* [SCMOD-10362](https://portal.digitalsafe.net/browse/SCMOD-10362): GraalVM JavaScript support  
    The Document Worker Framework has been enhanced to support using the [GraalVM](https://www.graalvm.org/) JavaScript engine.  In previous versions of the framework Nashorn was the only JavaScript engine supported.  For backwards compatibility reasons it remains the default JavaScript engine, but note that it has been deprecated (see [JEP372](https://openjdk.java.net/jeps/372)), so it may not be supported by all future versions of Java.

#### Bug fixes
* SCMOD-11362: Closing script bindings and associated context to prevent memory leaks.

#### Known Issues
