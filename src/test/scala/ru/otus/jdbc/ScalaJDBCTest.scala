package ru.otus.jdbc

import java.sql.DriverManager

import org.scalatest.freespec.AnyFreeSpec

class ScalaJDBCTest extends AnyFreeSpec with PgEmbedded {

  "test" in {

    val connection = DriverManager.getConnection(url)

    val name  = "ivan"
    val passw = "123123"

//    connection.createStatement.execute(s"select * from USERS")
//    connection.createStatement.execute(s"select * from USERS")

    val statement = connection
      .prepareStatement(
        "select * from USERS where NAME = ? and PASSW = ?"
      )

    statement.setString(1, name)
    statement.setString(2, passw)

    val set = statement.executeQuery()

    val array = set.getArray("NAME")


    connection.close()
  }
}