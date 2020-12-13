package ru.otus.jdbc

import scalikejdbc._


class ScalikeJDBC  extends PgTestContainer {

  "test interpolation" in {

    Class.forName(container.driverClassName)

    ConnectionPool.singleton(container.jdbcUrl, container.username, container.password)

    val name  = "ivan"
    val passw = "123123"

    case class Person(name: String, passw: String)

    val userNames = DB readOnly { implicit session =>
      sql"select NAME, PASSW from USERS where NAME = $name and PASSW = $passw"
        .map(rs =>
          Person(
            name = rs.string("NAME"),
            passw = rs.string("PASSW")
          )).list.apply()
    }

    assert(userNames.head.name == "ivan")
    assert(userNames.head.passw == "123123")
  }


  case class Person(name: String, passw: Option[String])

  object Person extends SQLSyntaxSupport[Person] {
    override val tableName = "USERS"
    def apply(a: SyntaxProvider[Person])(rs: WrappedResultSet): Person = apply(a.resultName)(rs)
    def apply(a: ResultName[Person])(rs: WrappedResultSet): Person = new Person(rs.string(a.name), rs.stringOpt(a.passw))
    def opt(a: SyntaxProvider[Person])(rs: WrappedResultSet): Option[Person] = rs.stringOpt(a.resultName.name).map(_ => apply(a)(rs))
  }

  "test Querry DSL" in {
    Class.forName(container.driverClassName)

    ConnectionPool.singleton(container.jdbcUrl, container.username, container.password)

//    DB autoCommit {
//      implicit s =>
//        try sql"drop table if not exists ${Person.table}".execute.apply()
//        catch { case e: Exception => }
//        sql"create table ${Person.table} (name varchar(256) not null, passw varchar(256))".execute.apply()
//
//    }


    DB localTx { implicit  s =>
//      val p = Person.column
//
//        applyUpdate(
//          insert
//            .into(Person).columns(p.name, p.passw)
//            .values("ivan", "123123")
//        )

      val pp = Person.syntax("p")

      val ivan: Person = withSQL(
        select
          .from(Person as pp)
          .where.eq(pp.name, "ivan")
      ).map(Person(pp)).single.apply().get

      assert(ivan.name == "ivan")
      assert(ivan.passw.get == "123123")
    }
  }
}