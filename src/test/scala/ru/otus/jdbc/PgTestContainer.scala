package ru.otus.jdbc

import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import org.scalatest.BeforeAndAfter
import org.scalatest.freespec.AnyFreeSpec

import java.sql.DriverManager

class PgTestContainer extends AnyFreeSpec with ForAllTestContainer with BeforeAndAfter {

  override val container = PostgreSQLContainer()

  var schema: String = _


  override def afterStart(): Unit = {
    Class.forName(container.driverClassName)

    val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)

    val name = "ivan"
    val passw = "123123"

    connection.createStatement.execute(s"""create schema "$schema" """)
    connection.createStatement.execute(s"""drop table if exists USERS;""")
    connection.createStatement.execute(s"""create table USERS (NAME text, PASSW text);""")
    connection.createStatement.execute(s"""insert into USERS values ('ivan', '123123');""")

    connection.close()
  }


  override def beforeStop(): Unit = {
    super.beforeStop()

    val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)

    connection.createStatement.execute(s"""drop schema "$schema" cascade """)

    connection.close()
  }
}
