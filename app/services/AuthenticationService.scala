package services

import dao.UserDAO
import models.User

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import org.mindrot.jbcrypt.BCrypt
import utils.{IncorrectPasswordException, NoSuchUserException}

class AuthenticationService @Inject() (userDAO: UserDAO)(implicit executionContext: ExecutionContext)  {

  def checkCredentials(username: String, password: String): Future[Option[Long]] = {
    userDAO.findByUsername(username).map {
      case Some(user) if BCrypt.checkpw(password, user.password) => Some(user.id)
      case _ => None
    }
  }

  def registerUser(user: User): Future[Try[Long]] = {
    val passwordHash = BCrypt.hashpw(user.password, BCrypt.gensalt)
    userDAO.insert(User(user.id, user.username, user.email, passwordHash, user.imagePath))
  }

  def changeUserPassword(id: Long, oldPassword: String, newPassword: String): Future[Either[Throwable, Int]] = {
    userDAO.findById(id).flatMap {
      case Some(user) if BCrypt.checkpw(oldPassword, user.password) =>
        val passwordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt)
        userDAO.updatePassword(id, passwordHash).map {
          case 1 => Right(1)
          case _ => Left(new IncorrectPasswordException)
        }
      case _ => Future.successful(Left(new NoSuchUserException))
    }
  }
}