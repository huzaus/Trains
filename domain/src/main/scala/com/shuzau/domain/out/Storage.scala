package com.shuzau.domain.out

import com.shuzau.domain.entity.{Station, Train, Trip}
import zio.{Has, Ref, UIO, ULayer, ZIO, ZLayer}

object Storage {

  type StorageService = Has[TrainStorage] with Has[StationStorage] with Has[TripStorage]

  def save(train: Train): ZIO[StorageService, Nothing, Unit] =
    ZIO.accessM[StorageService](_.get[TrainStorage].save(train))

  def trains(): ZIO[StorageService, Nothing, Vector[Train]] =
    ZIO.accessM[StorageService](_.get[TrainStorage].load())

  def save(station: Station): ZIO[StorageService, Nothing, Unit] =
    ZIO.accessM[StorageService](_.get[StationStorage].save(station))

  def stations(): ZIO[StorageService, Nothing, Vector[Station]] =
    ZIO.accessM[StorageService](_.get[StationStorage].load())

  def save(trip: Trip): ZIO[StorageService, Nothing, Unit] =
    ZIO.accessM[StorageService](_.get[TripStorage].save(trip))

  def trips(): ZIO[StorageService, Nothing, Vector[Trip]] =
    ZIO.accessM[StorageService](_.get[TripStorage].load())

  trait TrainStorage {
    def save(train: Train): UIO[Unit]
    def load(): UIO[Vector[Train]]
  }

  trait StationStorage {
    def save(station: Station): UIO[Unit]
    def load(): UIO[Vector[Station]]
  }

  trait TripStorage {
    def save(trip: Trip): UIO[Unit]
    def load(): UIO[Vector[Trip]]
  }

  private val train: ULayer[Has[TrainStorage]] = Ref.make(Vector.empty[Train]).toLayer >>> ZLayer.fromFunction(ref =>
    new TrainStorage {
      override def save(train: Train): UIO[Unit] =
        ref.get.update(vector => vector :+ train)

      override def load(): UIO[Vector[Train]] =
        ref.get.get
    }
  )

  private val station: ULayer[Has[StationStorage]] =
    Ref.make(Vector.empty[Station]).toLayer >>> ZLayer.fromFunction(ref =>
      new StationStorage {
        override def save(station: Station): UIO[Unit] =
          ref.get.update(vector => vector :+ station)

        override def load(): UIO[Vector[Station]] =
          ref.get.get
      }
    )

  private val trip: ULayer[Has[TripStorage]] =
    Ref.make(Vector.empty[Trip]).toLayer >>> ZLayer.fromFunction(ref =>
      new TripStorage {
        override def save(trip: Trip): UIO[Unit] =
          ref.get.update(vector => vector :+ trip)

        override def load(): UIO[Vector[Trip]] =
          ref.get.get
      }
    )

  val layer: ULayer[StorageService] =
    train and station ++ trip
}