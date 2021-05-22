package com.shuzau.domain.in

import cats.syntax.either._
import cats.syntax.foldable._
import cats.syntax.traverse._
import com.shuzau.domain.entity.{Station, Train, Trip}
import com.shuzau.domain.out.Storage
import com.shuzau.domain.out.Storage.{StationStorage, TrainStorage, TripStorage}
import zio.{Has, IO, UIO, ULayer, ZIO, ZLayer}

object TrainsModule {

  type TrainsService = Has[Service]

  trait Service {
    def register(train: Train): UIO[Unit]
    def register(station: Station): UIO[Unit]
    def register(trip: Trip): IO[String, Unit]
    def report(): UIO[Map[Station, List[Train]]]
  }

  def register(train: Train): ZIO[TrainsService, Nothing, Unit] =
    ZIO.accessM[TrainsService](_.get[Service].register(train))

  def register(station: Station): ZIO[TrainsService, Nothing, Unit] =
    ZIO.accessM[TrainsService](_.get[Service].register(station))

  def register(trip: Trip): ZIO[TrainsService, String, Unit] =
    ZIO.accessM[TrainsService](_.get[Service].register(trip))

  def report(): ZIO[TrainsService, Nothing, Map[Station, List[Train]]] =
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

        private def validateTrip(trip: Trip, trains: Vector[Train], stations: Vector[Station]): Either[String, Unit] =
          List(
            Either
              .cond(
                trains.exists(train => train.version == trip.version && train.id == trip.train),
                (),
                s"Unknown train id='${trip.train}' version='${trip.version}'"
              )
              .toValidatedNec,
            trip.stations.traverse(tripStation =>
              Either
                .cond(
                  stations.exists(station => station.version == trip.version && station.id == tripStation),
                  (),
                  s"Unknown station id='$tripStation' version='${trip.version}'"
                )
                .toValidatedNec
            )
          ).sequence_.fold(
            failedConditions => failedConditions.mkString_(". ").asLeft,
            _ => ().asRight
          )

        override def report(): UIO[Map[Station, List[Train]]] = ZIO.succeed(Map.empty)
      }
    )
}
