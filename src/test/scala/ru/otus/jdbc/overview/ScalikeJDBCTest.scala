package ru.otus.jdbc.overview

import scalikejdbc._

class ScalikeJDBCTest  extends PgTestContainer {
  "test interpolation" in {
    Class.forName(container.driverClassName)

    case class Person(name: String, passwd: String)

    val name = "ivan"
    val passwd = "123123"

    ConnectionPool.singleton(container.jdbcUrl, container.username, container.password)

    val userNames = DB readOnly { implicit session =>
      sql"SELECT name, passwd FROM users WHERE name = $name AND passwd = $passwd"
        .map(rs =>
          Person(
            name = rs.string("name"),
            passwd = rs.string("passwd")
          )).list.apply()
    }

    assert(userNames.head.name == "ivan")
    assert(userNames.head.passwd == "123123")
  }

  "test Query DSL" in {
    Class.forName(container.driverClassName)

    case class Person(name: String, passwd: String)
    object Person extends SQLSyntaxSupport[Person] {
      override val tableName = "USERS"
      def apply(a: SyntaxProvider[Person])(rs: WrappedResultSet): Person = apply(a.resultName)(rs)
      def apply(a: ResultName[Person])(rs: WrappedResultSet): Person = new Person(
        name = rs.string(a.name),
        passwd = rs.string(a.passwd))
    }

    ConnectionPool.singleton(container.jdbcUrl, container.username, container.password)

    DB localTx { implicit s =>
      val pp = Person.syntax("p")

      val ivan: Person = withSQL(
        select
          .from(Person as pp)
          .where
          .withRoundBracket {
            _.eq(pp.name, "ivan")
              .and
              .eq(pp.passwd, "123123")
          }
      )
        .map(Person(pp)).single.apply().get

      assert(ivan.name == "ivan")
      assert(ivan.passwd == "123123")
    }
  }
}
