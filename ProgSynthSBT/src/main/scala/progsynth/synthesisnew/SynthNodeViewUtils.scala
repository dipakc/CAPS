package progsynth.synthesisnew

import progsynth.ProgSynth.Counter
import progsynth.types._
import progsynth.types.Types._
import scala.xml.Elem
import progsynth.utils._
import scalaz._
import scalaz.Scalaz._
import org.kiama.rewriting.Rewriter.{Term=> KTerm, _}
import progsynth.proofobligations.POGenerator
import progsynth.debug.PSDbg
import progsynth.ProgSynth.toRichFormula
import progsynth.printers.XHTMLPrinters2
import SynthUtils._
import progsynth.synthesisold.ProgContext
import progsynth.synthesisold.EmptyProgContext


trait SynthNodeViewUtils  { self: SynthNode =>
	/** Generates XHtml element of the SynthTree rooted at "this" node.
	 *  Recursively calls the method on the child nodes */
	def toXhtmlRec(): Elem = {
		<div class="node">
				<div class="nodeid"> { id } </div>
				<div class="tactic"> { tactic.tName + (if (tactic.isInstanceOf[StepInTactic]) ">" else "")} </div>
				<div class="frame"> { frameToXHtml(frame) } </div>
				<div class="nodeObj">{ nodeObjToXhtml() ++ nodeObjToXhtmlDbg()}</div>
				{ childs map (child => child.toXhtmlRec) }
			</div>
	}

	/**Get all the nodes from the ancestor node of type "AsgnDerivation"/"IfDerivation" to "this" node*/
	private def getAssignmentOrIfDerivationPath(): List[SynthNode] = {
		var retVal: List[SynthNode]= Nil
		var iNode: SynthNode = this

		while(!(iNode.nodeObj.isInstanceOf[AsgnDerivation] ||
				iNode.nodeObj.isInstanceOf[IfDerivation] ||
				iNode.nodeObj.isInstanceOf[CalcProgStep] ||
				iNode.isRoot
				)) {
			retVal = iNode :: retVal
			iNode = iNode.parent
		}

		//Add AsgnDerivation and IfDerivation node but not the program nodes.
		if( iNode.nodeObj.isInstanceOf[AsgnDerivation] || iNode.nodeObj.isInstanceOf[IfDerivation])
			retVal = iNode :: retVal
		retVal
	}

	/** Get html representation of the parent node.
	 *  Only programAnn parents are shown..*/
	def getParentNodeHtml() = {
		if (!this.isRoot) {
			this.parent.nodeObj match {
				case paParent: ProgramAnn =>
					<div class='ParentNode ProgramAnnParentNode'>
						<div class="Frame">
							{this.parent.frame.getSummary().toXHtml }
						</div>
						{/*pa.toHtml(withPO = true)*/}
						{XHTMLPrinters2.poStatusSummary(paParent)}
						{XHTMLPrinters2.programAnnToHtml(paParent)(None) /* TODO: set proper focusId */}
						{XHTMLPrinters2.toHtmlPO(paParent)}
						</div>
				case _ =>
					<div class='ParentNode'>
					</div>
			}
		}
	}

	/** Print nodeObj of "this" SynthNode*/
	def nodeObjToXhtml(): Elem = this.nodeObj match {
		case CalcProgStep(prog, focusIdOpt) =>
			<div>
				{getParentNodeHtml()}
				<div class='ParentTactic'>
					{this.tactic.getHint}
				</div>
				<div class='ThisNode'>
					<div class="Frame">
						{this.frame.getSummary().toXHtml }
					</div>
					{/*pa.toHtml(withPO = true)*/}
					{XHTMLPrinters2.poStatusSummary(prog)}
					<div id="ProgramAnnContainer">
					    {
					        XHTMLPrinters2.programAnnToHtml(prog)(focusIdOpt)
					    }
					</div>
					{XHTMLPrinters2.toHtmlPO(prog)}
				</div>
			</div>
		/*
		case pa: ProgramAnn =>
			<div>
				{getParentNodeHtml()}
				<div class='ParentTactic'>
					{this.tactic.getHint}
				</div>
				<div class='ThisNode'>
					<div class="Frame">
						{this.frame.getSummary().toXHtml }
					</div>
					{/*pa.toHtml(withPO = true)*/}
					{XHTMLPrinters2.poStatusSummary(pa)}
					{XHTMLPrinters2.programAnnToHtml(pa)(None)}
					{XHTMLPrinters2.toHtmlPO(pa)}
				</div>
			</div>
		case f: FOLFormula =>
			<div class="folformula">{ XHTMLPrinters2.formulaToHtml(f) }</div>
		case t: Term =>
			<div class="termbool">{XHTMLPrinters2.termToHtml(t)}</div>
		*/
		case asgnDerivation: AsgnDerivation =>
			XHTMLPrinters2.asgnDerivation2ToHtml(asgnDerivation)(None)
		case ifDerivation: IfDerivation =>
			<div>
			{getParentNodeHtml()}
			<div class='ParentTactic'>
				{this.tactic.getHint}
			</div>
			{ XHTMLPrinters2.ifDerivation2ToHtml(ifDerivation)}
			</div>
		case CalcProofStep(_coreObj, _grd, _derivedTerms, _freshVars, _assumedPre, _metaVars) =>
			val nodeList = getAssignmentOrIfDerivationPath()
			val hBox: HBox = HBoxUtils.getHBox(nodeList)
			hBox.hnodeToXHtml(false)
		case x => <div>{ scala.xml.Unparsed(x.toString()) }</div>
	}

	/** Genereate a node for debugging purpose */
	def nodeObjToXhtmlDbg(): Elem = {
		val dbgInfoNode = this.nodeObj match {
			case pa: ProgramAnn => <div class='nodeObjTpe'>nodeObjTpe: ProgramAnn</div>
			case f: FOLFormula =>
				<div class='nodeObjTpe'>nodeObjTpe: FOLFormula</div>
			case t: Term =>
				<div class='nodeObjTpe'>nodeObjTpe: Term</div>
			case asgnDerivation: AsgnDerivation =>
				<div class='nodeObjTpe'>nodeObjTpe: AsgnDerivation</div>
			case CalcProofStep(coreObj, grd, derivedTerms, freshVars, assumedPreList, metaVars) =>
				<div class='nodeObjTpe'>
					<div>nodeObjTpe: CalcProofStep &nbsp;</div>
					<div>{grd.toString + " "}</div>
					<div>{derivedTerms.toString}</div>
					<div>{freshVars.toString}</div>
					<div>{assumedPreList.toString}</div>
					<div>{metaVars.toString}</div>
				</div>
			case x =>
				<div class='nodeObjTpe'>nodeObjTpe: Unknown</div>
		}
		<div class="nodeObjDbg">{ dbgInfoNode }</div>
	}
	/*
	def contextToXHtml(progContext: ProgContext): Elem = {
		val retVal = <div class='context'>
		{
			val v1 = progContext.valList map { aVal: Var =>
				<div class='ctxval'>{aVal.v}: {aVal.getType}</div>
			}
			val v2 = progContext.varList map { aVar: Var =>
				<div class='ctxvar'>{aVar.v}: {aVar.getType}</div>
			}
			val v3 = progContext.dummyList map { aDummy: Var =>
				<div class='ctxdummy'>{aDummy.v}: {aDummy.getType}</div>
			}
			(v1 ++ v2 ++ v3).toSeq
		}
		</div>
		retVal
	}
	*/
	//TODO: implement
	def frameToXHtml(frame: Frame): Elem = {
		val retVal = <div class='frame'> TODO: implement</div>
		retVal
	}
}
