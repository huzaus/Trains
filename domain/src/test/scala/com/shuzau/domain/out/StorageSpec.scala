package com.shuzau.domain.out

import com.shuzau.domain.entity.EntityGen
import com.shuzau.domain.out.Storage.StorageService
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import zio.Runtime.default
import zio.{ZEnv, ZIO}

class StorageSpec extends AnyFlatSpec with Matchers with ScalaCheckPropertyChecks {

  behavior of "Storage"

  it should "return 0 trains for empty storage" in {
    unsafeRun(Storage.trains()) shouldBe empty
  }

  it should "return 0 stations for empty storage" in {
    unsafeRun(Storage.stations()) shouldBe empty
  }

  it should "return 0 trips for empty storage" in {
    unsafeRun(Storage.trips()) shouldBe empty
  }

  it should "return save and get a train" in {
    forAll(EntityGen.train) { train =>
      val scenario = for {
        _      <- Storage.save(train)
        result <- Storage.trains()
      } yield result
      unsafeRun(scenario) shouldBe Vector(train)
    }
  }

  it should "return save and get a station" in {
    forAll(EntityGen.station) { station =>
      val scenario = for {
        _      <- Storage.save(station)
        result <- Storage.stations()
      } yield result
      unsafeRun(scenario) shouldBe Vector(station)
    }
  }

  it should "return save and get a trip" in {
    forAll(EntityGen.trip) { trip =>
      val scenario = for {
        _      <- Storage.save(trip)
        result <- Storage.trips()
      } yield result
      unsafeRun(scenario) shouldBe Vector(trip)
    }
  }

  it should "return save and get trains" in {
    forAll(EntityGen.trains) { trains =>
      val scenario = for {
        _      <- ZIO.foreach_(trains)(Storage.save)
        result <- Storage.trains()
      } yield result
      unsafeRun(scenario) shouldBe trains
    }
  }

  it should "return save and get stations" in {
    forAll(EntityGen.stations) { stations =>
      val scenario = for {
        _      <- ZIO.foreach_(stations)(Storage.save)
        result <- Storage.stations()
      } yield result
      unsafeRun(scenario) shouldBe stations
    }
  }

  it should "return save and get trips" in {
    forAll(EntityGen.trips) { trips =>
      val scenario = for {
        _      <- ZIO.foreach_(trips)(Storage.save)
        result <- Storage.trips()
      } yield result
      unsafeRun(scenario) shouldBe trips
    }
  }

  def unsafeRun[T](scenario: ZIO[ZEnv with StorageService, Nothing, T]): T =
    default.unsafeRun(scenario.provideCustomLayer(Storage.layer))
}
