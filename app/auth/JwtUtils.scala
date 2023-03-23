package auth

import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtJson}
import play.api.Configuration
import play.api.libs.json.{JsObject, Json}

import java.time.Clock
import javax.inject.Inject
import scala.util.Try

class JwtUtils @Inject() (config: Configuration) {
  implicit val clock: Clock = Clock.systemUTC

  private val secret = config.get[String]("token.secret")
  private val expiresIn = config.get[Int]("token.expiresIn")

  def validateJwt(token: String): Try[JsObject] = for {
    claims <- JwtJson.decodeJson(token, secret, Seq(JwtAlgorithm.HS256))
  } yield claims

  def getIdFromJwt[A](jsObject: JsObject): Long = {
    (jsObject\"data"\"id").get.toString().toLong
  }

  def getUsernameFromJwt[A](jsObject: JsObject): String = {
    (jsObject\"data"\"username").get.toString().replace("\"", "")
  }

  def createJwt(id: Long, username: String): String = {
    val claim = Json.obj(
      "data" -> Json.obj(
        "id" -> id,
        "username" -> username
      )
    ).toString
    JwtJson.encode(JwtClaim(claim).issuedNow.expiresIn(expiresIn), secret, JwtAlgorithm.HS256)
  }
}
