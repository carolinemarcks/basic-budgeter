name := "budget"

version := "1.0"

scalaVersion := "2.11.8"

organization := "com.cmarcksthespot"


lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin)

//build info plugin settings
buildInfoKeys := Seq[BuildInfoKey](name, organization, version, scalaVersion, sbtVersion)
buildInfoPackage := "com.cmarcksthespot.budget"

//sbt openapi settings
com.meetup.sbtopenapi.Plugin.openapiSettings
(basePackage in openapiConfig) := "com.cmarcksthespot"

//meetup-scala-server generated code deps
resolvers += Resolver.bintrayRepo("meetup", "maven")
libraryDependencies ++= Seq(
  "io.reactivex" %% "rxscala" % "0.26.0",
  "io.reactivex" % "rxnetty" % "0.4.16",
  "com.netflix.hystrix" % "hystrix-rx-netty-metrics-stream" % "1.4.23"
    exclude("io.reactivex", "rxnetty")
    exclude("com.netflix.archaius", "archaius-core"),
  "com.meetup" %% "scala-logger" % "0.2.25",
  "org.json4s" %% "json4s-native" % "3.4.0",
  "com.meetup" %% "json4s-java-time" % "0.0.9"
)

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
  "mysql" % "mysql-connector-java" % "5.1.27",
  "org.slf4j" % "slf4j-nop" % "1.7.10"
)