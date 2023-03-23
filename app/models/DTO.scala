package models

import java.time.LocalDateTime

case class AuthenticateUserDTO(username: String, password: String)
case class CreateUserDTO(username: String, password: String, email: String)
case class ChangeUserEmailDTO(email: String)
case class ChangeUserPasswordDTO(oldPassword: String, newPassword: String)
case class CreatePostDTO(content: String)
case class EditPostDTO(content: String)

case class ResponseUserDTO(id: Long, username: String, email: String, imagePath: String)
case class ResponsePostDTO(postId: Long, authorId: Long, authorUsername: String, dateTime: LocalDateTime, content: String)
case class ResponseFriendRequestDTO(requestId: Long, userId: Long, senderId: Long, senderUsername: String)

case class ResponsePostWithLikeInformationDTO(postId: Long, authorId: Long, authorUsername: String, authorImagePath: String, dateTime: LocalDateTime, content: String, numberOfLikes: Int, isLiked: Int)