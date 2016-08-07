package jsonprinter

import play.api.libs.json.{Json, JsObject, JsNull}

object IntegerTV {
	
	def apply(value: Option[Int]): IntegerTV = new IntegerTV(value)
	
	def apply(value: Int): IntegerTV = new IntegerTV(Some(value))
	
	def getEmpty() = new IntegerTV(None)
}

class IntegerTV(value: Option[Int]) extends PrimitiveTV {
	override val tvName = "IntegerTV"
		
	override def toJSON(): JsObject = {
		val x = value.map(_.toString).getOrElse("")
		
		super.toJSON() ++
		Json.obj(
			"value" -> x)
	}
}