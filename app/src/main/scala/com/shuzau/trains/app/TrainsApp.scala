package com.shuzau.trains.app

import cats.syntax.either._
import com.shuzau.trains.app.util.Helper
import com.shuzau.trains.domain.in.TrainsModule
import zio.Runtime.default
import zio.ZIO

object TrainsApp extends App {

  if (args.length == 0) {
    Console.println("No directory given to source data.")
    System.exit(1)
  } else {
    val dir = args(0)

    val loadedStations = Helper.loadStations(dir)
    val stations       = loadedStations.collect { case Right(stations) => stations.station }.flatten
    val stationsErrors = loadedStations.collect { case Left(error) => error }

    val loadedTrains = Helper.loadTrains(dir)
    val trains       = loadedTrains.collect { case Right(trains) => trains.train }.flatten
    val trainsErrors = loadedTrains.collect { case Left(error) => error }

    val loadedTrips = Helper.loadTrips(dir)
    val trips       = loadedTrips.collect { case Right(trips) => trips }
    val tripsErrors = loadedTrips.collect { case Left(error) => error }

    val program = for {
      _      <- ZIO.foreach_(stations)(TrainsModule.register)
      _      <- ZIO.foreach_(trains)(TrainsModule.register)
      errors <- ZIO.foreach(trips) { case (file, trips) =>
                  ZIO.foreach(trips.trip) { trip =>
                    TrainsModule.register(trip).either.map(_.leftMap(error => s"$file: $error"))
                  }
                }
      report <- TrainsModule.report()
    } yield (errors.flatten, report)

    val (errors, report) = default.unsafeRun(program.provideCustomLayer(TrainsModule.layer))

    (for {
      _ <-
        Helper.reportErrors(stationsErrors ++ trainsErrors ++ tripsErrors ++ errors.collect { case Left(error) =>
          error
        })
      _ <- Helper.writeReport(report)
    } yield ()).left.foreach { error =>
      println(error.getMessage)
      System.exit(1)
    }
  }

}
