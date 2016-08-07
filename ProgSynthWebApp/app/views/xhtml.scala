package views

import progsynth.synthesisnew.SynthTree
import progsynth.synthesisnew.SynthNode
import play.api.templates.Html
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._
import scalatags.Text.all._

object xhtml {
	val logger = LoggerFactory.getLogger("progsynth.xhtml")

	def showState(synthTree: SynthTree): Html = {
		logger.trace("inside showState")
		/*
		val aNode =
			<div id="synthtree">
				<div class="rootnode node">
					<div class="nodeid"> {synthTree.rootNode.id}</div>
					<div class="tactic"> </div>
					<div class="nodeObj"> </div>
					{ synthTree.rootNode.childs map (child => child.toXhtmlRec) }
				</div>
				<div class="curNodeId">{ synthTree.curNode.id }</div>
			</div>
		*/
		val aNode =
		   div(id := "synthtree")(
		       div(cls := "rootnode node")(
		          div( cls := "nodeid")(synthTree.rootNode.id),
		          div( cls := "tactic"),
		          div( cls := "nodeObj"),
		          raw (
		               synthTree.rootNode.childs.map(child => child.toXhtmlRec.toString).mkString("\n")
		          )),
               div(cls := "curNodeId")(synthTree.curNode.id))

		new Html(new StringBuilder(aNode.toString))
	}

	def showNodeState(synthNode: SynthNode): Html = {
		val aNode = synthNode.toXhtmlRec()
		new Html(new StringBuilder(aNode.toString))
	}

	//Also refer parseTest.css and parseTest.js
	def parseTest(): Html = {
		val content =
		<div>
			<div> Use TestScript.scala for testing in scala interpreter </div>
			<form class="parseForm" name="ParseForm">
				<div class="parseComboDiv">
				<select class="parseTypeCombo" name="ParserType">
					<option value="Term">Term</option>
					<option value="TermInt">TermInt</option>
					<option value="TermBool">TermBool</option>
					<option value="TermArrayInt">TermArrayInt</option>
					<option value="TermArrayBool">TermArrayBool</option>
					<option value="FOLFormula">FOLFormula</option>
				</select>
				</div>
				<div class='parseInputDiv'>
					<div class="parseInput mathquill-editable" name="parserInput"/>
				</div>
				<div class='parseSubmitDiv'>
					<button id="parseSubmit" type="button" >Parse</button>
				</div>
			</form>
			<hr></hr>
			<div class='parseSentInput'>
				<div class='parseSentHeader'> Sent Data: </div>
				<div class='content'> </div>
			</div>
			<hr></hr>
			<div class='parseResult'>
				<div class='parseResultHeader'> Parse Result</div>
				<div class='content'> </div>
			</div>
		</div>
		new Html(new StringBuilder(content.toString))
	}
}