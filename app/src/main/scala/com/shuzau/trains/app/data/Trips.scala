package com.shuzau.trains.app.data

import com.shuzau.trains.domain.entity.Trip
import ru.tinkoff.phobos.decoding.XmlDecoder
import ru.tinkoff.phobos.derivation.semiauto.{deriveXmlDecoder, deriveXmlEncoder}
import ru.tinkoff.phobos.encoding.XmlEncoder

final case class Trips(trip: List[Trip])

object Trips {
  implicit val tripsXmlEncoder: XmlEncoder[Trips] = deriveXmlEncoder("trips")
  implicit val tripsXmlDecoder: XmlDecoder[Trips] = deriveXmlDecoder("trips")
}
