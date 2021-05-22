package com.shuzau.domain.entity

import org.scalacheck.Gen

object EntityGen {

  val version: Gen[Int] = Gen.choose(0, 10)

  val seats: Gen[Int] = Gen.choose(50, 500)

  val train: Gen[Train] = train(version)

  def train(version: Gen[Int]): Gen[Train] = for {
    version <- version
    id      <- Gen.identifier
    seats   <- seats
  } yield Train(version, id, seats)

  val trains: Gen[List[Train]] = trains(version)

  def trains(version: Gen[Int]): Gen[List[Train]] =
    Gen.listOf(train(version))

  val station: Gen[Station] = station(version)

  def station(version: Gen[Int]): Gen[Station] = for {
    version <- version
    id      <- Gen.identifier
    name    <- Gen.nonEmptyListOf(Gen.alphaNumChar).map(_.mkString)
  } yield Station(version, id, name: String)

  val stations: Gen[List[Station]] = stations(version)

  def stations(version: Gen[Int]): Gen[List[Station]] =
    Gen.listOf(station(version))

  val trip: Gen[Trip] = trip(version, train, stations)

  def trip(version: Gen[Int], train: Gen[Train], stations: Gen[List[Station]]): Gen[Trip] = for {
    version  <- version
    id       <- Gen.identifier
    train    <- train
    stations <- stations
  } yield Trip(version, id, train.id, stations.map(_.id))

  val trips: Gen[List[Trip]] = trips(trip)

  def trips(trip: Gen[Trip]): Gen[List[Trip]] =
    Gen.nonEmptyListOf(trip)
}
