package services

import dao.UserDAO
import models.Like
import utils.{AlreadyLikedException, NoSuchPostException, NotFriendsException}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LikeService @Inject() (userDAO: UserDAO)(implicit executionContext: ExecutionContext) {

  // LIKE POST
  def addPostLike(id: Long, friendId: Long, postId: Long): Future[Either[Throwable, Long]] = {
    // SAMO PRIJATELJIMA SE MOZE LAJKOVATI OBJAVA
    userDAO.findFriendshipByUserIdFriendId(id, friendId).flatMap {
      case None => Future.successful(Left(new NotFriendsException))
      case Some(_) =>
        // PROVERITI DA LI PRIJATELJ friendId IMA OBJAVU postId
        userDAO.findPostByPostIdUserId(postId, friendId).flatMap {
          case None => Future.successful(Left(new NoSuchPostException))
          case Some(_) =>
            // DA LI VEC POSTOJI LAJK U BAZI
            userDAO.findLikeByUserIdPostId(id, postId).flatMap {
              case Some(like) =>
                // AKO POSTOJI SA STATUSOM 1, GRESKA
                if (like.status == 1) Future.successful(Left(new AlreadyLikedException))
                // AKO POSTOJI SA STATUSOM 0, ZNACI DA JU JE KORISNIK PRETHODNO DISLAJKOVAO PA JE MOZE PONOVO LAJKOVATI
                else
                  userDAO.updateLike(id, postId).map { _ =>
                    Right(like.id)
                  }
              // AKO NE POSTOJI LAJK U BAZI MOZE SE DODATI
              case None =>
                userDAO.insert(Like(0, id, postId, 1)).map { r=>
                  Right(r)
                }
            }
        }
    }
  }

  // DISLIKE LIKED POST
  def removeLike(id : Long, friendId: Long, postId: Long): Future[Either[Throwable, Long]] = {
    userDAO.dislikeLikedPost(id, postId).flatMap {
      case 1 => Future.successful(Right(1))
      case 0 => Future.successful(Left(new NoSuchPostException))
    }
  }

  // GET NUMBER OF LIKES
  def getNumberOfLikes(id: Long, friendId: Long, postId: Long): Future[Either[Throwable, Int]] = {
    userDAO.findFriendshipByUserIdFriendId(id, friendId).flatMap {
      case None => Future.successful(Left(new NotFriendsException))
      case Some(_) =>
        userDAO.findPostByPostIdUserId(postId, friendId).flatMap {
          case None => Future.successful(Left(new NoSuchPostException))
          case Some(_) =>
            userDAO.numberOfLikes(postId).map { r=>
              Right(r)
            }
        }
    }
  }
}
