package views

import play.api.templates.Html
import scalatags.Text.all._

object DerivationView {
    def derivation(userId: String, rootUrl: String, derivationList: List[String]): Html = {

        //Not used. For reference
        val sideBarDiv =
            div(id := "caps-sidebar", cls := "ui sidebar inverted vertical menu")(
                a(id := "derivations", cls := "item")("Derivations"),
                a(id := "help", cls := "item")("Help"))

        val sHtmObj = Wrapper.main("CAPS") {
            body(
                sideBarDiv,
                div(id := "caps-pusher", cls := "ui pusher OuterLayout")(
                    div(cls := "ui-layout-north MenuBar")(menuBarSemUI),
                    div(cls := "ui-layout-west caps-left")(
                        div(cls := "header")("Tactics")),
                    div(cls := "ui-layout-center caps-right")(
                        topPanelElem,
                        div(cls := "caps-content"),
                        div(cls := "statusbar")(
                            div(cls := "displayIdDiv")("DisplayIdDiv")),
                        semUIGalleryPanel),
                    div(cls := "ui-layout-south")(
                        div(cls := "header")("Bottom Panel"),
                        bottomPanel)))
        }

        new Html(new StringBuilder(sHtmObj.toString))
    }

    val topPanelElem = {
        div(cls := "caps-top")(
            div(cls := "topButton", id := "toggleIdBtn")("Toggle Ids"),
            div(cls := "topButton")(2),
            div(cls := "topButton")(3),
            div(cls := "topButton", id := "optionsBtn")("Options"),
            div(cls := "topButton", id := "resetBtn")("Reset"))
    }

    val bottomPanel = {
        div(cls := "bottomPanel")(
            raw("<!-- This will be populated by a js function on document reload -->"))
    }

    val semUIGalleryPanel = {
        div(id := "semuiGalleryPanel", cls := "ui modal")(
            div(cls := "header")(
                "Gallery"),
            div(cls := "content")(
                raw("""<!--
				div( class="derivationDiv" id := "IntDiv">IntDiv)
				div( class="derivationDiv" id := "IntSqrt">IntSqrt)
			 	-->""")))
    }

    val fileMenu = {
        div(cls := "ui dropdown item")(
            div(cls := "text")("File"),
            i(cls := "dropdown icon"),
            div(cls := "menu")(
                div(id := "caps-menu-new", cls := "item")("New"),
                div(id := "caps-menu-gallery", cls := "item")("Gallery..."),
                div(id := "caps-menu-load-local", cls := "item")("Load from local file ..."),
                div(cls := "divider"),
                div(id := "caps-menu-download-text", cls := "item")("Download derivation (text format)"),
                div(id := "caps-menu-download-bin", cls := "item")("Download derivation (binary format)"),
                div(cls := "divider"),
                div(id := "caps-menu-reset", cls := "item")("Reset")/*,
                div(cls := "divider"),
                div(id := "caps-debug-log", cls := "item")("View Debug Log"),
                div(id := "caps-clear-debug-log", cls := "item")("Clear Debug Log")*/))
    }

    val viewMenu = {
        div(cls := "ui dropdown item")(
            div(cls := "text")("View"),
            i(cls := "dropdown icon"),
            div(cls := "menu")(
                div(id := "caps-menu-minimal-anno", cls := "item")("Minimal Annotations")))
    }

    val derivationsMenu = {
        div(cls := "ui dropdown item")(
            div(cls := "text")("Derivations"),
            i(cls := "dropdown icon"),
            div(cls := "menu"))
    }

    val actionsMenu = {
        div(cls := "ui dropdown item")(
            div(cls := "text")("Actions"),
            i(cls := "dropdown icon"),
            div(cls := "menu")(
                div(id := "caps-menu-set-head-node", cls := "item")("Set selected node as the head node (for branching)"),
                div(id := "caps-menu-delete-sel-node", cls := "item")("Delete selected node")))
    }

    val settingsMenu = {
        div(cls := "ui dropdown item")(
            div(cls := "text")("Settings"),
            i(cls := "dropdown icon"),
            div(cls := "menu")(
                div(id := "caps-menu-preferences", cls := "item")("Preferences")))
    }

    val helpMenu = {
        div(cls := "ui dropdown item")(
            div(cls := "text")("Help"),
            i(cls := "dropdown icon"),
            div(cls := "menu")(
                div(id := "caps-tactic-doc", cls := "item")("Tactic Documentation"),
                div(id := "caps-user-doc", cls := "item")("User Documentation")))
    }

    val userMenu = {
        div(cls := "right ui dropdown item")(
            div(cls := "text")("User"),
            i(cls := "dropdown icon"),
            div(cls := "menu")(
                div(id := "caps-menu-logout", cls := "item")("Logout")))
    }

    val menuBarSemUI =
        div(id := "caps-menubar")(
            div(cls := "ui menu")(
                div(id := "caps-sidebar-menu", cls := "item")(
                    i(cls := "sidebar icon")),
                fileMenu,
                viewMenu,
                actionsMenu,
                settingsMenu,
                helpMenu,
                userMenu))

}




