package jsonprinter

import play.api.libs.json.{Json, JsObject, JsNull}

object ListTV {
	def apply(metaTV: TV, items: List[TV]): ListTV =
		new ListTV(metaTV, items)
}

class ListTV(metaTV: TV, items: List[TV]) extends TV  {
	override val tvName = "ListTV"
		
	override def toJSON(): JsObject =
		super.toJSON() ++
		Json.obj(
			"metaTV" -> metaTV.toJSON(),
			"items" -> items.map(_.toJSON)
		)
}