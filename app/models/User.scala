package models

import java.time.LocalDateTime

case class User(id: Long, username: String, email: String, password: String, imagePath: String)
case class Post(id: Long, userId: Long, userUsername: String, dateTime: LocalDateTime, content: String)
case class FriendRequest(id: Long, senderId: Long, receiverId: Long, status: Int)
case class Friendship(id: Long, userId: Long, friendId: Long)
case class Like(id: Long, userId:Long, postId: Long, status: Int)