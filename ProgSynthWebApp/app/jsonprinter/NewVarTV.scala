package jsonprinter

import play.api.libs.json.{Json, JsObject, JsNull}
import progsynth.types.Var

object NewVarTV {
	
	def apply(aVar: Var) = {
		new NewVarTV(Some(StringTV(aVar.v)), Some(PSTypeTV(Some(aVar.getType.getCleanName()))))
	}
	
	def apply(varNameTV: Option[StringTV], varTypeTV: Option[PSTypeTV]): NewVarTV =
		new NewVarTV(varNameTV, varTypeTV)

	def getEmpty() = new NewVarTV(None, None)
}


class NewVarTV(varNameTV: Option[StringTV], varTypeTV: Option[PSTypeTV]) extends TV {
	
	override val tvName = "NewVarTV"
		
	override def toJSON(): JsObject =
		super.toJSON() ++
		Json.obj(
			"varNameTV" -> varNameTV.getOrElse(StringTV.getEmpty()).toJSON,
			"varTypeTV" -> varTypeTV.getOrElse(PSTypeTV.getEmpty()).toJSON
		)
}