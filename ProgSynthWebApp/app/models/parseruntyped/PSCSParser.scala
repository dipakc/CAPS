package models.parseruntyped

import progsynth.synthesisold.ProgContext
import progsynth.types._
import progsynth.types.Types._

/** Parser wrapper
 * Usage
 * import models.parser.PSParser
 * val p = new PSParser(progCtx)
 * p.parseAll(termP, )
 *  */
class PSCSParser(isMathQuill: Boolean) extends TermCSParsers with PredCSParsers with FOLFormulaCSParsers {
	override val isMQ = isMathQuill
}