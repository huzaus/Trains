package com.shuzau.trains.domain.in

import TrainsModule.TrainsService
import com.shuzau.trains.domain.entity.EntityGen
import org.scalacheck.{Gen, Shrink}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import zio.Runtime.default
import zio.{ZEnv, ZIO}

class TrainsModuleSpec extends AnyFlatSpec with Matchers with ScalaCheckPropertyChecks with EitherValues {

  private implicit def noShrink[T]: Shrink[T] = Shrink.shrinkAny

  behavior of "TrainsModule"

  it should "return empty report for empty storage" in {
    unsafeRun(TrainsModule.report()).map shouldBe empty
  }

  it should "not register trip for empty storage" in {
    forAll(EntityGen.trip) { trip =>
      unsafeRun(TrainsModule.register(trip).either) shouldBe Symbol("left")
    }
  }

  it should "generate report with expected properties" in {
    forAll(for {
      version  <- EntityGen.version
      trains   <- Gen.nonEmptyListOf(EntityGen.train(version))
      stations <- Gen.nonEmptyListOf(EntityGen.station(version))
      trips    <- Gen.nonEmptyListOf(EntityGen.trip(version, Gen.oneOf(trains), Gen.nonEmptyListOf(Gen.oneOf(stations))))
    } yield (trains, stations, trips)) { case (trains, stations, trips) =>
      val scenario = for {
        _      <- ZIO.foreach_(trains)(TrainsModule.register)
        _      <- ZIO.foreach_(stations)(TrainsModule.register)
        _      <- ZIO.foreach_(trips)(TrainsModule.register)
        report <- TrainsModule.report()
      } yield report
      val report   = unsafeRun(scenario)
      report.map.keys.map(_.id) should contain theSameElementsAs trips.flatMap(_.stations.station).toSet
      report.map.values.flatten.map(_.id).toSet should contain theSameElementsAs trips.map(_.train).toSet
      report.sorted().values.map(_.map(_.seats).sum) shouldBe
        report.map.values.map(_.map(_.seats).sum).toList.sortWith(_ > _)
    }
  }

  def unsafeRun[T](scenario: ZIO[ZEnv with TrainsService, Any, T]): T =
    default.unsafeRun(scenario.provideCustomLayer(TrainsModule.layer))
}
