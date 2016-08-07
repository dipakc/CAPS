package views

import scalatags.Text.all._
import scalatags.Text
import scalatags.Text.tags.{ html => shtml }
import scalatags.Text.tags2.title
import play.api.templates.Html
import controllers.routes

object WrapperDerivations {
    def includeAssetCSSOrigin(css: String) = {
        link(rel := "stylesheet", href := routes.Assets.at(css).toString)
    }

    def shortcutIcon = {
        link(rel := "shortcut icon", tpe := "image/png", href := routes.Assets.at("images/favicon.png").toString)
    }

    def includeJS(js: String) = {
        script(src := js)
    }

    def mainDerivations(titleStr: String)(content: Text.Frag): Html = {

        val sObj =
            "<!DOCTYPE html>" +
                shtml(
                    head(
                        title(titleStr),
                        shortcutIcon,
                        includeAssetCSSOrigin("javascripts/lib/Semantic-UI-CSS/semantic.min.css"),
                        includeAssetCSSOrigin("stylesheets/toastr/toastr.css"),
                        includeAssetCSSOrigin("stylesheets/SavedDerivations.css"),
                        includeAssetCSSOrigin("stylesheets/tmpdev.css"),
                        includeJS(routes.Application.jsRoutes().url),
                        script(
                            "data-main".attr := routes.Assets.at("javascripts/" + "datamain-derivations").toString,
                            src := routes.Assets.at("javascripts/" + "lib/require.js").toString)),
                    body(content))
        new Html(new StringBuilder(sObj.toString))
    }
}