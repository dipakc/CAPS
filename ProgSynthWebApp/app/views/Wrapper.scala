package views
import scalatags.Text.all._
import scalatags.Text
import scalatags.Text.tags.{ html => shtml }
import scalatags.Text.tags2.title
import controllers.routes
import play.api.templates.Html

object Wrapper {

    def main(titleStr: String)(content: Text.Frag) = {

        def includeAssetJS(js: String) = {
            val url = routes.Assets.at("javascripts/" + js).toString()
            script(src := url)
        }

        def includeJS(js: String) = {
            script(src := js)
        }

        def includeAssetCSS(css: String) = {
            val url = routes.Assets.at("stylesheets/" + css).toString()
            link(rel := "stylesheet", tpe := "text/css", href := url)
        }

        def includeAssetCSSOrigin(css: String) = {
            val url = routes.Assets.at(css).toString()
            link(rel := "stylesheet", tpe := "text/css", href := url)
        }

        val shortcutIcon = {
            val url = routes.Assets.at("images/favicon.png").toString()
            link(rel := "shortcut icon", tpe := "image/png", href := url)
        }

        val url1 = routes.Assets.at("javascripts/" + "datamain").toString
        val url2 = routes.Assets.at("javascripts/" + "lib/require.js").toString


        val ret: Text.Frag = /*"<!DOCTYPE html>" +*/
            shtml(
                head(
                    title(titleStr),
                    shortcutIcon,
                    includeAssetCSS("mathquill/mathquill.css"),
                    includeAssetCSS("jquery-ui-1.10.0.custom.css"),
                    includeAssetCSS("psweb.jquery.layout2.css"),
                    includeAssetCSSOrigin("javascripts/lib/Semantic-UI-CSS/semantic.min.css"),
                    includeAssetCSSOrigin("stylesheets/tmpdev.css"),
                    includeAssetCSS("progsynthlog.css"),
                    includeAssetCSS("toastr/toastr.css"),
                    includeJS(routes.Application.jsRoutes().url),
                    script("data-main".attr := url1, src := url2)),
                content)
        toHtml(ret)

    }

    def toHtml(in: Text.Frag ): Html = {
        new Html(new StringBuilder(in.toString))
    }
}