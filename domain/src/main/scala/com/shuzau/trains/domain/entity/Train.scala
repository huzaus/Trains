package com.shuzau.trains.domain.entity

import ru.tinkoff.phobos.decoding.ElementDecoder
import ru.tinkoff.phobos.derivation.semiauto._
import ru.tinkoff.phobos.encoding.ElementEncoder
import ru.tinkoff.phobos.syntax.attr

final case class Train(@attr version: Int, id: String, seats: Int)

object Train {
  implicit val trainEncoder: ElementEncoder[Train] = deriveElementEncoder
  implicit val trainDecoder: ElementDecoder[Train] = deriveElementDecoder
}
