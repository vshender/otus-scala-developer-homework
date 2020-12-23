package ru.otus.jdbc.overview

import java.sql.DriverManager

class ScalaJDBCTest extends PgTestContainer {


  "test example of PrepareStatement in ScalaJDBC" in {
    Class.forName(container.driverClassName)

  }
}