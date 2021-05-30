package com.shuzau.trains.app

import com.shuzau.trains.app.util.Helper.loadTrains
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TrainsDecoderSpec extends AnyFlatSpec with Matchers with EitherValues {

  behavior of "TrainsDecoder"

  it should "decode all trains files" in {
    val result = loadTrains("src/main/resources")
    result.filter(_.isLeft) should have size 1
    result.filter(_.isRight) should have size 9
  }
}
