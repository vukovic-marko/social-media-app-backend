package controllers

import auth.AuthAction
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.LikeService
import utils.{AlreadyLikedException, NoSuchPostException, NotFriendsException}
import utils.Formats._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class LikeController @Inject()(authAction: AuthAction, likeService: LikeService, val controllerComponents: ControllerComponents)(implicit executionContext: ExecutionContext) extends BaseController {

  // LIKE POST
  def likePost(friendId: Long, postId: Long): Action[AnyContent] = authAction.async { implicit request =>
    likeService.addPostLike(request.userId, friendId, postId).map {
      case Left(err) =>
        err match {
          // actually not friends, but we shouldn't tell that to the user
          case _: NotFriendsException => BadRequest(Json.toJson(new NoSuchPostException))
          case err: NoSuchPostException => BadRequest(Json.toJson(err))
          case err: AlreadyLikedException => BadRequest(Json.toJson(err))
        }
      case Right(i) => Ok(Json.toJson(i))
    }
  }

  // DISLIKE POST
  def dislikeLikedPost(friendId: Long, postId: Long): Action[AnyContent] = authAction.async { implicit request =>
    likeService.removeLike(request.userId, friendId, postId).map {
      case Left(err) =>
        err match {
          case err: NoSuchPostException => BadRequest(Json.toJson(err))
        }
      case Right(i) => Ok(Json.toJson(i))
    }
  }

  // GET NUMBER OF LIKES
  def numberOfLikes(friendId: Long, postId: Long): Action[AnyContent] = authAction.async { implicit request =>
    likeService.getNumberOfLikes(request.userId, friendId, postId).map {
      case Left(err) =>
        err match {
          case err: NotFriendsException => BadRequest(Json.toJson(err))
          case err: NoSuchPostException => BadRequest(Json.toJson(err))
        }
      case Right(i) => Ok(Json.toJson(i))
    }
  }
}
