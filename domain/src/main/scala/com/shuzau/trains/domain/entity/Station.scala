package com.shuzau.trains.domain.entity

import ru.tinkoff.phobos.decoding._
import ru.tinkoff.phobos.derivation.semiauto._
import ru.tinkoff.phobos.encoding._
import ru.tinkoff.phobos.syntax.attr

final case class Station(@attr version: Int, id: String, name: String)

object Station {
  implicit val stationEncoder: ElementEncoder[Station] = deriveElementEncoder
  implicit val stationDecoder: ElementDecoder[Station] = deriveElementDecoder
}
