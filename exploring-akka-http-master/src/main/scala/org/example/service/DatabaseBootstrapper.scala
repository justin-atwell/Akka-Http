package org.example.service

import org.example.model.entities.{Author, Authors, Book, Books}
import slick.lifted.TableQuery
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

trait DatabaseBootstrapper {
  protected lazy val database: Database = Database.forConfig("h2mem")

  protected lazy val authors = TableQuery[Authors]
  protected lazy val books = TableQuery[Books]

  def bootstrapDatabase(): Future[Unit] = {
    database.run(DBIO.seq(
      (authors.schema ++ books.schema).create,

      authors ++= Seq(
        Author(1, "George R. R. Martin"),
        Author(2, "J. R. R. Tolkien")
      ),

      books ++= Seq(
        Book(1, 1, "A Game of Thrones", 5),
        Book(2, 1, "A Clash of Kings", 4),
        Book(3, 1, "A Storm of Swords", 3),
        Book(4, 1, "A Feast for Crows", 2),
        Book(5, 1, "A Dance with Dragons", 1),
        Book(6, 2, "The Hobbit", 6),
        Book(7, 2, "The Lord of the Rings", 9),
        Book(8, 2, "The Silmarillion", 0)
      )
    ))
  }
}