package jsonprinter

import play.api.libs.json.{Json, JsObject, JsNull}



object TupleTV {
	def apply(item1: TV, item2: TV): TupleTV =
		new TupleTV(item1, item2)
}

class TupleTV(item1: TV, item2: TV) extends TV {

	override val tvName = "TupleTV"
		
	override def toJSON(): JsObject = {
		super.toJSON() ++
		Json.obj( "item1" -> item1.toJSON,
				"item2" -> item2.toJSON)
	}

}