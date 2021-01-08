#### Version Number
${version-number}

#### New Features
- [SCMOD-10362](https://portal.digitalsafe.net/browse/SCMOD-10362): GraalVM JavaScript support  
  The Document Worker Framework has been enhanced to support using the [GraalVM](https://www.graalvm.org/) JavaScript engine.  In previous versions of the framework Nashorn was the only JavaScript engine supported.  For backwards compatibility reasons it remains the default JavaScript engine, but note that it has been deprecated (see [JEP372](https://openjdk.java.net/jeps/372)), so it may not be supported by all future versions of Java.
- [SCMOD-11069](https://portal.digitalsafe.net/browse/SCMOD-11069): Update archetype to openSUSE Leap 15.2.
- [SCMOD-11144](https://portal.digitalsafe.net/browse/SCMOD-11144): Upgrade json-schema-core to 1.2.14  
  This fixes a vulnerability pointed out by [Fortify](https://ams.fortify.com/Releases/269904/Issues/109539445)

#### Bug Fixes
- [SCMOD-11362](https://portal.digitalsafe.net/browse/SCMOD-11362): Close script context  
  Fixes a performance degradation issue occurring when the worker is under heavy load
-  Corrected dependencies used by the unit testing framework  
   These are not intended for use with dependency management systems.

#### Known Issues
- None