package controllers

import auth.AuthAction
import models.ResponseFriendRequestDTO
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.FriendRequestService
import utils._
import utils.Formats._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class FriendRequestController @Inject()(authAction: AuthAction, friendRequestService: FriendRequestService, val controllerComponents: ControllerComponents)(implicit executionContext: ExecutionContext) extends BaseController {

  // GET FRIEND REQUESTS
  def getFriendRequests: Action[AnyContent] = authAction.async { implicit request =>
    friendRequestService.getFriendRequests(request.userId).map { r =>

      val resp = r.map(ru => ResponseFriendRequestDTO(ru._1.id, ru._1.receiverId, ru._1.senderId, ru._2.username))
      Ok(Json.toJson(resp))
    }
  }

  // SEND FRIEND REQUEST
  def sendFriendRequest(receiverId: Long): Action[AnyContent] = authAction.async { implicit request =>
    friendRequestService.addFriendRequest(request.userId, receiverId).map {
      case Left(err) =>
        err match {
          case err: SameUserIdException => BadRequest(Json.toJson(err))
          case err: AlreadyFriendsException => BadRequest(Json.toJson(err))
          case err: PendingRequestAlreadyExistsException => BadRequest(Json.toJson(err))
          case err: RequestAlreadySentException => BadRequest(Json.toJson(err))
        }
      case Right(r) => Ok(Json.toJson(r))
    }
  }

  // ACCEPT FRIEND REQUEST
  def acceptFriendRequest(requestId: Long): Action[AnyContent] = authAction.async { implicit request =>
    friendRequestService.acceptFriendRequest(request.userId, requestId).map {
      case Left(err) =>
        err match {
          case err :NoSuchRequestException => BadRequest(Json.toJson(err))
        }
      case Right(_) => Ok("Successfully accepted friend request!")
    }
  }

  // REJECT FRIEND REQUEST
  def rejectFriendRequest(requestId: Long): Action[AnyContent] = authAction.async { implicit request =>
    friendRequestService.rejectFriendRequest(request.userId, requestId).map {
      case Left(err) =>
        err match {
          case err: NoSuchRequestException => BadRequest(Json.toJson(err))
        }
      case Right(_) => Ok("Successfully rejected")
    }
  }
}
