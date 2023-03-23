package services

import dao.UserDAO
import models.User
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData.FilePart
import utils.{FileTooLargeException, UnsupportedImageFormatException}

import java.nio.file.Paths
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject() (userDAO: UserDAO)(implicit executionContext: ExecutionContext) {

  // GET USER BY ID
  def getUserById(id: Long): Future[Option[User]] = {
    userDAO.findById(id)
  }

  // GET USER BY USERNAME
  def getUserByUsername(username: String): Future[Option[User]] = {
    userDAO.findByUsername(username)
  }

  // SEARCH USERS BY USERNAME
  def searchByUsername(username: String): Future[Seq[User]] = {
    userDAO.searchUsersByUsername(username)
  }

  // CHANGE USERNAME
  def changeUserEmail(id: Long, email: String): Future[Int] = {
    userDAO.updateEmail(id, email)
  }

  // CHANGE PROFILE PICTURE
  def setProfilePicture(id: Long, picture: FilePart[TemporaryFile]): Future[Either[Throwable, Int]] = {
    val fileSize = picture.fileSize
    val contentType = picture.contentType
    // da li id.jpg pa da svaki put menjam i ne diram bazu, ili da svaki put generisem novi naziv
    val name = java.util.UUID.randomUUID().toString

    if (fileSize > 5242880) Future.successful(Left(new FileTooLargeException))
    else
      contentType match {
        case Some("image/png") =>
          picture.ref.copyTo(Paths.get(s"public\\images\\profile\\$name.png"), replace = true)
          userDAO.editProfileImage(id, s"$name.png").map { r=>
            Right(r)
          }
        case Some("image/jpeg") =>
          picture.ref.copyTo(Paths.get(s"public\\images\\profile\\$name.jpg"), replace = true)
          userDAO.editProfileImage(id, s"$name.jpg").map { r=>
            Right(r)
          }
        case _ =>
          Future.successful(Left(new UnsupportedImageFormatException))
      }
  }

  // GET USERS' FRIENDS
  def getFriends(id: Long): Future[Vector[User]] = {
    userDAO.findFriendsById(id)
  }
}
