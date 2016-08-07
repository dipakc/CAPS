package progsynth.synthesisnew

import progsynth.types._
import scala.xml.Elem
import progsynth.printers.XHTMLPrinters2
import progsynth.methodspecs.InterpretedFns._
import SynthUtils._

object FrameDoc {
    <a>
        +------------------------+
        		             			|{ type t = Frame }
        |
                		     			|------------------------|
                     					|    macros              |
							            |    parent 			 |
                     					+----------^-------------+
                                					|
                              +--------------------+------------------+
                              |                                       |
                     +--------+------------------+        +-----------+------------+
                     |{ type t = ProgramFrame }
        |        |{ type t = FormulaFrame }
        |
                     |{ val o = ProgramFrame }
        |        |{ val o = FormulaFrame }
        |
                     |---------------------------|        |------------------------|
                     |  varList                  |        |   dummyList            |
                     |  valList                  |        |   axioms               |
                     |  globalInvs               |        |   conjectures          |
                     |                           |        |   relation             |
                     +---------------------------+        +------------------------+
    </a>
}

//TODO: make macros immutable
abstract class Frame(var macros: List[Macro], val parent: Option[Frame]) {

    def getSummary(): PFFrameSummary

    def isRootFrame(): Boolean = parent.isDefined

    def addMacros(macros: List[Macro]): Unit = {
        this.macros = this.macros ++ macros
    }

}

class ProgramFrame(macros: List[Macro], parent: Option[Frame],
                   val varList: List[Var], val valList: List[Var],
                   val globalInvs: List[TermBool]) extends Frame(macros, parent) {

    def copy(macros: List[Macro] = macros, parent: Option[Frame] = parent,
             varList: List[Var] = varList, valList: List[Var] = valList,
             globalInvs: List[TermBool] = globalInvs) = {
        new ProgramFrame(macros, parent, varList, valList, globalInvs)
    }

    def getSummary() = FrameSummaryUtils.getFrameSummary(this)

    override def toString() = {
        val varStr = "[" + (varList map { vVar => vVar.v /*+ ": " + vVar.t*/ }).mkString(" ") + "]"
        val valStr = "[" + (valList map { vVar => vVar.v /*+ ": " + vVar.t*/ }).mkString(" ") + "]"
        val globalInvStr = "[" + (globalInvs map { gi => gi.toString /*+ ": " + vVar.t*/ }).mkString(" ") + "]"
        varStr + " " + valStr + " " + globalInvStr
    }

    def containsVariable(aVar: Var): Boolean =
        (varList contains aVar) || (valList contains aVar)

    def containsVariable(aVarStr: String): Boolean =
        (varList.map(_.v) contains aVarStr) ||
            (valList.map(_.v) contains aVarStr)

    def getVar(aVarStr: String): Option[Var] = {
        (varList ++ valList).find(_.v == aVarStr)
    }

    def getVar(aVarStr: String, tpe: PSType): Option[Var] = {
        getVar(aVarStr) filter (_.getType == tpe)
    }

    def toXhtml(): Elem = {
        //TODO: Implement
        null
    }

    //def addMacros(newMacros: List[Macro]): ProgramFrame =
    //	new ProgramFrame(newMacros, parent, varList, valList, globalInvs)
}

object ProgramFrame {

    def mkEmptyFrame(parent: Option[Frame]) = new ProgramFrame(macros = Nil, parent = parent,
        varList = Nil, valList = Nil, globalInvs = Nil)

    /**
     * Creates new program frame by adding "vals".
     * Parent of the new frame = parent of the input frame
     */
    def addVals(pf: ProgramFrame, vals: List[Var]) =
        pf.copy(valList = pf.valList ++ vals)

    /**
     * Creates new program frame by adding "vars".
     * Parent of the new frame = parent of the input frame
     */
    def addVars(pf: ProgramFrame, vars: List[Var]) =
        pf.copy(varList = pf.varList ++ vars)

    /**
     * Creates new program frame by adding "globalInvs".
     * Parent of the new frame = parent of the input frame
     */
    def addGlobalInv(pf: ProgramFrame, globalInvs: List[TermBool]) =
        pf.copy(globalInvs = pf.globalInvs ++ globalInvs)
}

class FormulaFrame(macros: List[Macro] = Nil,
                   parent: Option[Frame],
                   val dummies: List[Var] = Nil,
                   val axioms: List[TermBool] = Nil,
                   val conjectures: List[TermBool] = Nil,
                   val relation: Fn) extends Frame(macros, parent) {

    def copy(macros: List[Macro] = macros,
             parent: Option[Frame] = parent,
             dummies: List[Var] = dummies,
             axioms: List[TermBool] = axioms,
             conjectures: List[TermBool] = conjectures,
             relation: Fn = relation) = {
        new FormulaFrame(macros, parent, dummies, axioms, conjectures, relation)
    }

    def setParent(parent: Option[Frame]): FormulaFrame = {
        this.copy(parent = parent)
    }

    def absorbParentFormulaFrame(): Option[FormulaFrame] = {
        parent match {
            case Some(parentF: FormulaFrame) =>
                Some(new FormulaFrame(macros = parentF.macros ++ this.macros,
                    parent = parentF.parent,
                    dummies = parentF.dummies ++ this.dummies,
                    axioms = parentF.axioms ++ this.axioms,
                    conjectures = parentF.conjectures ++ this.conjectures,
                    this.relation))
            case _ => None
        }
    }

    def getSummary() = FrameSummaryUtils.getFrameSummary(this)

    override def toString() = {
        val dummyStr = "[" + (dummies map { vVar => vVar.v /*+ ": " + vVar.t*/ }).mkString(" ") + "]"
        dummyStr
    }

    def containsDummy(aVar: Var): Boolean =
        dummies contains aVar

    def containsDummy(aVarStr: String): Boolean =
        dummies.map(_.v) contains aVarStr

    def getDummyVar(aVarStr: String): Option[Var] = {
        dummies.find(_.v == aVarStr)
    }

    def getDummyVar(aVarStr: String, tpe: PSType): Option[Var] = {
        getDummyVar(aVarStr) filter (_.getType == tpe)
    }

    def getSummaryAxiomAndConjectures(): TermBool = {
        this.getSummary.formulaFrameSummary.getAxiomsAndConjectures()
    }

    def setFocusId(focusIdArg: Int): FormulaFrame = {
        throw new RuntimeException("setFocusId called on formula frame.")
    }

}

object FormulaFrame {

    /**
     * Creates a new empty formula frame.
     */
    def mkEmptyFrame(parent: Option[Frame], relation: Fn) =
        new FormulaFrame(macros = Nil, parent = parent, dummies = Nil, axioms = Nil,
            conjectures = Nil, relation = relation)

    /**
     * Creates new formula frame by adding "dummies".
     * Parent of the new frame = parent of the input frame
     */
    def addDummies(ff: FormulaFrame, dummies: List[Var]) = {
        ff.copy(dummies = ff.dummies ++ dummies)
    }

    /**
     * Creates new formula frame by adding a "dummy".
     * Parent of the new frame = parent of the input frame
     */
    def addDummies(ff: FormulaFrame, dummy: Var) = {
        ff.copy(dummies = ff.dummies :+ dummy)
    }

    /**
     * Creates new formula frame by adding "axioms".
     * Parent of the new frame = parent of the input frame
     */
    def addAxioms(ff: FormulaFrame, axioms: List[TermBool]) = {
        ff.copy(axioms = ff.axioms ++ axioms)
    }

    /**
     * Creates new formula frame by adding an "axiom".
     * Parent of the new frame = parent of the input frame
     */
    def addAxioms(ff: FormulaFrame, axiom: TermBool) = {
        ff.copy(axioms = ff.axioms :+ axiom)
    }

    /**
     * Creates new formula frame by adding "conjectures".
     * Parent of the new frame = parent of the input frame
     */
    def addConjectures(ff: FormulaFrame, conjectures: List[TermBool]) = {
        ff.copy(conjectures = ff.conjectures ++ conjectures)
    }

    /**
     * Creates new formula frame by adding a "conjecture".
     * Parent of the new frame = parent of the input frame
     */
    def addConjectures(ff: FormulaFrame, conjecture: TermBool) = {
        ff.copy(conjectures = ff.conjectures :+ conjecture)
    }
}

