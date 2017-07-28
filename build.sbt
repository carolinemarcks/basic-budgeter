name := "budget"

version := "1.0"

scalaVersion := "2.11.8"

organization := "com.cmarcksthespot"


lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin)

buildInfoKeys := Seq[BuildInfoKey](name, organization, version, scalaVersion, sbtVersion)
buildInfoPackage := "com.cmarcksthespot.budget"
