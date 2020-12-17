package ru.otus.jdbc.slick

import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import org.scalatest.BeforeAndAfter
import org.scalatest.freespec.AnyFreeSpec
import slick.jdbc.PostgresProfile.api._

class PgSlickTestContainer extends AnyFreeSpec with ForAllTestContainer with BeforeAndAfter {

  override val container = PostgreSQLContainer()

  val schema = "otus"


  override def afterStart(): Unit = {
    Class.forName(container.driverClassName)

    val db = Database.forURL(container.jdbcUrl, container.username, container.password)


    db.close()
  }


  override def beforeStop(): Unit = {
    super.beforeStop()

    val db = Database.forURL(container.jdbcUrl, container.username, container.password)

    db.close()
  }
}
