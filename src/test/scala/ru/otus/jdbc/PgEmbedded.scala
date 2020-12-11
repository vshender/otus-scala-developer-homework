package ru.otus.jdbc

import java.sql.DriverManager
import java.util.UUID

import org.scalatest.{BeforeAndAfterAll, Suite}
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres
import ru.yandex.qatools.embed.postgresql.distribution.Version.Main.V9_6
import ru.yandex.qatools.embed.postgresql.distribution.Version.V10_3


trait PgEmbedded extends BeforeAndAfterAll {

  this: Suite =>


  var url: String = _
  var postgres: EmbeddedPostgres = _
  var schema: String = _

  override def beforeAll(): Unit = {
    super.beforeAll()

    postgres = new EmbeddedPostgres(V9_6)
    url = postgres.start("localhost", 5432, "dbName", "userName", "password")
    schema = UUID.randomUUID().toString


    val conn = DriverManager.getConnection(url)
    conn.createStatement.execute(s"""create schema "$schema" """)
    conn.createStatement.execute(s"""drop table if exists USERS;""")
    conn.createStatement.execute(s"""create table USERS (NAME text, PASSW text);""")
    conn.createStatement.execute(s"""insert into USERS values ('ivan', '123123');""")

    conn.close()
  }

  override def afterAll(): Unit = {
    val conn = DriverManager.getConnection(url)
    conn.createStatement.execute(s"""drop schema "$schema" cascade """)

    conn.close()

    super.afterAll()
  }
}
