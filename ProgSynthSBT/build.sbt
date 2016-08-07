organization := "default-a6c1da"

name := "ProgSynth"

version := "0.0.1"

scalaVersion := "2.10.2"

//Temporarily making it false for fast compilation
exportJars := false 

libraryDependencies <<= scalaVersion { scala_version => Seq(
  "org.scala-lang" % "scala-compiler" % "2.10.2",
  "org.scala-lang" % "scala-library" % "2.10.2",
  "org.hamcrest" % "hamcrest-all" % "1.1",
  "junit" % "junit" % "4.7",
  "org.scalatest" %% "scalatest" % "1.9.1"  % "test",
  "org.specs2" %% "specs2-scalaz-core" % "6.0.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.10.0",
  "org.easymock" % "easymock" % "3.1" % "test",
  "org.scalaz" %% "scalaz-core" % "6.0.4",
  "com.googlecode.kiama" %% "kiama" % "1.4.0",
  "com.twitter" %% "chill" % "0.3.6"
  )
}

//resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.7"

(test in Test) <<= (test in Test) dependsOn (Keys.`package` in Compile)

//(test-only in Test) <<= (test in Test) dependsOn (Keys.`package` in Compile) //TODO: Fix compile error.

fork in run := true

fork in Test := true

parallelExecution in Test := false

scalacOptions ++= Seq("-unchecked", "-deprecation")

//seq(ScctPlugin.instrumentSettings : _*)

net.virtualvoid.sbt.graph.Plugin.graphSettings

//For logger initialization during testing. Does not work.
//testOptions += Tests.Setup( cl =>
//   cl.loadClass("org.slf4j.LoggerFactory").
//     getMethod("getLogger",cl.loadClass("java.lang.String")).
//     invoke(null,"ROOT")
//)
//
//# To create eclipse .classpath file and .project file, run the eclipse command from the sbt prompt.
//# When you add additional jars, you might have to modify the some test classes like TreeExtractor
//# Export kiama in Eclipse project for ProgSynth (in order and export) to avoid "illegal cyclic inheritance" errors.
//
//kiama jars are added as unmanagedJars since TreeExtractor needs the path of the jar
//-------commented code...kept if needed in future ----------------------------------
//unmanagedJars in Compile += file("""extlib\scalaz3-3.2.b.jar""")
//unmanagedJars in Compile += file("""D:\ProgramFilesx86\Z3-3.2\bin\z3.dll""")
//"com.googlecode.kiama" %% "kiama" % "1.2.0",
//"com.googlecode.kiama" %% "kiama" % "1.2.0" % "test" classifier ("test")
//
//Moved the jars to lib directory.
//kept the extlib since it is required by TreeExtractor
//unmanagedJars in Compile += file("""extlib\scalaz3-3.2.b_scala.jar""")
//unmanagedJars in Compile += file("""extlib\scalaz3-3.2.b_java.jar""")
//unmanagedJars in Compile += file("""extlib\kiama_2.9.1-1.2.0.jar""")
//unmanagedJars in Compile += file("""extlib\kiama_2.9.1-1.2.0-test.jar""")

