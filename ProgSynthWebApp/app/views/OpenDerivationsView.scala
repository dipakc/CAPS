package views

import scalatags.Text.all._
import scalatags.Text
import scalatags.Text.tags.{ html => shtml }
import scalatags.Text.tags2.title

import play.api.templates.Html

object OpenDerivationsView {

    def openDerivations(userId: String, derivationList: List[String]): Html = {
        val menuBar = {
            div(id := "psMenuBar", cls := "yui3-menu yui3-menu-horizontal yui3-loading")(
                div(cls := "yui3-menu-content")(
                    ul(cls := "first-of-type")(
                        li(
                            a(cls := "yui3-menu-label", href := "#")("File"),
                            div(id := "file", cls := "yui3-menu")(
                                div(cls := "yui3-menu-content")(
                                    ul(
                                        li(cls := "yui3-menuitem", id := "newMenu")(
                                            a(cls := "yui3-menuitem-content", href := "#")("New")),
                                        li(cls := "yui3-menuitem", id := "galleryMenu")(
                                            a(cls := "yui3-menuitem-content", href := "#")("Gallery...")),
                                        li(cls := "yui3-menuitem", id := "loadLocalMenu")(
                                            a(cls := "yui3-menuitem-content", href := "#")("Load from local file...")),
                                        li(cls := "yui3-menuitem", id := "downloadDerivationMenu")(
                                            a(cls := "yui3-menuitem-content", href := "#")("Download Derivation")),
                                        li(cls := "yui3-menuitem", id := "resetMenu")(
                                            a(cls := "yui3-menuitem-content", href := "#")("Reset")))))),

                        li(cls := "UserInfo")(
                            a(cls := "yui3-menu-label")(userId),
                            div(id := "UserNameId", cls := "yui3-menu")(
                                div(cls := "yui3-menu-content")(
                                    ul(
                                        li(cls := "yui3-menuitem", id := "logoutMenu")(
                                            a(cls := "yui3-menuitem-content", href := "#")("Logout")))))))))

        }

        val retVal = Wrapper.main("ProgSynth Title") {

            div(cls := "ui-layout-center MainLayout")(
                div("Open Derivations:")(

                    if (derivationList.isEmpty)
                        div("No open derivations")
                    else {
                        derivationList.map { x =>
                            div(a(href := "derivation/" + x)(x))
                        }
                    }),
                br(),
                div(cls := "NewDerivationLink")(
                    a(href := "#")("Create new Derivation")))
        }

        new Html(new StringBuilder(retVal.toString))
    }

}