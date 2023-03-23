package services

import dao.UserDAO
import models.{FriendRequest, Friendship, User}
import utils.{AlreadyFriendsException, NoSuchRequestException, PendingRequestAlreadyExistsException, RequestAlreadySentException, SameUserIdException}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FriendRequestService @Inject() (userDAO: UserDAO)(implicit executionContext: ExecutionContext) {

  // GET FRIENDS REQUESTS
  def getFriendRequests(id: Long): Future[Seq[(FriendRequest, User)]] = {
    userDAO.findFriendRequestByReceiver(id)
  }

  // SEND FRIEND REQUEST
  def addFriendRequest(id: Long, receiverId: Long): Future[Either[Throwable, Long]] = {
    // NE MOZE SE POSLATI ZAHTEV SAMOM SEBI
    if (id == receiverId)
      Future.successful(Left(new SameUserIdException))
    else
      // DA LI SU OSOBE VEC PRIJATELJI, AKO JESU NE MOZE SE POSLATI
      userDAO.findFriendshipByUserIdFriendId(id, receiverId).flatMap {
        case Some(_) => Future.successful(Left(new AlreadyFriendsException))
        case None =>
          // DA LI POSTOJI FRIEND REQUEST OD OSOBE KOJOJ POKUSAVAS DA POSALJES FRIEND REQUEST UPUCEN TEBI
          userDAO.findPendingFriendRequestBySenderReceiver(receiverId, id).flatMap {
            case Some(_) => Future.successful(Left(new PendingRequestAlreadyExistsException))
            case None =>
              // DA LI FRIEND REQUEST VEC POSTOJI KAKO SE NE BI MOGAO POSLATI DVAPUT
              userDAO.findPendingFriendRequestBySenderReceiver(id, receiverId).flatMap {
                case Some(_) => Future.successful(Left(new RequestAlreadySentException))
                case None => userDAO.insert(FriendRequest(0, id, receiverId, 0)).map { r=>
                  Right(r)
                }
              }
          }
      }
  }

  // ACCEPT FRIEND REQUEST
  def acceptFriendRequest(id: Long, requestId: Long): Future[Either[Throwable, (Long, Long)]] = {
    userDAO.respondToFriendRequest(id, requestId, 2).flatMap { r =>
      userDAO.findFriendRequestById(requestId).flatMap {
        case Some(fr) =>
          for {
            v1 <- userDAO.insert(Friendship(0, fr.receiverId, fr.senderId))
            v2 <- userDAO.insert(Friendship(0, fr.senderId, fr.receiverId))
          } yield Right(v1, v2)
        case None => Future.successful(Left(new NoSuchRequestException))
      }
    }
  }

  // REJECT FRIEND REQUEST
  def rejectFriendRequest(id: Long, requestId: Long): Future[Either[Throwable, Int]] = {
    userDAO.respondToFriendRequest(id, requestId, 1).map {
      case 1 => Right(1)
      case _ => Left(new NoSuchRequestException)
    }
  }
}
