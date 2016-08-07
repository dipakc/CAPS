package expt

object Expt9WS {
  	import progsynth.types.FormulaGen._
  	import progsynth.types.TermGen._
  	import progsynth.types.ProgramAnnGen._

  	val fgen = folFormulaGenHt(3, 4)

  	fgen.sample
  	fgen.sample

	  val paGen = programAnnGenHt(1)
	  paGen.sample
}