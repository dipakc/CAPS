package jsonprinter

import play.api.libs.json.{Json, JsObject, JsNull}

abstract class ClassTV(fields: List[FieldTV]) extends TV {

	override val tvName = "ClassTV"
		
	override def toJSON(): JsObject = {
		val fs = fields.map{_.toJSON}
		
		super.toJSON() ++
		Json.obj( "fields" -> fs)
	}

}