package com.shuzau.trains.app.util

import cats.syntax.either._
import com.shuzau.trains.app.data.{Stations, Trains, Trips}
import ru.tinkoff.phobos.decoding.XmlDecoder

import java.io.File
import scala.io.Source
import scala.util.Using

object FileHelper {

  def loadStations(): Array[Either[String, Stations]] = {
    val folder = new File(getClass.getResource("/stations").getPath)
    folder.listFiles().filter(xmlFileFilter).map { file =>
      (for {
        xml      <- FileHelper.readFile(file)
        stations <- XmlDecoder[Stations].decode(xml).leftMap(_.getMessage())
      } yield stations).leftMap(error => s"${file.getName}: $error")
    }
  }

  def loadTrains(): Array[Either[String, Trains]] = {
    val folder = new File(getClass.getResource("/trains").getPath)
    folder.listFiles().filter(xmlFileFilter).map { file =>
      (for {
        xml      <- FileHelper.readFile(file)
        stations <- XmlDecoder[Trains].decode(xml).leftMap(_.getMessage())
      } yield stations).leftMap(error => s"${file.getName}: $error")
    }
  }

  def loadTrips(): Array[Either[String, Trips]] = {
    val folder = new File(getClass.getResource("/trips").getPath)
    folder.listFiles().filter(xmlFileFilter).map { file =>
      (for {
        xml      <- FileHelper.readFile(file)
        stations <- XmlDecoder[Trips].decode(xml).leftMap(_.getMessage())
      } yield stations).leftMap(error => s"${file.getName}: $error")
    }
  }

  def readFile(file: File): Either[String, String] =
    Using(Source.fromFile(file))(_.getLines().mkString(System.lineSeparator())).toEither.leftMap(_.getMessage)

  private def xmlFileFilter: File => Boolean = file => file.isFile && file.getName.endsWith(".xml")

}
