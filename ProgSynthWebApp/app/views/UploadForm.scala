package views
import play.api.templates.Html
import scalatags.Text.all._
import scalatags.Text
import scalatags.Text.tags.{ html => shtml }
import scalatags.Text.tags2.title
import controllers.routes
import views.html.helper
import play.api.data.Form
import play.api.mvc.Call
import play.api.mvc.Flash

object UploadFormView {

    def apply()(implicit flash: Flash): Html = {

        h1("Load derivation from a local file")

        val f = helper.form(action = routes.Application.upload, 'enctype -> "multipart/form-data") {
            val z = div(
                input(tpe := "file", name := "derivation"),
                p(
                    input( tpe := "submit", value := "Load file"),
                    span(cls := "warning")("Warning: The current derivation will be lost.")
                ),
                flash.get("error").map { message =>
                    p( cls :="error")(
                        //font( size := "3", color := "red")("Error: " + message)
                        "Error:" + message
                    )
                },
                br,
            	p(a( href := "/")("Back to active derivation"))
        	)

        	new Html(new StringBuilder(z.toString))
        }

        new Html(new StringBuilder(f.toString))
    }
}