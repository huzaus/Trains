package com.shuzau.domain.in

import com.shuzau.domain.entity.EntityGen
import com.shuzau.domain.in.TrainsModule.TrainsService
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import zio.Runtime.default
import zio.{ZEnv, ZIO}

class TrainsModuleSpec extends AnyFlatSpec with Matchers with ScalaCheckPropertyChecks with EitherValues {

  behavior of "TrainsModule"

  it should "return empty report for empty storage" in {
    unsafeRun(TrainsModule.report()) shouldBe empty
  }

  it should "not register trip for empty storage" in {
    forAll(EntityGen.trip) { trip =>
      unsafeRun(TrainsModule.register(trip).either) shouldBe Symbol("left")
    }
  }

  it should "register trip with known train and stations" in {
    forAll(for {
      version  <- EntityGen.version
      train    <- EntityGen.train(version)
      stations <- EntityGen.stations(version)
      trip     <- EntityGen.trip(version, train, stations)
    } yield (train, stations, trip)) { case (train, stations, trip) =>
      val scenario = for {
        _ <- TrainsModule.register(train)
        _ <- ZIO.foreach_(stations)(TrainsModule.register)
        _ <- TrainsModule.register(trip)
      } yield ()
      unsafeRun(scenario.either) shouldBe Symbol("right")
    }
  }
  def unsafeRun[T](scenario: ZIO[ZEnv with TrainsService, Nothing, T]): T =
    default.unsafeRun(scenario.provideCustomLayer(TrainsModule.layer))
}
