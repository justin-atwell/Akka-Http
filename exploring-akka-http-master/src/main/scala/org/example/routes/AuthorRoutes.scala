package org.example.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{Directives, Route}
import org.example.model.entities.{Author, AuthorExtended, Authors, Books}
import org.example.util.JsonSupport._
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


class AuthorRoutes(db: Database, authors: TableQuery[Authors], books: TableQuery[Books]) extends Directives {

  private def getAuthorsRoute: Route = path("authors") {
    get {
      parameters('start.as[Long], 'size.as[Long]) { (start, size) =>
        val result = for {
          authorList <- db.run(authors.drop(start).take(size).result)
          totalCount <- db.run(authors.length.result)
        } yield (authorList, totalCount)

        onComplete(result) {
          case Success(data) => data match { case (authorList, totalCount) =>
            respondWithHeader(RawHeader("X-Total-Count", totalCount.toString)) {
              complete(authorList)
            }
          }
          case Failure(ex) => complete(StatusCodes.InternalServerError, ex.getMessage)
        }
      }
    }
  }

  private def getAuthorById: Route = path("authors" / LongNumber) { authorId =>
    get {
      rejectEmptyResponse(complete(db.run(authors.filter(_.id === authorId).result.headOption)))
    }
  }

  private def getAuthorsWithBooksCountRoute: Route = path("authors" / "book_counting") {
    get {
      parameters('start.as[Long], 'size.as[Long]) { (start, size) =>
        val query = (for {
          author <- authors.drop(start).take(size)
          book   <- books.filter(_.authorId === author.id)
        } yield (author, book)).groupBy(_._1).map { case (author, booksQuery) =>
          (author, booksQuery.map(_._2.id).length)
        }

        val result = for {
          authorList <- db.run(query.result)
          totalCount <- db.run(authors.length.result)
        } yield (authorList, totalCount)

        onComplete(result) {
          case Success(data) => data match {
            case (authorList, totalCount) =>
              respondWithHeader(RawHeader("X-Total-Count", totalCount.toString)) {
                complete(authorList.map { case (author: Author, booksCount) =>
                  AuthorExtended(author.id, author.name, booksCount)
                })
              }
          }
          case Failure(ex) => complete(StatusCodes.InternalServerError, ex.getMessage)
        }
      }
    }
  }

  lazy val routes: Route = getAuthorsRoute ~ getAuthorById ~ getAuthorsWithBooksCountRoute

}
