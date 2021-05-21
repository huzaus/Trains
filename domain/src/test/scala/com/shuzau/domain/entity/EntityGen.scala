package com.shuzau.domain.entity

import org.scalacheck.Gen

object EntityGen {

  val version: Gen[Int] = Gen.choose(0, 10)

  val seats: Gen[Int] = Gen.choose(50, 500)

  val train: Gen[Train] = for {
    version <- version
    id      <- Gen.identifier
    seats   <- seats
  } yield Train(version, id, seats)

  val station: Gen[Station] = for {
    version <- version
    id      <- Gen.identifier
    name    <- Gen.nonEmptyListOf(Gen.alphaNumChar).map(_.mkString)
  } yield Station(version, id, name: String)

  val trip: Gen[Trip] = for {
    version  <- version
    id       <- Gen.identifier
    train    <- Gen.identifier
    stations <- Gen.nonEmptyListOf(Gen.identifier)
  } yield Trip(version, id, train, stations)

}
