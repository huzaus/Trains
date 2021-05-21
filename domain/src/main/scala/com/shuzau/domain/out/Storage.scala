package com.shuzau.domain.out

import com.shuzau.domain.entity.{Station, Train, Trip}
import zio.{Has, Ref, UIO, ULayer, ZIO, ZLayer}

object Storage {

  type StorageService = Has[TrainService] with Has[StationService] with Has[TripService]

  def save(train: Train): ZIO[StorageService, Nothing, Unit] =
    ZIO.accessM[StorageService](_.get[TrainService].save(train))

  def trains(): ZIO[StorageService, Nothing, Vector[Train]] =
    ZIO.accessM[StorageService](_.get[TrainService].load())

  def save(station: Station): ZIO[StorageService, Nothing, Unit] =
    ZIO.accessM[StorageService](_.get[StationService].save(station))

  def stations(): ZIO[StorageService, Nothing, Vector[Station]] =
    ZIO.accessM[StorageService](_.get[StationService].load())

  def save(trip: Trip): ZIO[StorageService, Nothing, Unit] =
    ZIO.accessM[StorageService](_.get[TripService].save(trip))

  def trips(): ZIO[StorageService, Nothing, Vector[Trip]] =
    ZIO.accessM[StorageService](_.get[TripService].load())

  trait TrainService {
    def save(train: Train): UIO[Unit]
    def load(): UIO[Vector[Train]]
  }

  trait StationService {
    def save(station: Station): UIO[Unit]
    def load(): UIO[Vector[Station]]
  }

  trait TripService {
    def save(trip: Trip): UIO[Unit]
    def load(): UIO[Vector[Trip]]
  }

  private val train: ULayer[Has[TrainService]] = Ref.make(Vector.empty[Train]).toLayer >>> ZLayer.fromFunction(ref =>
    new TrainService {
      override def save(train: Train): UIO[Unit] =
        ref.get.update(vector => vector :+ train)

      override def load(): UIO[Vector[Train]] =
        ref.get.get
    }
  )

  private val station: ULayer[Has[StationService]] =
    Ref.make(Vector.empty[Station]).toLayer >>> ZLayer.fromFunction(ref =>
      new StationService {
        override def save(station: Station): UIO[Unit] =
          ref.get.update(vector => vector :+ station)

        override def load(): UIO[Vector[Station]] =
          ref.get.get
      }
    )

  private val trip: ULayer[Has[TripService]] =
    Ref.make(Vector.empty[Trip]).toLayer >>> ZLayer.fromFunction(ref =>
      new TripService {
        override def save(trip: Trip): UIO[Unit] =
          ref.get.update(vector => vector :+ trip)

        override def load(): UIO[Vector[Trip]] =
          ref.get.get
      }
    )

  val layer: ULayer[StorageService] =
    train and station ++ trip
}