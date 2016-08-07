package jsonprinter

import play.api.libs.json.JsObject
import play.api.libs.json.Json

abstract class AbstractTV(subTVNames: List[String], concreteTV: TV) extends TV {
	
	override val tvName = "AbstractTV"
		
	override def toJSON(): JsObject = {
		Json.obj( "tvName" -> tvName,
				"subTVNames" -> subTVNames,
				"concreteTV" -> concreteTV.toJSON())
	}
	
}