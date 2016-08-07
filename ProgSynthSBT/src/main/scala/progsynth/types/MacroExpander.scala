package progsynth.types

import progsynth.synthesisnew.Macro
import scala.collection.immutable.{HashMap => IHashMap}
import scala.collection.mutable.{HashMap => MHashMap}

class MacroExpander(macroList: List[Macro]) {

	private val macroMap: IHashMap[String, Macro] = IHashMap(macroList map (i => i.name -> i): _*)

	private def isAMacroApp(aFn: Fn) = macroMap.contains(aFn.name)

	def expand(aTerm: Term): Term = {
		aTerm.mapSubTerms {
			//Replace macro app with body
			case aFnApp@ FnApp(aFn, terms) if isAMacroApp(aFn) => expandAMacroApp(aFnApp)
		}
	}

	// input: mul(m + n, mul(p , q + 1))
	// output: (m + n) * (p *  (q + 1))
	private def expandAMacroApp(aMacroApp: FnApp) = {
		val macroName = aMacroApp.f.name
		val theMacro = macroMap.get(macroName).get
		val paramsTermMap: IHashMap[Var, Term] = IHashMap(theMacro.params zip aMacroApp.ts: _*)

		def isFormalParam(v: Var) = paramsTermMap contains v
		def getActualArgument(formalParam: Var) = paramsTermMap.get(formalParam).get

		//Replace the formal macro parameters with the actual terms
		theMacro.body.mapSubTerms { case v@Var(_) if isFormalParam(v) =>
			expand(getActualArgument(v))
		}

	}
}