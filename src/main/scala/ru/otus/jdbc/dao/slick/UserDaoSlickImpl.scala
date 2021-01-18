package ru.otus.jdbc.dao.slick

import ru.otus.jdbc.model.{Role, User}
import slick.dbio.Effect
import slick.jdbc.PostgresProfile.api._
import slick.sql.FixedSqlAction

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class UserDaoSlickImpl(db: Database)(implicit ec: ExecutionContext) {
  import UserDaoSlickImpl._

  def getUser(userId: UUID): Future[Option[User]] = {
    val res = for {
      user  <- users.filter(user => user.id === userId).result.headOption
      roles <- usersToRoles.filter(_.usersId === userId).map(_.rolesCode).result.map(_.toSet)
    } yield user.map(_.toUser(roles))

    db.run(res)
  }

  def createUser(user: User): Future[User] = {
    val userToCreate = user.id match {
      case Some(_) => user
      case None    => user.copy(id=Some(UUID.randomUUID()))
    }

    val createUser = users += UserRow.fromUser(userToCreate)
    val createRoles = usersToRoles ++= userToCreate.roles.map(userToCreate.id.get -> _)

    val action = createUser >> createRoles >> DBIO.successful(userToCreate)

    db.run(action.transactionally)
  }

  def updateUser(user: User): Future[Unit] = {
    user.id match {
      case Some(userId) =>
        val updateUser = users
          .filter(_.id === userId)
          .map(u => (u.firstName, u.lastName, u.age))
          .update((user.firstName, user.lastName, user.age))
        val deleteRoles = usersToRoles.filter(_.usersId === userId).delete
        val insertRoles = usersToRoles ++= user.roles.map(userId -> _)

        val action = updateUser >> deleteRoles >> insertRoles >> DBIO.successful(())

        db.run(action.transactionally)
      case None => createUser(user).map(_ => None)
    }
  }

  def deleteUser(userId: UUID): Future[Option[User]] = {
    val deleteUser = users.filter(_.id === userId).delete
    val deleteRoles = usersToRoles.filter(_.usersId === userId).delete

    val action = deleteRoles >> deleteUser >> DBIO.successful(())

    for {
      user <- getUser(userId)
      _ <- db.run(action.transactionally)
    } yield user
  }

  private def findByCondition(condition: Users => Rep[Boolean]): Future[Seq[User]] =
    for {
      userRows <- db.run(users.filter(condition).result)
      userIds = userRows.map(_.id).collect { case id if id.isDefined => id.get }
      roles <- db.run(usersToRoles.filter(_.usersId.inSet(userIds)).result)
      users = userRows.map(
        ur => ur.toUser(roles.collect { case (userId, role) if userId == ur.id.get => role }.toSet)
      )
    } yield users

  def findByLastName(lastName: String): Future[Seq[User]] =
    findByCondition(_.lastName === lastName)

  def findAll(): Future[Seq[User]] =
    for {
      userRows <- db.run(users.result)
      userIds = userRows.map(_.id).collect { case id if id.isDefined => id.get }
      roles <- db.run(usersToRoles.filter(_.usersId.inSet(userIds)).result)
      users = userRows.map(
        ur => ur.toUser(roles.collect { case (userId, role) if userId == ur.id.get => role }.toSet)
      )
    } yield users

  private[jdbc] def deleteAll(): Future[Unit] =
    db.run(usersToRoles.delete >> users.delete >> DBIO.successful(()))
}

object UserDaoSlickImpl {
  implicit val rolesType: BaseColumnType[Role] = MappedColumnType.base[Role, String](
    {
      case Role.Reader => "reader"
      case Role.Manager => "manager"
      case Role.Admin => "admin"
    },
    {
      case "reader"  => Role.Reader
      case "manager" => Role.Manager
      case "admin"   => Role.Admin
    }
  )

  case class UserRow(
    id: Option[UUID],
    firstName: String,
    lastName: String,
    age: Int
  ) {
    def toUser(roles: Set[Role]): User = User(id, firstName, lastName, age, roles)
  }

  object UserRow extends ((Option[UUID], String, String, Int) => UserRow) {
    def fromUser(user: User): UserRow = UserRow(user.id, user.firstName, user.lastName, user.age)
  }

  class Users(tag: Tag) extends Table[UserRow](tag, "users") {
    val id        = column[UUID]("id", O.PrimaryKey)
    val firstName = column[String]("first_name")
    val lastName  = column[String]("last_name")
    val age       = column[Int]("age")

    val * = (id.?, firstName, lastName, age).mapTo[UserRow]
  }

  val users: TableQuery[Users] = TableQuery[Users]

  class UsersToRoles(tag: Tag) extends Table[(UUID, Role)](tag, "users_to_roles") {
    val usersId   = column[UUID]("users_id")
    val rolesCode = column[Role]("roles_code")

    val * = (usersId, rolesCode)
  }

  val usersToRoles = TableQuery[UsersToRoles]
}
