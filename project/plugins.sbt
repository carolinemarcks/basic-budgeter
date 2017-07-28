logLevel := Level.Warn

//open api spec codegen
libraryDependencies += "com.meetup" % "meetup-scala-generator" % "2.4.10"
addSbtPlugin("com.meetup" % "sbt-openapi" % "0.0.4")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")
