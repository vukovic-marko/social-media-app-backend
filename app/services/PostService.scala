package services

import dao.UserDAO
import models.{Post, ResponsePostWithLikeInformationDTO}
import utils.{NoSuchPostException, NoSuchUserException, NotFriendsException}

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PostService @Inject() (userDAO: UserDAO)(implicit executionContext: ExecutionContext) {

  // GET POSTS BY USER ID
  def getPostsById(id: Long): Future[Seq[ResponsePostWithLikeInformationDTO]] = {
    userDAO.getFriendPostsById(id, id)
  }

  // ADD POST
  def addPost(id: Long, username:String, content: String): Future[Long] = {
    userDAO.insert(Post(0, id, username, LocalDateTime.now(), content))
  }

  // EDIT POST
  def changePostContent(id: Long, postId: Long, content: String): Future[Either[Throwable, Int]] = {
    userDAO.updatePost(id, postId, content).map {
      case 1 => Right(1)
      case _ => Left(new NoSuchPostException)
    }
  }

  // GET ALL FRIENDS POSTS
  def getFriendsPosts(id: Long): Future[Vector[ResponsePostWithLikeInformationDTO]] = {
    userDAO.getFriendsPostsById(id)
  }

  // GET FRIENDS POSTS BY ID
  def getFriendPosts(id: Long, friendId: Long): Future[Either[Throwable, Seq[ResponsePostWithLikeInformationDTO]]] = {
    userDAO.findById(friendId).flatMap {
      case Some(_) =>
      userDAO.findFriendshipByUserIdFriendId(id, friendId).flatMap {
        case Some(_) => userDAO.getFriendPostsById(id, friendId).map { r =>
          Right(r)
        }
        case None => Future.successful(Left(new NotFriendsException))
      }
      case None => Future.successful(Left(new NoSuchUserException))
    }
  }
}
