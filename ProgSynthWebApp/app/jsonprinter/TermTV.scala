package jsonprinter

import play.api.libs.json.{Json, JsObject, JsNull}
import progsynth.types.Term
import models.mqprinter.MQPrinter.mqprintTerm0

object TermTV {
	
	def apply(value: Option[String]): TermTV =
		new TermTV(value)

	def apply(value: String): TermTV =
		new TermTV(Some(value))

	def getEmpty(): TermTV =
		new TermTV(None)
	
	def apply(value: Term): TermTV =
		new TermTV(Some(mqprintTerm0(value).toString))
}

class TermTV(value: Option[String]) extends TV {
	override val tvName = "TermTV"
		
	override def toJSON(): JsObject =
		super.toJSON() ++
		Json.obj(
			"value" -> value.getOrElse[String]("")
		)
		
}