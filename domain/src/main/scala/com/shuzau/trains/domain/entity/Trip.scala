package com.shuzau.trains.domain.entity

import ru.tinkoff.phobos.decoding.ElementDecoder
import ru.tinkoff.phobos.derivation.semiauto._
import ru.tinkoff.phobos.encoding.ElementEncoder
import ru.tinkoff.phobos.syntax.attr

final case class Trip(@attr version: Int, id: String, train: String, stations: Stations)

object Trip {
  implicit val tripXmlEncoder: ElementEncoder[Trip] = deriveElementEncoder
  implicit val tripXmlDecoder: ElementDecoder[Trip] = deriveElementDecoder
}

final case class Stations(station: List[String])

object Stations {
  implicit val stationsEncoder: ElementEncoder[Stations] = deriveElementEncoder
  implicit val stationsDecoder: ElementDecoder[Stations] = deriveElementDecoder
}
