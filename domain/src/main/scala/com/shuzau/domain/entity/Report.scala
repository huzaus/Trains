package com.shuzau.domain.entity

import cats.syntax.option._

import scala.collection.immutable.ListMap

final case class Report(map: Map[Station, List[Train]]) {
  def visit(station: Station, train: Train): Report =
    copy(map = map.updatedWith(station)(list => list.fold(List(train))(list => list :+ train).some))

  def sorted(): ListMap[Station, List[Train]] =
    ListMap(map.toSeq.sortWith(_._2.map(_.seats).sum > _._2.map(_.seats).sum): _*)
}

object Report {
  val empty: Report = Report(Map())
}
