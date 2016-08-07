package jsonprinter

import play.api.libs.json.{Json, JsObject, JsNull}

object StringTV {
	
	def apply(value: Option[String]): StringTV = new StringTV(value)
	
	def apply(value: String): StringTV = new StringTV(Some(value))
	
	def getEmpty() = new StringTV(None)
}

class StringTV(value: Option[String]) extends PrimitiveTV {
	override val tvName = "StringTV"
		
	override def toJSON(): JsObject = {
		val x = value.getOrElse("")
		
		super.toJSON() ++
		Json.obj(
			"value" -> x)
	}
}