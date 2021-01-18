package ru.otus.jdbc.overview

import slick.jdbc.PostgresProfile.api._
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class SlickTest extends PgTestContainer {
  case class Person(name: String, passwd: String)

  class PersonTable(tag: Tag) extends Table[Person](tag, "users") {
    val name   = column[String]("name")
    val passwd = column[String]("passwd")
    val *      = (name, passwd).mapTo[Person]
  }

  "test slick" in {
    val db = Database.forURL(container.jdbcUrl, container.username, container.password)

    val allUsers = TableQuery[PersonTable]

    val filteredUsers = allUsers
      .filter(_.name === "ivan")
      .filter(_.passwd === "123123")
      .result
      .headOption

    val result = db.run(filteredUsers)

    val executedResult = Await.result(result, 2.seconds)

    assert(executedResult.get.name == "ivan")
    assert(executedResult.get.passwd == "123123")
  }
}
