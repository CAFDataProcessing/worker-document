#### Version Number
${version-number}

#### New Features
- SCMOD-10362: GraalVM JavaScript support  
  The Document Worker Framework has been enhanced to support using the [GraalVM](https://www.graalvm.org/) JavaScript engine.  In previous versions of the framework Nashorn was the only JavaScript engine supported.  For backwards compatibility reasons it remains the default JavaScript engine, but note that it has been deprecated (see [JEP372](https://openjdk.java.net/jeps/372)), so it may not be supported by all future versions of Java.
- SCMOD-11069: Archetype updated to openSUSE Leap 15.2.
- SCMOD-11144: json-schema-core upgraded to 1.2.14  
  Resolves a security security vulnerability. 
- Worker-framework upgraded to 4.0.0  
  Configuration files can now be supplied as resources in addition to external files.  
  Lyra client replaced with RabbitMQ Java client.

#### Known Issues
- None
