package controllers

import auth.JwtUtils
import models.{AuthenticateUserDTO, CreateUserDTO, User}
import utils.Formats._

import javax.inject._
import play.api.mvc._
import services.{AuthenticationService, ValidationService}
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class AuthenticationController @Inject()(jwtUtils: JwtUtils, validationService: ValidationService, authenticationService: AuthenticationService, val controllerComponents: ControllerComponents)(implicit executionContext: ExecutionContext) extends BaseController {

  // LOGIN
  def login(): Action[AuthenticateUserDTO] = Action.async(parse.json[AuthenticateUserDTO]) { implicit request =>
    validationService.validateAuthenticateUserDTO(request.body) match {
      case Right(userData) =>
        authenticationService.checkCredentials(userData.username, userData.password).map {
          case Some(id) => Ok(jwtUtils.createJwt(id, userData.username))
          case None => BadRequest("Username and/or password error")
        }
      case Left(err) => Future.successful(BadRequest(Json.toJson(err)))
    }
  }

  // REGISTER
  def register(): Action[CreateUserDTO] = Action.async(parse.json[CreateUserDTO]) { implicit request =>
      validationService.validateCreateUserDTO(request.body) match {
      case Right(userData) =>
        authenticationService.registerUser(User(0, userData.username, userData.email, userData.password, s"blank-profile-picture.png")) map {
          case Success(i) => Ok(Json.toJson(i))
          case Failure(_) => BadRequest("Username already taken!")
        }
      case Left(err) => Future.successful(BadRequest(Json.toJson(err)))
    }
  }
}
