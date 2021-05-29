package com.shuzau.trains.app.data

import com.shuzau.trains.domain.entity.Station
import ru.tinkoff.phobos.decoding.XmlDecoder
import ru.tinkoff.phobos.derivation.semiauto.{deriveXmlDecoder, deriveXmlEncoder}
import ru.tinkoff.phobos.encoding.XmlEncoder

final case class Stations(station: List[Station])

object Stations {
  implicit val stationsXmlEncoder: XmlEncoder[Stations] = deriveXmlEncoder("stations")
  implicit val stationsXmlDecoder: XmlDecoder[Stations] = deriveXmlDecoder("stations")
}
