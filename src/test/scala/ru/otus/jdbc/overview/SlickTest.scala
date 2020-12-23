package ru.otus.jdbc.overview

import slick.jdbc.PostgresProfile.api._
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._


class SlickTest extends PgTestContainer {
  case class Person(name:  String, passw: String)

  class PersonTable(tag: Tag) extends Table[Person](tag, "users") {
    val name     = column[String]("name")
    val passw    = column[String]("passw")
    val *        = (name, passw).mapTo[Person]
  }


  "test slick" in {



  }
}