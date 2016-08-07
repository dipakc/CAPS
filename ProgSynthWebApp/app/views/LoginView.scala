package views

import play.api.data.Form
import play.api.mvc.Call
import play.api.mvc.Flash
import scalatags.Text.all._
import scalatags.Text
import scalatags.Text.tags.{ html => shtml }
import scalatags.Text.tags2.title
import views.html.helper
import play.api.templates.Html

object LoginView {
    def loginOld(form: Form[(String, String)], submitCall: Call)(implicit flash: Flash) = {

        Wrapper.main("CAPS Login") {
            val f = helper.form(submitCall) {

                val z = div(
                    form.errors.map { error =>
                        p(cls := "error")(error.message)
                    },
                    flash.get("success").map { message =>
                        p(cls := "success")(message)
                    },
                    p(
                        input(tpe := "email", name := "email", placeholder := "Email", id := "email", value := form("email").value.getOrElse(""))),
                    p(
                        input(tpe := "password", name := "password", id := "password", placeholder := "Password")),
                    p(
                        button(tpe := "submit", id := "loginbutton")("Login")))

                new Html(new StringBuilder(z.toString))

            }

            body(
                h1("CAPS Login"),
                p("Please provide your credentials."),
                raw(f.toString))
        }
    }

    def login(form: Form[(String, String)], submitCall: Call)(implicit flash: Flash) = {

        Wrapper.main("CAPS Login") {
            body( id := "loginpage",
                div(cls := "ui middle aligned center aligned grid",
                    div( cls := "column",
                        h1("CAPS"),
                        h3("Login."),
                        raw{
                            (helper.form(submitCall) {
                                scalaTagToHtml(div( id := "formcontent", cls := "ui large form",
                                    div( cls := "ui stacked segment",
                                        div( cls := "field",
                                            div( cls := "ui left icon input",
                                                i( cls := "user icon" ),
                                                input(tpe := "text", name := "email",
                                                        placeholder := "E-mail address", id := "email",
                                                        value := form("email").value.getOrElse("")))),
                                        div( cls := "field",
                                            div( cls := "ui left icon input",
                                                i( cls := "lock icon" ),
                                                input(tpe := "password", name := "password", id := "password", placeholder := "Password"))),
                                        button( tpe:= "submit", cls := "ui fluid large teal submit button", id := "loginbutton", "Login")),
                                    form.errors.map { error =>
                                        div(cls := "ui error message", error.message)
                                    },
                                    flash.get("success").map { message =>
                                        p(cls := "ui message", message)
                                    }))
                            }).toString
                    }))
            )
        }
    }

    private def scalaTagToHtml(in: Text.TypedTag[String]): Html = {
        new Html(new StringBuilder(in.toString))
    }


}