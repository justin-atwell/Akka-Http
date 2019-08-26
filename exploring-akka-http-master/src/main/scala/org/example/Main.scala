package org.example

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{HttpApp, Route}
import org.example.routes.{AuthorRoutes, BookRoutes}
import org.example.service.DatabaseBootstrapper

import scala.util.{Success, Try}

object Main extends HttpApp with App with DatabaseBootstrapper {
  override def route: Route =
    pathEndOrSingleSlash { complete("Server up and running")} ~
      new AuthorRoutes(database, authors, books).routes ~
      new BookRoutes(database, books).routes

  implicit val actorSystem = ActorSystem("api-actor-system")
  implicit val dispatcher = actorSystem.dispatcher

  bootstrapDatabase.andThen {
    case Success(_) =>
      val port = Try(actorSystem.settings.config.getInt("akka.http.server.port")).getOrElse(8080)
      startServer("localhost", port)
  }
}
