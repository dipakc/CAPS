* XSBT Version: 0.11.2
* sbteclipse:
* Eclipse:

"extlib\scala-compiler-2.9.1.jar" and "extlib\scala-library-2.9.1.jar" are not used in the project.
These libraries are there just for backup purpose.
The project uses the libraries fetched by sbt.

"extlib\scalaz3-3.2.b.jar" is split into following two jars
"scalaz3-3.2.b_java.jar" : classes built from java source(JNI). This should be loaded by the system or bootloader.
"extlib\scalaz3-3.2.b_scala.jar" : classes built from scala source.

"D:\ProgramFilesx86\Z3-3.2\bin\" : should be added to the system PATH

Run ./scripts/pssbt.bat for running sbt.

