package com.shuzau.trains.app

import com.shuzau.trains.app.util.Helper.loadStations
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class StationsDecoderSpec extends AnyFlatSpec with Matchers with EitherValues {

  behavior of "StationsDecoder"

  it should "decode all stations files" in {
    val result = loadStations("app/src/main/resources")
    result.filter(_.isLeft) should have size 1
    result.filter(_.isRight) should have size 9
  }
}
