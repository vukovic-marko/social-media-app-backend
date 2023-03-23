package utils

case class NoSuchPostException(msg: String = "No such post.") extends Throwable()

case class IncorrectPasswordException(msg: String = "Incorrect old password.") extends Throwable

case class SameUserIdException(msg: String = "Can't send friend request to yourself.") extends Throwable
case class NoSuchRequestException(msg: String = "No such request.") extends Throwable
case class RequestAlreadySentException(msg: String = "Request already sent.") extends Throwable
case class PendingRequestAlreadyExistsException(msg: String = "Pending request from the user already exists.") extends Throwable

case class AlreadyFriendsException(msg: String = "Can't send friend requests to friends.") extends Throwable
case class NotFriendsException(msg: String = "Not friends.") extends Throwable

case class NoSuchUserException(msg: String = "No such user.") extends Throwable

case class AlreadyLikedException(msg: String = "Post already liked.") extends Throwable

case class FileTooLargeException(msg: String = "File too large. Please use files smaller than 5MB.") extends Throwable
case class UnsupportedImageFormatException(msg: String = "Unsupported image format. Please use png or jpeg images.") extends Throwable