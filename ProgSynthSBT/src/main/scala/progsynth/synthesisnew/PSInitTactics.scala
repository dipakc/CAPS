package progsynth.synthesisnew

import progsynth.ProgSynth.Counter
import progsynth.types._
import progsynth.types.Types._
import scala.xml.Elem
import progsynth.utils._
import scalaz.{ Forall => SForall, Const => SConst, Success => SSuccess, Failure => SFailure, _ }
import Scalaz._
import org.kiama.rewriting.Rewriter.{ Term => KTerm, _ }
import progsynth.proofobligations.POGenerator
import progsynth.proofobligations.StrongestPost
import progsynth.debug.PSDbg
import progsynth.ProgSynth.toRichFormula
import progsynth.printers.XHTMLPrinters2
import SynthUtils._
import progsynth.methodspecs.InterpretedFns._
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._
import progsynth.provers._
import PartialFunction._
import TacticDocRepo._
import PSTacticsHelper._
//import scala.util.Try
import scala.util.{Success, Failure, Try}

case class InitTactic(name: String, params: List[Var], retVar: Var, preF: TermBool, postF: TermBool) extends PrgFunTactic {
	override val tName = "Init"

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = (nodeObj, frame) match {
		case (any, _) =>
			val pre = InvariantT(None, UnkTerm.mkUnkTermBool, None)
			val post = InvariantT(None, UnkTerm.mkUnkTermBool, None)
			Success(FunctionProg(name, params, retVar, mkUnknownProg(preF.inv(), 1, postF.inv(retVar)), Nil, pre, post))
		case _ =>
		    Failure(new RuntimeException("Tactic not applicable"))
	} //TODO: set proper id

	def getHint(): Elem =
		<div>
			<div>InitTactic</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = None
}

case class InitTactic2(initP: ProgramAnn) extends PrgFunTactic {
	override val tName = "Init2"

	//override def fun: (CalcStep, Frame) ==> CalcStep = { case (any, _) => initP }

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = {
	    Success(initP)
	}

	def getHint(): Elem =
		<div>
			<div>InitTactic2</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = None
}

case class InitTactic3(name: String, params: List[Var], retVar: Var, preF: TermBool, postF: TermBool, globalInvs: List[TermBool]) extends PrgFunTactic {
	override val tName = "Init3"

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = {
		val pre = InvariantT(None, UnkTerm.mkUnkTermBool, None)
		val post = InvariantT(None, UnkTerm.mkUnkTermBool, None)
		Success(FunctionProg(name, params, retVar, mkUnknownProg(preF.inv(), 1, postF.inv(retVar)), globalInvs, pre, post))
	} //TODO: set proper id

	def getHint(): Elem =
		<div>
			<div>InitTactic</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = None
}

/**Init tactic that directly creates an unknownProg
 * TODO: name is not used anywhere */
/*
	Init4Tactic(
		name = "",
		immutableVars = Nil,
		mutableVars = Nil,
		globalInvs = Nil,
		preF = TermBool.TrueT,
		postF = TermBool.TrueT ,
		macros = Nil
	)
*/
/*
new Init4Tactic(
	name = "",
	immutableVars = Nil,
	mutableVars = Nil,
	globalInvs = Nil,
	preF = null,
	postF = null,
	macros = Nil)
*/
case class Init4Tactic(	val name: String,
	val immutableVars: List[Var], val mutableVars: List[Var],
	val globalInvs: List[TermBool], val preF: TermBool, val postF: TermBool,
	val macros: List[Macro]) extends InitTacticHoareAbs {

    override val tName = "Init4"

	override def initNodeAndFrame(rootFrame: Frame): (CalcStep, Frame) = {
		val nodeObj = mkUnknownProg(preF.inv(), 1, postF.inv())
		val frm: Frame = new ProgramFrame(macros, parent = Some(rootFrame), mutableVars, immutableVars, globalInvs)
		(CalcProgStep(nodeObj, None), frm)
	}

	def getHint(): Elem = {

	    import XHTMLPrinters2._

	    def varStr(aVar: Var): String = aVar.v + ": " + aVar.getType().getCleanName()

	    import Init4TacticDoc.dName
		<div>
	    	<div class="tacticName">Init4</div>
	    	{
		    	PSTacticsHelper.paramTable(
		    	    Tuple2(dName("immutableVars"), immutableVars.map(varStr).mkString(", "))
		    	    :: Tuple2(dName("mutableVars"), mutableVars.map(varStr).mkString(", ") )
		    	    :: Tuple2(dName("globalInvs"), oneColTable(globalInvs, termToHtml _) )
		    	    :: Tuple2(dName("preF"), termToHtml(preF) )
		    	    :: Tuple2(dName("postF"), termToHtml(postF))
		    	    //:: Tuple2(dName("macros"), getListTable(macros, macroToHtml _))
		    	    :: Nil
		    	)
			}
			{docLink(tName)}
		</div>
	}

	def getRelation() = None
}

/**Init tactic that directly creates an unknownProg
 * TODO: name is not used anywhere */
/*
	Init4Tactic(
		name = "",
		immutableVars = Nil,
		mutableVars = Nil,
		globalInvs = Nil,
		preF = TermBool.TrueT,
		postF = TermBool.TrueT ,
		macros = Nil
	)
*/
/*
new Init4Tactic(
	name = "",
	immutableVars = Nil,
	mutableVars = Nil,
	globalInvs = Nil,
	preF = null,
	postF = null,
	macros = Nil)
*/

/**Init tactic that directly creates a program node.*/
//TODO: check for correctness of input program.
case class InitProgTactic(	name: String,
	immutableVars: List[Var], mutableVars: List[Var],
	globalInvs: List[TermBool], prog: ProgramAnn, macros: List[Macro]) extends InitTacticHoareAbs {

    override val tName = "InitProg"

	override def initNodeAndFrame(rootFrame: Frame): (CalcStep, Frame) = {
		val nodeObj = prog
		val frm: Frame = new ProgramFrame(macros, parent = Some(rootFrame), mutableVars, immutableVars, globalInvs)
		(CalcProgStep(nodeObj, None), frm)
	}

	def getHint(): Elem = {
		<div>
			{docLink(tName)}
		</div>
	}

	def getRelation() = None
}

