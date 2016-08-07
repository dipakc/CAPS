package views

import play.api.templates.Html
import scalatags.Text.all._
import scalatags.Text
import scalatags.Text.tags.{ html => shtml }
import scalatags.Text.tags2.title
import controllers.routes

object ParseTestView {

    def apply(inputHtml: Html): Html = {

        def includeAssetJS(js: String) = {
            script(tpe := "text/javascript", src := routes.Assets.at("javascripts/" + js).toString)
        }

        def includeJS(js: String) = {
            script(tpe := "text/javascript", src := js)
        }

        def includeAssetCSS(css: String) = {
            link(rel := "stylesheet", tpe := "text/css", href := routes.Assets.at("stylesheets/" + css).toString)
        }

        val shortcutIcon = {
            link(rel := "shortcut icon", tpe := "image/png", href := routes.Assets.at("images/favicon.png").toString)
        }

        def toHtml(in: Text.Frag*): Html = {
            //val x: Text.Frag = ???
            //x.render
            new Html(new StringBuilder(in.map(_.render).mkString("\n")))
            //new Html(new StringBuilder(in.toString))
        }

        toHtml(
            raw("<!DOCTYPE html>"),
            shtml(
                head(
                    title("Parser Test"),
                    shortcutIcon,
                    includeAssetCSS("mathquill/mathquill.css"),
                    includeAssetCSS("jquery-ui-1.10.0.custom.css"),
                    includeAssetCSS("progsynthlog.css"),
                    includeAssetCSS("parseTest.css"),
                    includeAssetJS("lib/require.js"),
                    script(
                        """ var jqueryMod = "lib/jquery-1.7.1.min";
                    		require.config({
    			                baseUrl: "/assets/javascripts",
    	    		            shim: {
    	    			            'lib/mathquill/mathquill' :
    	    			                {deps: [jqueryMod]},
    	    		            }
    		                });
    		                require(["parseTest"]);""")),
                body(div(raw(inputHtml.toString())))))
    }

}