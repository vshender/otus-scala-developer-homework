package ru.otus.jdbc

import org.scalatest.freespec.AnyFreeSpec
import scalikejdbc._


class ScalikeJDBC  extends AnyFreeSpec with PgEmbedded {

  "test" in {

    ConnectionPool.singleton(url, "userName", "password")

    val name  = "ivan"
    val passw = "123123"


    val userNames = DB readOnly { implicit session =>
      sql"select NAME from USERS where NAME = $name and PASSW = $passw"
        .map(_.string(1)).list.apply()
    }
    assert(userNames.head == "ivan")

  }

}
