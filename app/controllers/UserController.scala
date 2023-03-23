package controllers

import auth.AuthAction
import models.{ChangeUserEmailDTO, ChangeUserPasswordDTO, ResponseUserDTO}
import play.api.libs.Files
import utils.Formats._

import javax.inject._
import play.api.mvc._
import services.{AuthenticationService, UserService, ValidationService}
import play.api.libs.json._
import utils._

import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class UserController @Inject()(authAction: AuthAction, userService: UserService, validationService: ValidationService, authenticationService: AuthenticationService, val controllerComponents: ControllerComponents)(implicit executionContext: ExecutionContext) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  // GET USER INFORMATION
  def profile(): Action[AnyContent] = authAction.async { implicit request =>
    userService.getUserById(request.userId).map {
      case Some(u) =>
        val resp = ResponseUserDTO(u.id, u.username, u.email, u.imagePath)
        Ok(Json.toJson(resp))
      case None => BadRequest("Bad Request")
    }
  }

  // SEARCH FOR USER BY USERNAME
  def search(username: String): Action[AnyContent] = authAction.async { implicit request =>
    userService.searchByUsername(username).map { r =>

      val resp = r.map(u => ResponseUserDTO(u.id, u.username, u.email, u.imagePath))
      Ok(Json.toJson(resp))
    }
  }

  // CHANGE EMAIL
  def email(): Action[ChangeUserEmailDTO] = authAction.async(parse.json[ChangeUserEmailDTO]) { implicit request =>
    validationService.validateChangeUserEmailDTO(request.request.body) match {
      case Right(userData) =>
            userService.changeUserEmail(request.userId, userData.email).map { _ =>
              Ok("Ok")
            }
      case Left(err) => Future.successful(BadRequest(Json.toJson(err)))
    }
  }

  // CHANGE PASSWORD
  def password(): Action[ChangeUserPasswordDTO] = authAction.async(parse.json[ChangeUserPasswordDTO]) { implicit request =>
    validationService.validateChangeUserPasswordDTO(request.request.body) match {
      case Right(userData) =>
            authenticationService.changeUserPassword(request.userId, userData.oldPassword, userData.newPassword).map {
              case Left(err) =>
                err match {
                  case err: NoSuchUserException => BadRequest(Json.toJson(err))
                  case err: IncorrectPasswordException => BadRequest(Json.toJson(err))
                }
              case Right(_) => Ok("Ok")
            }
      case Left(err) => Future.successful(BadRequest(Json.toJson(err)))
    }
  }

  // CHANGE PROFILE PICTURE
  def profilePicture(): Action[MultipartFormData[Files.TemporaryFile]] = authAction.async(parse.multipartFormData) { implicit request =>
    request.body
      .file("picture")
      .map{ picture =>
        userService.setProfilePicture(request.userId, picture).map {
          case Left(err) =>
            err match {
              case err: UnsupportedImageFormatException => BadRequest(Json.toJson(err))
              case err: FileTooLargeException => BadRequest(Json.toJson(err))
            }
          case Right(n) => Ok(Json.toJson(n))
        }
      }
      .getOrElse(Future.successful(BadRequest("No file in request body")))
  }

  // GET USER FRIENDS
  def getFriends: Action[AnyContent] = authAction.async { implicit request =>
    userService.getFriends(request.userId).map { r =>

      val resp = r.map(u => ResponseUserDTO(u.id, u.username, u.email, u.imagePath))
      Ok(Json.toJson(resp))
    }
  }
}
