package jsonprinter

import play.api.libs.json.{Json, JsObject, JsNull}
import progsynth.types.TermBool
import models.mqprinter.MQPrinter._

object TermBoolTV {
	
	def apply(value: TermBool): TermBoolTV = {
		new TermBoolTV(Some(mqprintTerm0(value)))
	}
	
	def apply(value: Option[String]): TermBoolTV =
		new TermBoolTV(value)

	def getEmpty(): TermBoolTV =
		new TermBoolTV(None)
}

class TermBoolTV(value: Option[String]) extends TV {
	override val tvName = "TermBoolTV"
		
	override def toJSON(): JsObject =
		super.toJSON() ++
		Json.obj(
			"value" -> value.getOrElse[String]("")
		)
		
}