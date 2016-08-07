package jsonprinter

import play.api.libs.json.{Json, JsObject, JsNull}

object PSTypeTV {
	def apply(selectedElem: Option[String]) = new PSTypeTV(selectedElem)
	
	def getEmpty() = new PSTypeTV(None)
}

class PSTypeTV(selectedElem: Option[String]) extends EnumTV(List("Int", "Bool", "ArrayInt", "ArrayBool"), selectedElem) {
	
	override val tvName = "PSTypeTV"
}