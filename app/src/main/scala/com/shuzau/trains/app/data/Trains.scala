package com.shuzau.trains.app.data

import com.shuzau.trains.domain.entity.Train
import ru.tinkoff.phobos.decoding.XmlDecoder
import ru.tinkoff.phobos.derivation.semiauto.{deriveXmlDecoder, deriveXmlEncoder}
import ru.tinkoff.phobos.encoding.XmlEncoder

final case class Trains(train: List[Train])

object Trains {
  implicit val trainsXmlEncoder: XmlEncoder[Trains] = deriveXmlEncoder("trains")
  implicit val trainsXmlDecoder: XmlDecoder[Trains] = deriveXmlDecoder("trains")
}
