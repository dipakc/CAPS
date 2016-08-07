package jsonprinter
package tactics

import progsynth.types.TermBool
import progsynth.types.Var
import models.mqprinter.MQPrinter._

object Init4TV {
	def apply(name: String, immutableVars: List[Var], mutableVars: List[Var],
		globalInvs: List[TermBool], preF: TermBool, postF: TermBool): Init4TV = {
		new Init4TV(name, immutableVars, mutableVars,
						globalInvs.map(mqprintTerm0(_)),
						mqprintTerm0(preF), mqprintTerm0(postF))
	}
}

//TODO: macros argument
class Init4TV(name: String, immutableVars: List[Var], mutableVars: List[Var],
		globalInvs: List[String], preF: String, postF: String) extends ClassTV(
	{
		List(FieldTV("name", "Derivation Name", StringTV(name)),
			FieldTV("immutableVars", "Constants",
					ListTV(NewVarTV.getEmpty(),
							immutableVars.map(iv =>
								NewVarTV(Some(StringTV(iv.v)), Some(PSTypeTV(Some(iv.getType.getCleanName))))))),
			FieldTV("mutableVars", "Variables",
					ListTV(NewVarTV.getEmpty(),
							mutableVars.map(mv =>
								NewVarTV(Some(StringTV(mv.v)), Some(PSTypeTV(Some(mv.getType.getCleanName))))))),
			FieldTV("globalInvs", "Global Invariants",
					ListTV(TermBoolTV.getEmpty(), globalInvs.map(gInv => TermBoolTV(Some(gInv))))),
			FieldTV("preF", "Precondition",
					TermBoolTV(Some(preF))),
			FieldTV("postF", "Postcondition",
					TermBoolTV(Some(postF))))
	})
{
	override val tvName = "Init4TV"
	
}
