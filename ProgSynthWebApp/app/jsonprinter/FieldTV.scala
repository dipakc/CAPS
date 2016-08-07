package jsonprinter

import play.api.libs.json.{Json, JsObject, JsNull}

object FieldTV {
	
	def apply(fname: String, displayName: String, ftv: TV): FieldTV =
		new FieldTV(fname, displayName, ftv)
}

class FieldTV(fname: String, displayName: String, ftv: TV) extends TV {
	
	override val tvName = "FieldTV"
	
	override def toJSON(): JsObject =
		super.toJSON() ++
		Json.obj(
			"fname" -> fname,
			"displayName" -> displayName,
			"ftv" -> ftv.toJSON()
		)
}