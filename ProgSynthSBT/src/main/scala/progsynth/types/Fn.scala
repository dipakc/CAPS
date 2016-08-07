package progsynth.types

import Types._
import scala.collection.immutable.HashMap
import scala.collection.mutable.LinkedHashMap

case class Fn(val name: String, val argTpes: List[PSType], val tpe: PSType) {
	def numArgs = argTpes.length

	/**Returns scala code(String) that constructs the Fn. */
	def toCode(): String = {
		val ctxMap = LinkedHashMap[String, String]()
		toCode(ctxMap, true)
		(for((code, vari) <- ctxMap) yield {
			<a>val {vari} = {code}</a>.text
		}).mkString("\n")
	}

	/**Returns scala  code(String) that constructs the Fn
	 * given the context Map(code-> variable_name) of the already converted program
	 * If introduceVar is true, then the ctxMap is updated and a variable name is returned.
	 * If introduceVar is false, program fragment corresponding to the Fn is returned */
	def toCode ( ctxMap: LinkedHashMap[String, String] , introduceVar: Boolean=true): String = {
		val argTpeStrs = (argTpes map (_.toString)).mkString(", ")
		val codeRhs = <a>Fn("{name}", List({argTpeStrs}), {tpe})</a>.text
		val seed = Some("f" + name.replace("$", "_"))
		GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
	}

}

//TODO: Move to proper location.
/** A class to store the signature of a user defined predicates */
case class PredSig(val name: String, val args: List[PSType]) {
	def numArgs = args.length
}

/** A class to store the definition of user defined predicates */
case class PredDef(val params: List[String], val formula: FOLFormula)


//object Fn {
//	def apply(name: String, argTpes: List[PSType], tpe: PSType): Fn =
//		new Fn(name, argTpes, tpe, false)
//}

object GenCodeObj { //TODO: move to another file.

	var cnt = 0
	var codeVarNameMap = HashMap[String, String]()

	def mkVarName(seed: Option[String] = None): String = {
		cnt = cnt + 1;
		seed.getOrElse("v") + "_" + cnt.toString
	}

	/*
	def addToMap(varCode: String, varName: String) = {
		codeVarNameMap += varCode -> varName
	}

	def getVarName(varCode: String): Option[String] = {
		codeVarNameMap.get(varCode)
	}

	def getCodeFromRhs(codeRhs: String, seed: Option[String]= None): (String, List[String]) = {
		val cachedNameOpt = getVarName(codeRhs)
		if (cachedNameOpt.isDefined) {
			(cachedNameOpt.get, Nil)
		} else {
			val retVarName = mkVarName(seed)
			val codeList = <a>val { retVarName } = { codeRhs }</a>.text :: Nil
			GenCodeObj.addToMap(codeRhs, retVarName)
			(retVarName, codeList)
		}
	}
	 */
	def addToMap2(ctxMap: LinkedHashMap[String, String], code: String,
		seed: Option[String] = None ): String = {
		ctxMap.getOrElse(code, {
			val varName = GenCodeObj.mkVarName(seed)
			ctxMap += (code -> varName)
			varName
		})
	}

		/** Get code after adding to the map if required.*/
	def gcaam(codeRhs: String, ctxMap: LinkedHashMap[String, String],
		seed: Option[String], introduceVar: Boolean): String =
		{
			if (introduceVar)
				GenCodeObj.addToMap2(ctxMap, codeRhs, seed)
			else
				codeRhs
		}
}
