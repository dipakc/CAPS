package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views._
import models.User

object Authentication extends Controller {

    /**
     * Login Form.
     */
    val loginForm = Form{
        tuple (
            "email" -> text,
            "password" -> text)
        .verifying ("Invalid email or password", result =>
            result match {
                case (email, password) =>
                    User.authenticate(email, password).isDefined
            })
    }

    /**
     * Login page.
     */
    def login = Action { implicit request =>
        val submitCall = routes.Authentication.authenticate
        //Ok(html.login(loginForm, submitCall))
        Ok(views.LoginView.login(loginForm, submitCall))
    }

    /**
     * Logout and clean the session.
     */
    def logout = Action {
	    Redirect(routes.Authentication.login).withNewSession.flashing(
	      "success" -> "You've been logged out"
	    )
    }

    /**
     * Handle login form submission.
     */
    def authenticate = Action { implicit request =>
        loginForm.bindFromRequest.fold(
            formWithErrors => BadRequest(views.LoginView.login(formWithErrors, routes.Authentication.authenticate)),
            user => Redirect(routes.Application.derivations())
                    .withSession("email" -> user._1))
    }

}