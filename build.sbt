name := "Trains"

ThisBuild / scalaVersion := "2.13.5"
ThisBuild / version := "0.0.1"
ThisBuild / organization := "com.shuzau.trains"
ThisBuild / organizationName := "Siarhei Huzau"


lazy val `trains` = (project in file("."))
  .settings(Compile / discoveredMainClasses ++= (`app` / Compile / discoveredMainClasses).value)
  .dependsOn(`app`)
  .aggregate(`domain`, `app`)

lazy val `domain` = project in file("domain")

lazy val `app` = (project in file("app"))
  .settings()
  .dependsOn(`domain` % "compile->compile;test->test")
