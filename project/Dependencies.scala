import sbt._

object Dependencies {

  lazy val scalaTest          = "org.scalatest"     %% "scalatest"       % "3.2.9"   % Test
  lazy val scalaTestPlusCheck = "org.scalatestplus" %% "scalacheck-1-15" % "3.2.9.0" % Test

  lazy val cats   = "org.typelevel" %% "cats-core"   % "2.4.2"
  lazy val zio    = "dev.zio"       %% "zio"         % "1.0.7"
  lazy val phobos = "ru.tinkoff"    %% "phobos-core" % "0.10.1"

  lazy val testDependencies: Seq[ModuleID] = Seq(scalaTest, scalaTestPlusCheck)

  lazy val commonDependencies: Seq[ModuleID] = Seq(cats, zio, phobos)
}
