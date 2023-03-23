package auth

import play.api.http.HeaderNames
import play.api.mvc.{ActionBuilder, AnyContent, BodyParser, BodyParsers, Request, Result, Results, WrappedRequest}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

case class UserRequest[A](userId: Long, username: String, token: String, request: Request[A]) extends WrappedRequest[A](request)

class AuthAction @Inject()(bodyParser: BodyParsers.Default, jwtUtils: JwtUtils)(implicit ec: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] {

  override def parser: BodyParser[AnyContent] = bodyParser
  override protected def executionContext: ExecutionContext = ec

  private val headerTokenRegex = """Bearer (.+?)""".r

  override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] =
    extractBearerToken(request) map { token =>
      jwtUtils.validateJwt(token) match {
        case Success(claim) => block(UserRequest(jwtUtils.getIdFromJwt(claim), jwtUtils.getUsernameFromJwt(claim), token, request))
        case Failure(t) => Future.successful(Results.Unauthorized(t.getMessage))
      }
    } getOrElse Future.successful(Results.Unauthorized("Unauthorized"))

  private def extractBearerToken[A](request: Request[A]): Option[String] =
    request.headers.get(HeaderNames.AUTHORIZATION) collect {
      case headerTokenRegex(token) => token
    }
}
