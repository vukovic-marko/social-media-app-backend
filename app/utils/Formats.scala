package utils

import models._
import play.api.libs.json.{Json, OFormat}

object Formats {
  implicit val changeUserEmailDtoFormat: OFormat[ChangeUserEmailDTO] = Json.format[ChangeUserEmailDTO]
  implicit val changeUserPasswordDtoFormat: OFormat[ChangeUserPasswordDTO] = Json.format[ChangeUserPasswordDTO]
  implicit val createPostDtoFormat: OFormat[CreatePostDTO] = Json.format[CreatePostDTO]
  implicit val editPostDtoFormat: OFormat[EditPostDTO] = Json.format[EditPostDTO]
  implicit val createUserDtoFormat: OFormat[CreateUserDTO] = Json.format[CreateUserDTO]
  implicit val authenticateUserDtoFormat: OFormat[AuthenticateUserDTO] = Json.format[AuthenticateUserDTO]
  implicit val responseUserDtoFormat: OFormat[ResponseUserDTO] = Json.format[ResponseUserDTO]
  implicit val responsePostDtoFormat: OFormat[ResponsePostDTO] = Json.format[ResponsePostDTO]
  implicit val responseFriendRequestDtoFormat: OFormat[ResponseFriendRequestDTO] = Json.format[ResponseFriendRequestDTO]

  implicit val responsePostWithLikeInformationDtoFormat: OFormat[ResponsePostWithLikeInformationDTO] = Json.format[ResponsePostWithLikeInformationDTO]

  implicit val likeFormat: OFormat[Like] = Json.format[Like]
  implicit val friendshipFormat: OFormat[Friendship] = Json.format[Friendship]
  implicit val friendRequestFormat: OFormat[FriendRequest] = Json.format[FriendRequest]
  implicit val postFormat: OFormat[Post] = Json.format[Post]
  implicit val userFormat: OFormat[User] = Json.format[User]

  implicit val noSuchPostExceptionFormat: OFormat[NoSuchPostException] = Json.format[NoSuchPostException]
  implicit val incorrectPasswordExceptionFormat: OFormat[IncorrectPasswordException] = Json.format[IncorrectPasswordException]
  implicit val sameUserIdExceptionFormat: OFormat[SameUserIdException] = Json.format[SameUserIdException]
  implicit val noSuchRequestExceptionFormat: OFormat[NoSuchRequestException] = Json.format[NoSuchRequestException]
  implicit val requestAlreadySentExceptionFormat: OFormat[RequestAlreadySentException] = Json.format[RequestAlreadySentException]
  implicit val pendingRequestAlreadyExistsExceptionFormat: OFormat[PendingRequestAlreadyExistsException] = Json.format[PendingRequestAlreadyExistsException]
  implicit val alreadyFriendsExceptionFormat: OFormat[AlreadyFriendsException] = Json.format[AlreadyFriendsException]
  implicit val notFriendsExceptionFormat: OFormat[NotFriendsException] = Json.format[NotFriendsException]
  implicit val noSuchUserExceptionFormat: OFormat[NoSuchUserException] = Json.format[NoSuchUserException]
  implicit val alreadyLikedExceptionFormat: OFormat[AlreadyLikedException] = Json.format[AlreadyLikedException]
  implicit val fileTooLargeExceptionFormat: OFormat[FileTooLargeException] = Json.format[FileTooLargeException]
  implicit val unsupportedImageFormatExceptionFormat: OFormat[UnsupportedImageFormatException] = Json.format[UnsupportedImageFormatException]
}
