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

    connection.createStatement.execute(s"CREATE SCHEMA IF NOT EXISTS $schema;" )
    connection.createStatement.execute("DROP TABLE IF EXISTS users;")
    connection.createStatement.execute("CREATE TABLE users(name text, passwd text);")
    connection.createStatement.execute("INSERT INTO users VALUES('ivan', '123123');")
    connection.createStatement.execute("INSERT INTO users VALUES('ivan', '999999');")
    connection.createStatement.execute("INSERT INTO users VALUES('jack', '777777');")

//    connection.createStatement.execute(s"create table USERS (NAME text);")
//    connection.createStatement.execute(s"insert into USERS values ('ivan');")

    connection.close()
  }

  override def beforeStop(): Unit = {
    super.beforeStop()

    val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)

    connection.createStatement.execute(s"DROP SCHEMA IF EXISTS $schema CASCADE;")

    connection.close()
  }
}
