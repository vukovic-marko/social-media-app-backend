package controllers

import auth.AuthAction
import models.{CreatePostDTO, EditPostDTO, ResponsePostDTO}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.{PostService, ValidationService}
import utils.{NoSuchPostException, NoSuchUserException, NotFriendsException}
import utils.Formats._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PostController @Inject()(authAction: AuthAction, postService:PostService, validationService: ValidationService, val controllerComponents: ControllerComponents)(implicit executionContext: ExecutionContext) extends BaseController {

  // GET USER POSTS
  def profilePosts(): Action[AnyContent] = authAction.async { implicit request =>
    postService.getPostsById(request.userId).map { r =>
      Ok(Json.toJson(r))
    }
  }

  // CREATE NEW POST
  def post(): Action[CreatePostDTO] = authAction.async(parse.json[CreatePostDTO]) { implicit request =>
    validationService.validateCreatePostDTO(request.request.body) match {
      case Right(postData) =>
        postService.addPost(request.userId, request.username, postData.content) map { i =>
          Ok(Json.toJson(i))
        }
      case Left(err) => Future.successful(BadRequest(Json.toJson(err)))
    }
  }

  // EDIT POST CONTENT
  def editPost(postId: Long): Action[EditPostDTO] = authAction.async(parse.json[EditPostDTO]) { implicit request =>
    validationService.validateEditPostDTO(request.request.body) match {
      case Right(postData) =>
        postService.changePostContent(request.userId, postId, postData.content) map {
          case Left(err) =>
            err match {
              case p: NoSuchPostException => BadRequest(Json.toJson(p))
            }
          case Right(_) => Ok("Post successfully changed!")
        }
      case Left(err) => Future.successful(BadRequest(Json.toJson(err)))
    }
  }

  // GET POSTS FROM FRIEND WITH ID friendId
  def getFriendPosts(friendId: Long): Action[AnyContent] = authAction.async { implicit request =>
    postService.getFriendPosts(request.userId, friendId).map {
      case Left(err) =>
        err match {
          case err: NotFriendsException => BadRequest(Json.toJson(err))
          case err: NoSuchUserException => BadRequest(Json.toJson(err))
        }
      case Right(p) => Ok(Json.toJson(p))
    }
  }

  // GET POSTS FROM ALL FRIENDS
  def getFriendsPosts: Action[AnyContent] = authAction.async { implicit request =>
    postService.getFriendsPosts(request.userId).map { p => Ok(Json.toJson(p))
    }
  }
}
