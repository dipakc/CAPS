package views

import scalatags.Text.all._
import scalatags.Text
import scalatags.Text.tags.{ html => shtml }
import scalatags.Text.tags2.title

import WrapperDerivations.mainDerivations
import play.api.templates.Html

object DerivationsView {

    def derivations(userId: String, savedDerivations: List[String]): Html = {

        mainDerivations("Derivations | CAPS") {

            div(id := "caps-derivations-container")(
                h1(cls := "ui header")("Saved Derivations"),
                div(id := "caps-derivations-menubar")(
                    div(cls := "ui menu")(
                        div(cls := "ui dropdown item")(
                            div(cls := "text")("Derivations"),
                            i(cls := "dropdown icon"),
                            div(cls := "menu")(
                                div(id := "caps-menu-new", cls := "item")("New"),
                                div(id := "caps-menu-import", cls := "item")("Import from gallery"),
                                div(cls := "divider"))),
                        div(cls := "ui dropdown item")(
                            div(cls := "text")("Actions"),
                            i(cls := "dropdown icon"),
                            div(cls := "menu")(
                                div(id := "caps-menu-rename", cls := "item")("Rename"),
                                div(id := "caps-menu-copy", cls := "item")("Make a copy"),
                                div(id := "caps-menu-download", cls := "item")("Download"),
                                div(id := "caps-menu-delete", cls := "item")("Delete"))),
                        div(cls := "right ui dropdown item")(
                            div(cls := "text")("User"),
                            i(cls := "dropdown icon"),
                            div(cls := "menu")(
                                div(id := "caps-menu-logout", cls := "item")("Logout"))))),
                div(id := "caps-derivations-table")(
                    table(cls := "ui celled table")(
                        thead(
                            tr(th("Name"))),
                        tbody(
                            savedDerivations.map { x =>
                                tr(td(x))
                            }))),
                renameModalHtm)

            div()
        }
    }

    val renameModalHtm =
        div(id := "renameModal", cls := "small ui modal")(
            div(cls := "header")("Rename derivation: Enter new name"),
            div(cls := "content")(
                div(cls := "ui input")(
                    input(tpe := "text", placeholder := "New derivation name"),
                    span(".capstxt"))),
            div(cls := "actions")(
                div(cls := "ui approve button")("Rename"),
                div(cls := "ui cancel button")("Cancel")),
            div(cls := "ui negative message hidden")(
                //i(cls := "close icon"),
                div(cls := "area")("")))
}