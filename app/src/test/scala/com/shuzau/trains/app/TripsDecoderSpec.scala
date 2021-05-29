package com.shuzau.trains.app

import com.shuzau.trains.app.util.FileHelper.loadTrips
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TripsDecoderSpec extends AnyFlatSpec with Matchers with EitherValues {

  behavior of "TripsDecoder"

  it should "decode all trips files" in {
    val result = loadTrips()
    result.filter(_.isLeft) shouldBe empty
    result.filter(_.isRight) should have size 10
  }
}
