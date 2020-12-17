package ru.otus.jdbc.overview

import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import org.scalatest.BeforeAndAfter
import org.scalatest.freespec.AnyFreeSpec

import java.sql.DriverManager

class PgTestContainer extends AnyFreeSpec with ForAllTestContainer with BeforeAndAfter {

  override val container = PostgreSQLContainer()

  val schema = "otus"

  override def afterStart(): Unit = {
    Class.forName(container.driverClassName)

    val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)

    connection.createStatement.execute(s"create schema if not exists $schema;" )
    connection.createStatement.execute(s"drop table if exists USERS;")
    connection.createStatement.execute(s"create table USERS (NAME text, PASSW text);")
    connection.createStatement.execute(s"insert into USERS values ('ivan', '123123');")
    connection.createStatement.execute(s"insert into USERS values ('ivan', '999999');")
    connection.createStatement.execute(s"insert into USERS values ('jack', '777777');")

//    connection.createStatement.execute(s"create table USERS (NAME text);")
//    connection.createStatement.execute(s"insert into USERS values ('ivan');")

    connection.close()
  }


  override def beforeStop(): Unit = {
    super.beforeStop()

    val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)

    connection.createStatement.execute(s"drop schema if exists $schema cascade;")

    connection.close()
  }
}
