package jsonprinter

import play.api.libs.json.{Json, JsObject, JsNull}

object EnumTV {
	def apply(elements: List[String], selectedElem: Option[String]): EnumTV =
		new EnumTV(elements, selectedElem)
}

class EnumTV(elements: List[String], selectedElem: Option[String]) extends TV {
	override val tvName = "EnumTV"
			
	override def toJSON(): JsObject = {
		val x = selectedElem.getOrElse(" ")
		
		super.toJSON() ++
		Json.obj(
			"elements" -> elements,
			"selectedElem" -> x
		)
	}
}
