name := "ProgSynthWebApp"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.2"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "6.0.4"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.1" % "test"

libraryDependencies += "com.twitter" %% "chill" % "0.3.6"

libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.5.3"

libraryDependencies += "com.typesafe.slick" %% "slick"       % "2.1.0"

libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.7.2"

libraryDependencies += "org.mindrot" % "jbcrypt" % "0.3m"


//sxr scala to html plugin config
//scalacOptions <+= scalaSource in Compile map { "-P:sxr:base-directory:" + _.getAbsolutePath }


// IMP: To create eclipse settings, first comment the dependency on ProgSynth in project/Build.scala
// and then run eclipse.
// docs/ProgSynthWebAppDoc.html/#classpath
//
//For some reason the following is not working..
//libraryDependencies <<= scalaVersion { scala_version => Seq(
//  "org.scalaz" %% "scalaz-core" % "6.0.4"
//  )
//}
//Error:
//java.lang.ClassNotFoundException: play.core.server.NettyServer
//-------------------------------
//comments
//libraryDependencies += "in.ac.iitb" %% "helloworld" % "0.1-SNAPSHOT" changing()