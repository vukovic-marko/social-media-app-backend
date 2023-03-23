package services

import models.{AuthenticateUserDTO, ChangeUserEmailDTO, ChangeUserPasswordDTO, CreatePostDTO, CreateUserDTO, EditPostDTO}

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import octopus.dsl._
import octopus.syntax._

class ValidationService @Inject() (implicit executionContext: ExecutionContext)  {

  implicit val authenticateUserDtoValidator: Validator[AuthenticateUserDTO] = Validator[AuthenticateUserDTO]
    // USERNAME RULES
    .rule(_.username, (_: String).nonEmpty, "must not be empty")
    .rule(_.username, (_: String).length > 3, "must be longer than 3 characters")
    // PASSWORD RULES
    .rule(_.password, (_: String).nonEmpty, "must not be empty")
    .rule(_.password, (_: String).length > 3, "must be longer than 3 characters")

  implicit val createUserDtoValidator: Validator[CreateUserDTO] = Validator[CreateUserDTO]
    // USERNAME RULES
    .rule(_.username, (_: String).nonEmpty, "must not be empty")
    .rule(_.username, (_: String).length > 3, "must be longer than 3 characters")
    // EMAIL RULES
    .rule(_.email, (_: String).nonEmpty, "must not be empty")
    .rule(_.email, (_: String).contains("@"), "must contain @")
    .rule(_.email, (_: String).split("@").last.contains("."), "must contain . after @")
    // PASSWORD RULES
    .rule(_.password, (_: String).nonEmpty, "must not be empty")
    .rule(_.password, (_: String).length > 3, "must be longer than 3 characters")

  implicit val changeUserEmailDtoValidator: Validator[ChangeUserEmailDTO] = Validator[ChangeUserEmailDTO]
    // EMAIL RULES
    .rule(_.email, (_: String).nonEmpty, "must not be empty")
    .rule(_.email, (_: String).contains("@"), "must contain @")
    .rule(_.email, (_: String).split("@").last.contains("."), "must contain . after @")

  implicit val changeUserPasswordDtoValidator: Validator[ChangeUserPasswordDTO] = Validator[ChangeUserPasswordDTO]
    // PASSWORD RULES
    .rule(_.oldPassword, (_: String).nonEmpty, "must not be empty")
    .rule(_.oldPassword, (_: String).length > 3, "must be longer than 3 characters")
    // PASSWORD RULES
    .rule(_.newPassword, (_: String).nonEmpty, "must not be empty")
    .rule(_.newPassword, (_: String).length > 3, "must be longer than 3 characters")

  implicit val createPostDtoValidator: Validator[CreatePostDTO] = Validator[CreatePostDTO]
    // PASSWORD RULES
    .rule(_.content, (_: String).nonEmpty, "must not be empty")

  implicit val editPostDtoValidator: Validator[EditPostDTO] = Validator[EditPostDTO]
    // PASSWORD RULES
    .rule(_.content, (_: String).nonEmpty, "must not be empty")

  def validateAuthenticateUserDTO(userData: AuthenticateUserDTO): Either[List[(String, String)], AuthenticateUserDTO] = {
    userData.isValid match {
      case true => Right(userData)
      case false => Left(userData.validate.toFieldErrMapping)
    }
  }

  def validateCreateUserDTO(userData: CreateUserDTO): Either[List[(String, String)], CreateUserDTO] = {
    userData.isValid match {
      case true => Right(userData)
      case false => Left(userData.validate.toFieldErrMapping)
    }
  }

  def validateChangeUserEmailDTO(userData: ChangeUserEmailDTO): Either[List[(String, String)], ChangeUserEmailDTO] = {
    userData.isValid match {
      case true => Right(userData)
      case false => Left(userData.validate.toFieldErrMapping)
    }
  }

  def validateChangeUserPasswordDTO(userData: ChangeUserPasswordDTO): Either[List[(String, String)], ChangeUserPasswordDTO] = {
    userData.isValid match {
      case true => Right(userData)
      case false => Left(userData.validate.toFieldErrMapping)
    }
  }

  def validateCreatePostDTO(postData: CreatePostDTO): Either[List[(String, String)], CreatePostDTO] = {
    postData.isValid match {
      case true => Right(postData)
      case false => Left(postData.validate.toFieldErrMapping)
    }
  }

  def validateEditPostDTO(postData: EditPostDTO): Either[List[(String, String)], EditPostDTO] = {
    postData.isValid match {
      case true => Right(postData)
      case false => Left(postData.validate.toFieldErrMapping)
    }
  }
}
