
resolvers += Classpaths.typesafeResolver

resolvers += "scct-github-repository" at "http://mtkopone.github.com/scct/maven-repo"
//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

//Required for scalatest 2.0.M5
resolvers += "SonaType" at "https://oss.sonatype.org/content/groups/public"

//addSbtPlugin("reaktor" % "sbt-scct" % "0.2-SNAPSHOT")
//addSbtPlugin("com.sqality.scct" % "sbt-scct" % "0.3-SNAPSHOT")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.2.0")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.3")