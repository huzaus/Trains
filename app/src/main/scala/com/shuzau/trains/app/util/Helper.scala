package com.shuzau.trains.app.util

import cats.syntax.either._
import com.shuzau.trains.app.data.{Stations, Trains, Trips}
import com.shuzau.trains.domain.entity.{Report, Station, Train}
import ru.tinkoff.phobos.decoding.XmlDecoder

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.Using

object Helper {

  def loadStations(dir: String): List[Either[String, Stations]] = {
    val folder = new File(s"$dir/stations")
    folder
      .listFiles()
      .filter(xmlFileFilter)
      .map { file =>
        (for {
          xml      <- Helper.readFile(file)
          stations <- XmlDecoder[Stations].decode(xml).leftMap(_.getMessage())
        } yield stations).leftMap(error => s"${file.getName}: $error")
      }
      .toList
  }

  def loadTrains(dir: String): List[Either[String, Trains]] = {
    val folder = new File(s"$dir/trains")
    folder
      .listFiles()
      .filter(xmlFileFilter)
      .map { file =>
        (for {
          xml      <- Helper.readFile(file)
          stations <- XmlDecoder[Trains].decode(xml).leftMap(_.getMessage())
        } yield stations).leftMap(error => s"${file.getName}: $error")
      }
      .toList
  }

  def loadTrips(dir: String): List[Either[String, (String, Trips)]] = {
    val folder = new File(s"$dir/trips")
    folder
      .listFiles()
      .filter(xmlFileFilter)
      .map { file =>
        (for {
          xml      <- Helper.readFile(file)
          stations <- XmlDecoder[Trips].decode(xml).leftMap(_.getMessage())
        } yield (file.getName, stations)).leftMap(error => s"${file.getName}: $error")
      }
      .toList
  }

  def readFile(file: File): Either[String, String] =
    Using(Source.fromFile(file))(_.getLines().mkString(System.lineSeparator())).toEither.leftMap(_.getMessage)

  def reportErrors(errors: List[String]): Either[Throwable, Unit] = {
    val file = new File("errors.txt")
    println(s"See errors in: ${file.getAbsolutePath}")
    write(file, errors)
  }

  def writeReport(report: Report): Either[Throwable, Unit] = {
    val file = new File("report.txt")
    println(s"See report in: ${file.getAbsolutePath}")
    write(
      file,
      report
        .sorted()
        .take(15)
        .map { case (station, trains) =>
          format(station, trains)
        }
        .toList
    )
  }

  def write(file: File, content: List[String]): Either[Throwable, Unit] =
    Using(new PrintWriter(file)) { writer =>
      content.foreach(writer.println)
    }.toEither

  private def xmlFileFilter: File => Boolean = file => file.isFile && file.getName.endsWith(".xml")

  private def format(station: Station, trains: List[Train]): String =
    if (trains.size == 1) {
      val seats = trains.head.seats
      s"${station.name} is visited by a train with $seats seats - it can receive $seats passangers."
    } else {
      val seats = trains.map(_.seats)
      s"${station.name} is visited by a trains with ${seats.mkString(", ")} seats - it can receive ${seats.sum} passangers."
    }

}
