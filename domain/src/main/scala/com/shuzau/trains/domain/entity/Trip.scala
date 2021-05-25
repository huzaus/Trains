package com.shuzau.trains.domain.entity

final case class Trip(version: Int, id: String, train: String, stations: List[String])
