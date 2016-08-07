package jsonprinter

import play.api.libs.json.{Json, JsObject, JsNull}

abstract class TV {

	val tvName: String

	def toJSON(): JsObject =
		Json.obj("tvName" -> tvName)

}