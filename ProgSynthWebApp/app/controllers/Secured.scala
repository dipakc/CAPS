package controllers

import play.api.mvc._
import play.api.mvc.Results._
import models.User

/**
 * Provide security features
 */
trait Secured {

    /**
     * Retrieve the connected user's email
     */
    private def email(request: RequestHeader): Option[String] = request.session.get("email")

    /**
     * Not authorized, forward to login
     */
    private def onUnauthorized(request: RequestHeader) = {
        Results.Redirect(routes.Authentication.login)
    }

    /**
     * Action for authenticated users.
     */
    def IsAuthenticated(f: => String => Request[AnyContent] => Result) = {
        Security.Authenticated(email, onUnauthorized) { user =>
            Action(request => f(user)(request))
        }
    }

    //-----------------------
    //Source: Play documentation
    case class AuthenticatedRequest[A](
        user: User, private val request: Request[A]) extends WrappedRequest(request)

    def Authenticated[A](p: BodyParser[A])(f: AuthenticatedRequest[A] => Result) = {
        Action(p) { request =>
            val result = for {
                id <- request.session.get("email")
                user <- User.findByEmail(id)
            } yield f(AuthenticatedRequest(user, request))
            result getOrElse Results.Redirect(routes.Authentication.login)
        }
    }

    // Overloaded method to use the default body parser
    import play.api.mvc.BodyParsers._

    def Authenticated(f: AuthenticatedRequest[AnyContent] => Result): Action[AnyContent] = {
        Authenticated(parse.anyContent)(f)
    }

    //---------------------
}