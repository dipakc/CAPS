import sbt._
import Keys._
import play.Project._


// Add the dependencies/settings in build.sbt.
// The dependencies/settings here will be ignored by the parent project in case of multiproject setup

// Generating Eclipse Project Files (.classpath and .project) :
// First comment the dependency("depends" as well as "aggregate") on ProgSynth and then run "eclipse"
// Manually add "ProgSynth" project dependency in eclipse. (Ensure that "ProgSynth" project exports the kiama library as well)


object ApplicationBuild extends Build {

	lazy val zProgSynthPrj = uri("../ProgSynthSBT")

	// Replicate these settings in build.sbt
	// The name and version here will be ignored by the parent project in case of multiproject setup
    val appName         = "ProgSynthWebApp"
    val appVersion      = "0.1-SNAPSHOT"

    val appDependencies = Seq(
    	//"org.scalatest" %% "scalatest" % "1.8" % "test"
    )

    // Before creating the .classpath file. read this: docs/ProgSynthWebAppDoc.html/#classpath
    lazy val progSynthWebAppPrj =
		play.Project(appName, appVersion, appDependencies, path = file("."))
		.dependsOn(zProgSynthPrj)
		.aggregate(zProgSynthPrj)
		.settings(
			testOptions in Test := Nil //Required for running scalatest (play default if specs2)
		)
}
