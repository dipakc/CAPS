package progsynth.synthesisnew

import progsynth.types._
import scala.xml.Elem
import progsynth.printers.XHTMLPrinters2._
import progsynth.methodspecs.InterpretedFns._

/////////////////////////////
//// SUMMARY CLASSES ////////
/////////////////////////////
object FrameSummaryDoc {
<a>

                                             +----------------------------------+
                                      +----->| {type t = ProgramFrameSummary}   |
                                      |      |----------------------------------|
                                      |      |  macros                          |
                                      |      |  varList                         |
                                      |      |  valList                         |
                                      |      |  globalInvs                      |
  +--------------------------+        |      +----------------------------------+
  | {type t = PFFrameSummary}  +o-------+
  |--------------------------|
  |                          +o-------+
  +--------------------------+        |       +---------------------------------+
                                      +------>|  {type t = FormulaFrameSummary} |
                                              |---------------------------------|
                                              |   macros                        |
                                              |   dummies                       |
                                              |   axioms                        |
                                              |   conjectures                   |
                                              |   relation                      |
                                              +---------------------------------+
</a>
}


//TODO: formulaFrameSummary should be of type Option[FormulaFrameSummary]
case class PFFrameSummary(val progFrameSummary: ProgramFrameSummary, val formulaFrameSummary: FormulaFrameSummary) {
	def toXHtml() = {
		<div class="FrameSummary">
		{	progFrameSummary.toXHtml ++
			formulaFrameSummary.toXHtml
		}
		</div>
	}

	def getMacros() = progFrameSummary.macros ++ formulaFrameSummary.macros

	def getGlobalInvs() = progFrameSummary.globalInvs

	def getAllVariables() = progFrameSummary.valList ++ progFrameSummary.varList ++ formulaFrameSummary.dummies

	def addToSummary(f: Frame) = f match {
	    case progFrame: ProgramFrame =>
	        val prevPFS = this.progFrameSummary
    		val newMacros = prevPFS.macros ++ progFrame.macros
    		val newVarList = prevPFS.varList ++ progFrame.varList
    		val newValList = prevPFS.valList ++ progFrame.valList
    		val newGlobalInvs= prevPFS.globalInvs ++ progFrame.globalInvs
    		val newProgFrameSummary = prevPFS.copy(
    				macros = newMacros,
    				varList = newVarList, valList = newValList,
    				globalInvs = newGlobalInvs)

    		this.copy(progFrameSummary = newProgFrameSummary)

	    case ff: FormulaFrame =>
	        val prevFFS = this.formulaFrameSummary
			val newMacros = prevFFS.macros ++ ff.macros
			val newDummyList = prevFFS.dummies ++ ff.dummies

			//filter out axioms and conjectures whose free variables clash with the dummies.
			val newAxioms = prevFFS.axioms.filter(_.isFreeOf(ff.dummies)) ++ ff.axioms
			val newConjectures = prevFFS.conjectures.filter(_.isFreeOf(ff.dummies)) ++ ff.conjectures

			val newRelation = ff.relation

			val newFormulaFrameSummary =
				prevFFS.copy(macros = newMacros, dummies = newDummyList, axioms = newAxioms,
						conjectures = newConjectures, relation = newRelation )

			this.copy(formulaFrameSummary = newFormulaFrameSummary)
	}
}

case class ProgramFrameSummary(val macros: List[Macro], val varList: List[Var], val valList: List[Var],
							   val globalInvs: List[TermBool]) {

	def getConjunctionOfGlobalInvs(): TermBool = {
		val trueFormula: TermBool = TermBool.TrueT
		globalInvs.foldLeft(trueFormula)((f1:TermBool, f2:TermBool) => AndTermBool(f1, f2))
	}

	def toXHtml(): Elem = {
		val retVal = <div class='ProgFrameSummary'>
		{
			val v1 = valList map { aVal: Var =>
				<div class='ctxval'>{aVal.v}: {aVal.getType.getCleanName}</div>
			}
			val wrappedv1 = <div class='ctxvals'>{v1}</div>
			val v2 = varList map { aVar: Var =>
				<div class='ctxvar'>{aVar.v}: {aVar.getType.getCleanName}</div>
			}
			val wrappedv2 = <div class='ctxvars'>{v2}</div>
			val v3 = globalInvs map { gv: TermBool =>
				<div class='globalInv'>{termToHtml(gv)}</div>
			}
			val wrappedv3 = <div class='globalInvs'>{v3}</div>

			(wrappedv1 ++ wrappedv2 ++ wrappedv3).toSeq
		}
		</div>
		retVal
	}
}


case class FormulaFrameSummary(val macros: List[Macro], val dummies: List[Var], val axioms: List[TermBool],
							   val conjectures: List[TermBool], val relation: Fn) {

	def getAxiomsAndConjectures(): TermBool = {
		TermBool.mkConjunct(axioms ++ conjectures)
	}

	def addAxioms(axms: List[TermBool]) = {
		this.copy(axioms = this.axioms ++ axms)
	}

	def addAxioms(axm: TermBool)= {
		this.copy(axioms = this.axioms ++ List(axm))
	}

	def addDummies(dummies: List[Var]) = {
		this.copy(dummies = this.dummies ++ dummies)
	}

	def addDummy(dummy: Var) = {
		this.copy(dummies = this.dummies ++ List(dummy))
	}

	def toXHtml(): Elem = {
		val retVal = <div class='FormulaFrameSummary'>
		{
			val wrappedv1 = {
				val v1 = dummies map { aVal: Var =>
					<div class='dummyvar'>{aVal.v}: {aVal.getType.getCleanName}</div>
				}
				<div class='dummyvars'>{v1}</div>
			}

			val wrappedv2 = {
				val v2 = axioms map { ax: TermBool =>
					<div class='axiom'>{termToHtml(ax)}</div>
				}
				<div class='axioms'>{v2}</div>
			}

			val wrappedv3 = {
				val v3 = conjectures map { conjecture: TermBool =>
					<div class='conjecture'>{termToHtml(conjecture)}</div>
				}
				<div class='axioms'>{v3}</div>
			}

			val v4 =
				<div class='relation'>
					<div>Frame Relation:</div>
					{ mkSpaceDiv }
					<div>{fnToHtml(relation)}</div>
				</div>

			(wrappedv1 ++ wrappedv2 ++ wrappedv3 ++ v4 ).toSeq
		}
		</div>
		retVal
	}
}

object FrameSummaryUtils {

    def mkProgFrameSummary(progFrame: ProgramFrame) =
        ProgramFrameSummary(progFrame.macros, progFrame.varList, progFrame.valList, progFrame.globalInvs)

	val EmptyFormulaFrameSummary = FormulaFrameSummary(Nil, Nil, Nil, Nil, EquivBoolFn)
	val EmptyProgramFrameSummary = ProgramFrameSummary(Nil, Nil, Nil, Nil)


	def getFrameSummary(ff: FormulaFrame) = {
	    ff.parent match {
	        case Some(parentFrame) =>
	            val parentFS: PFFrameSummary = parentFrame.getSummary()
	            parentFS.addToSummary(ff)

	        case None =>
        		PFFrameSummary(
        				EmptyProgramFrameSummary,
        				FormulaFrameSummary(ff.macros , ff.dummies, ff.axioms, ff.conjectures, ff.relation))
	    }
	}

	def getFrameSummary(progFrame: ProgramFrame) = {
	    progFrame.parent match {
	        case Some(parentFrame) =>
	            val parentFS: PFFrameSummary = parentFrame.getSummary()
	            parentFS.addToSummary(progFrame)

	        case None =>
        		PFFrameSummary(
        				mkProgFrameSummary(progFrame),
        				FormulaFrameSummary(progFrame.macros, Nil, Nil, Nil, EquivBoolFn))
	    }
	}

}
