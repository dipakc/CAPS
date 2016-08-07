//logLevel := Level.Debug
logLevel := Level.Debug

// SBT Eclipse Plugin
//addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.1.1")
//
// Resolver for Play SBT Plugin
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// SBT Play plugin
addSbtPlugin("play" % "sbt-plugin" % "2.1.1")

//added by dipakc for sxr
//resolvers += "Typesafe repository2" at "http://repo.typesafe.com/typesafe/list/scala-tools-releases/"

//https://github.com/harrah/browse sxr plugin. Not available for scala 2.10.2
//addCompilerPlugin("org.scala-tools.sxr" % "sxr" % "0.3.0-SNAPSHOT" cross CrossVersion.full)
