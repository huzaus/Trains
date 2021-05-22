package com.shuzau.domain.in

import cats.syntax.either._
import cats.syntax.foldable._
import cats.syntax.traverse._
import com.shuzau.domain.entity.{Report, Station, Train, Trip}
import com.shuzau.domain.out.Storage
import com.shuzau.domain.out.Storage.{StationStorage, TrainStorage, TripStorage}
import zio.{Has, IO, UIO, ULayer, ZIO, ZLayer}

object TrainsModule {

  type TrainsService = Has[Service]

  trait Service {
    def register(train: Train): UIO[Unit]
    def register(station: Station): UIO[Unit]
    def register(trip: Trip): IO[String, Unit]
    def report(): UIO[Report]
  }

  def register(train: Train): ZIO[TrainsService, Nothing, Unit] =
    ZIO.accessM[TrainsService](_.get[Service].register(train))

  def register(station: Station): ZIO[TrainsService, Nothing, Unit] =
    ZIO.accessM[TrainsService](_.get[Service].register(station))

  def register(trip: Trip): ZIO[TrainsService, String, Unit] =
    ZIO.accessM[TrainsService](_.get[Service].register(trip))

  def report(): ZIO[TrainsService, Nothing, Report] =
    ZIO.accessM[TrainsService](_.get[Service].report())

  val layer: ULayer[TrainsService] =
    Storage.layer >>> ZLayer.fromFunction(storage =>
      new Service {
        override def register(train: Train): UIO[Unit] =
          storage.get[TrainStorage].save(train)

        override def register(station: Station): UIO[Unit] =
          storage.get[StationStorage].save(station)

        override def register(trip: Trip): IO[String, Unit] = for {
          trains   <- storage.get[TrainStorage].load()
          stations <- storage.get[StationStorage].load()
          _        <- ZIO.fromEither(validateTrip(trip, trains, stations))
          _        <- storage.get[TripStorage].save(trip)
        } yield ()

        override def report(): UIO[Report] = {

          def buildReport(trains: Vector[Train], stations: Vector[Station], trips: Vector[Trip]) = {
            def process(report: Report, trip: Trip): Report = {
              val train: Option[Train] = trains.find(trainPredicate(trip))
              train.fold(report) { train =>
                trip.stations
                  .flatMap(id => stations.find(stationPredicate(id, trip.version)))
                  .foldLeft(report) { case (report, station) =>
                    report.visit(station, train)
                  }
              }
            }
            trips.foldLeft(Report.empty) { case (report, trip) => process(report, trip) }
          }

          for {
            trains   <- storage.get[TrainStorage].load()
            stations <- storage.get[StationStorage].load()
            trips    <- storage.get[TripStorage].load()
          } yield buildReport(trains, stations, trips)
        }

        private def trainPredicate(trip: Trip): Train => Boolean =
          train => train.version == trip.version && train.id == trip.train

        private def stationPredicate(id: String, version: Int): Station => Boolean =
          station => station.version == version && station.id == id

        private def validateTrip(trip: Trip, trains: Vector[Train], stations: Vector[Station]): Either[String, Unit] =
          List(
            Either
              .cond(
                trains.exists(trainPredicate(trip)),
                (),
                s"Unknown train id='${trip.train}' version='${trip.version}'"
              )
              .toValidatedNec,
            trip.stations.traverse(id =>
              Either
                .cond(
                  stations.exists(stationPredicate(id, trip.version)),
                  (),
                  s"Unknown station id='$id' version='${trip.version}'"
                )
                .toValidatedNec
            )
          ).sequence_.fold(
            failedConditions => failedConditions.mkString_(". ").asLeft,
            _ => ().asRight
          )

      }
    )
}
