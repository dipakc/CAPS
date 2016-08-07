package jsonprinter

import play.api.libs.json.{Json, JsObject, JsNull}
import progsynth.types.Term
import models.mqprinter.MQPrinter.mqprintTerm0

object VarTV {
	
	def apply(value: Option[String]): VarTV =
		new VarTV(value)

	def apply(value: String): VarTV =
		new VarTV(Some(value))

	def getEmpty(): VarTV =
		new VarTV(None)
	
	def apply(value: Term): VarTV =
		new VarTV(Some(mqprintTerm0(value).toString))
}

class VarTV(value: Option[String]) extends TV {
	override val tvName = "VarTV"
		
	override def toJSON(): JsObject =
		super.toJSON() ++
		Json.obj(
			"value" -> value.getOrElse[String]("")
		)
		
}