package dao

import models.{FriendRequest, Friendship, Like, Post, ResponsePostWithLikeInformationDTO, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.{GetResult, JdbcProfile}

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class UserDAO @Inject() (protected  val dbConfigProvider: DatabaseConfigProvider)(implicit  executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]  {
  import profile.api._

  private val Users = TableQuery[UsersTable]
  private val Posts = TableQuery[PostsTable]
  private val FriendRequests = TableQuery[FriendRequestTable]
  private val Friendships = TableQuery[FriendshipTable]
  private val Likes = TableQuery[LikeTable]

  implicit val queryToUserResult = GetResult(r => User(r.<<, r.<<, r.<<, r.<<, r.<<))
  implicit val queryToResponsePostWithLikeInformationDtoResult = GetResult(r => ResponsePostWithLikeInformationDTO(r.<<, r.<<, r.<<, r.<<, LocalDateTime.parse(r.<<), r.<<, r.<<, r.<<))

  def allFriendRequests(): Future[Seq[FriendRequest]] = db.run(FriendRequests.result)
  def allFriendships(): Future[Seq[Friendship]] = db.run(Friendships.result)
  def allUsers(): Future[Seq[User]] = db.run(Users.result)
  def allPosts(): Future[Seq[Post]] = db.run(Posts.sortBy(_.dateTime.desc).result)
  def allLikes(): Future[Seq[Like]] = db.run(Likes.result)

  // USER

  def findById(id: Long): Future[Option[User]] = db.run {
    Users.filter(_.id === id).result.headOption
  }

  def findByUsername(username: String): Future[Option[User]] = db.run {
    Users.filter(_.username === username).result.headOption
  }

  def searchUsersByUsername(username: String): Future[Seq[User]] = db.run {
    Users
      .filter(_.username like s"$username%")
      .result
  }

  // POSTS

  def findPostsById(id: Long): Future[Seq[Post]] = db.run {
    Posts
      .filter(_.userId === id)
      .sortBy(p => p.dateTime.desc)
      .result
  }

  def getFriendPostsById(id: Long, friendId: Long): Future[Seq[ResponsePostWithLikeInformationDTO]] = db.run {
    sql"""
         select
            post.id,
            post.user_id,
            post.user_username,
            friend.image_path,
            post.date_time,
            post.content,
            sum(case when status = 1 then 1 else 0 end) as number_of_likes,
            exists(select 1 from like_post where like_post.post_id = post.id and like_post.user_id = $id and like_post.status = 1 limit 1) as liked
          from post
          join user as friend on post.user_id = friend.id
          left outer join like_post as likes on post.id = likes.post_id
          where post.user_id = $friendId
          group by post.id
          order by post.date_time desc;
       """.as[ResponsePostWithLikeInformationDTO]
  }

  def findPostByPostIdUserId(postId: Long, userId: Long): Future[Option[Post]] = db.run {
    Posts.filter(_.id === postId).filter(_.userId === userId).result.headOption
  }

  // FRIEND REQUESTS

  def findFriendRequestById(requestId: Long): Future[Option[FriendRequest]] = db.run {
    FriendRequests.filter(_.id === requestId).result.headOption
  }

  def findPendingFriendRequestBySenderReceiver(senderId: Long, receiverId: Long): Future[Option[FriendRequest]] = db.run {
    FriendRequests.filter(_.senderId === senderId).filter(_.receiverId === receiverId).filter(_.status === 0).result.headOption
  }

  def findFriendRequestByReceiver(id: Long): Future[Seq[(FriendRequest, User)]] = {
    val query = for {
      (friendRequest, user) <- FriendRequests.filter(_.receiverId === id).filter(_.status === 0).join(Users).on(_.senderId === _.id)
    } yield (friendRequest, user)
    db.run(query.result)
  }

  // FRIENDS

  def findFriendshipByUserIdFriendId(userId: Long, friendId: Long): Future[Option[Friendship]] =  db.run {
    Friendships.filter(_.userId === userId).filter(_.friendId === friendId).result.headOption
  }

  def findFriendsById(id: Long): Future[Vector[User]] = db.run {
    sql"""
         SELECT friend.id, friend.username, friend.email, friend.password, friend.image_path  FROM user
          join friendship on user.id = friendship.user_id
          join user as friend on friend_id = friend.id
          where user.id = $id;
       """.as[User]
  }

  def getFriendsPostsById(id: Long): Future[Vector[ResponsePostWithLikeInformationDTO]] = db.run {
    sql"""
       select
        friendsPosts.id,
        friendsPosts.user_id,
        friendsPosts.user_username,
        friend.image_path,
        friendsPosts.date_time,
        friendsPosts.content,
        sum(case when status = 1 then 1 else 0 end) as number_of_likes,
        exists(select 1 from like_post where like_post.post_id = friendsPosts.id and like_post.user_id = $id and like_post.status = 1 limit 1) as liked
      from user
      join friendship on user.id = friendship.user_id
      join user as friend on friendship.friend_id = friend.id
      join post as friendsPosts on friend_id = friendsPosts.user_id
      left outer join like_post as likes on friendsPosts.id = likes.post_id
      where user.id = $id
      group by friendsPosts.id
      order by friendsPosts.date_time desc;
       """.as[ResponsePostWithLikeInformationDTO]
  }

  // LIKES

  def findLikeByUserIdPostId(userId: Long, postId: Long): Future[Option[Like]] = db.run {
    Likes.filter(_.userId === userId).filter(_.postId === postId).result.headOption
  }

  def numberOfLikes(postId: Long): Future[Int] = db.run {
    Likes
      .filter(_.postId === postId)
      .filter(_.status === 1).length.result
  }

  // INSERTS

  def insert(user: User) : Future[Try[Long]] = db.run((Users returning Users.map(_.id) += user).asTry)
  def insert(post: Post) : Future[Long] = db.run(Posts returning Posts.map(_.id) += post)
  def insert(friendRequest: FriendRequest): Future[Long] = db.run(FriendRequests returning FriendRequests.map(_.id) += friendRequest)
  def insert(friendship: Friendship): Future[Long] = db.run(Friendships returning Friendships.map(_.id) += friendship)
  def insert(like: Like): Future[Long] = db.run(Likes returning Likes.map(_.id) += like)

  // UPDATES

  def updateEmail(id: Long, email: String): Future[Int] = {
    db.run(
      Users.filter(_.id === id)
        .map(u => u.email)
        .update(email)
    )
  }

  def updatePassword(id: Long, newPassword: String): Future[Int] = {
    db.run(
      Users.filter(_.id === id)
        .map(u => u.password)
        .update(newPassword)
    )
  }

  def updatePost(id: Long, postId: Long, content: String): Future[Int] = {
    db.run(
      Posts
        .filter(_.id === postId)
        .filter(_.userId === id)
        .map(p => p.content)
        .update(content)
    )
  }

  def editProfileImage(id: Long, imagePath: String): Future[Int] = {
    db.run(
      Users
        .filter(_.id === id)
        .map(u => u.imagePath)
        .update(imagePath)
    )
  }

  def respondToFriendRequest(id: Long, requestId: Long, status: Int): Future[Int] = db.run {
    FriendRequests
      .filter(_.id === requestId)
      .filter(_.receiverId === id)
      .filter(_.status === 0)
      .map(r => r.status)
      .update(status)
  }

  def updateLike(id: Long, postId: Long): Future[Int] = {
    db.run(
      Likes
        .filter(_.userId === id)
        .filter(_.postId === postId)
        .filter(_.status === 0)
        .map(p => p.status)
        .update(1)
    )
  }

  def dislikeLikedPost(id: Long, postId: Long): Future[Int] = db.run {
    Likes
      .filter(_.userId === id)
      .filter(_.postId === postId)
      .filter(_.status === 1)
      .map(r => r.status)
      .update(0)
  }

  private class UsersTable(tag: Tag) extends Table[User](tag, "USER") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def username = column[String]("USERNAME", O.Unique)
    def email = column[String]("EMAIL")
    def password = column[String]("PASSWORD")
    def imagePath = column[String]("IMAGE_PATH")

    def * = (id, username, email, password, imagePath) <> (User.tupled, User.unapply)
  }

  private class PostsTable(tag: Tag) extends Table[Post](tag, "POST") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def userId = column[Long]("USER_ID")
    def userUsername = column[String]("USER_USERNAME")
    def dateTime = column[LocalDateTime]("DATE_TIME")
    def content = column[String]("CONTENT")

    def user = foreignKey("user_fk", userId, Users)(_.id)
    def username = foreignKey("username_fk", userUsername, Users)(_.username)

    def * = (id, userId, userUsername, dateTime, content) <> (Post.tupled, Post.unapply)
  }

  private class FriendRequestTable(tag: Tag) extends Table[FriendRequest](tag, "FRIEND_REQUEST") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def senderId = column[Long]("SENDER_ID")
    def receiverId = column[Long]("RECEIVER_ID")
    def status = column[Int]("STATUS")

    def sender = foreignKey("sender_fk", senderId, Users)(_.id)
    def receiver = foreignKey("receiver_fk", receiverId, Users)(_.id)

    def * = (id, senderId, receiverId, status) <> (FriendRequest.tupled, FriendRequest.unapply)
  }

  private class FriendshipTable(tag: Tag) extends Table[Friendship](tag, "FRIENDSHIP") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def userId = column[Long]("USER_ID")
    def friendId = column[Long]("FRIEND_ID")

    def user = foreignKey("user_fk", userId, Users)(_.id)
    def friend = foreignKey("friend_fk", friendId, Users)(_.id)

    def * = (id, userId, friendId) <> (Friendship.tupled, Friendship.unapply)
  }

  private class LikeTable(tag: Tag) extends Table[Like](tag, "LIKE_POST") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def userId = column[Long]("USER_ID")
    def postId = column[Long]("POST_ID")
    def status = column[Int]("STATUS")

    def user = foreignKey("user_fk", userId, Users)(_.id)
    def post = foreignKey("post_fk", postId, Posts)(_.id)

    def * = (id, userId, postId, status) <> (Like.tupled, Like.unapply)
  }
}
