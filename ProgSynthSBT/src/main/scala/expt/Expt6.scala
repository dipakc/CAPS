package expt
import progsynth.ProgSynth.Counter
import progsynth.types._
import progsynth.types.Types._

import scala.xml.Elem
import progsynth.utils.PSUtils._
import progsynth.debug.PSDbg

object Expt6 {
	def paren(s: String) = "( " + s + " )"
	def parens(s: String) = "" //"[" + s + "]"
	implicit def strToVar(s: String): Var = Var(s)
	implicit def intToNumber(i: Int): Number = Number(i)
	def exprToString(e: Expr): String = e match {
		case Var(name) => name + parens(e.id.toString())
		case Number(num) => num.toString + parens(e.id.toString())
		case UnOp(opr, arg) => paren(opr + arg) + parens(e.id.toString())
		case BinOp(opr, left, right) => paren(List(left, opr, right).mkString(" ")) + parens(e.id.toString())
	}

	abstract class Expr {
		override def toString(): String = exprToString(this)
		var id = -1
		def setId(nid: Int) = { id = nid; this }
	}
	case class Var(name: String) extends Expr
	object Var {
		def apply(name: String, id: Int) = new Var(name).setId(id)
	}
	case class Number(num: Int) extends Expr
	object Number {
		def apply(num: Int, id: Int) = new Number(num).setId(id)
	}
	case class UnOp(operator: String, arg: Expr) extends Expr
	object UnOp{
		def apply(operator: String, arg: Expr, id: Int) = new UnOp(operator, arg).setId(id)
	}
	case class BinOp(operator: String, left: Expr, right: Expr) extends Expr
	object BinOp{
		def apply(operator: String, left: Expr, right: Expr, id: Int) =
			new BinOp(operator, left, right).setId(id)
	}

	case class Formula(lhs: Expr, rhs:Expr)
///////////////////////////////////////////////////////////////////

    abstract class Tactic {
        def apply(anExpr: Expr): Expr
    }

    class InitTactic(anExpr: Expr) extends Tactic {
        override def apply(anExpr: Expr): Expr = {
            anExpr
        }
        override def toString() = {
            "InitTactic( " + anExpr + ")"
        }
    }

    object SwapArgTactic {
        def apply() = new SwapArgTactic
    }
    class SwapArgTactic extends Tactic {
    	override def apply(anExpr: Expr): Expr = anExpr match {
    	    case BinOp(opr, left, right) => BinOp(opr, right, left)
    	    case _ => anExpr
    	}
    	override def toString() = {
    		"SwapArgTactic "
    	}
    }

    object StepInto {
        def apply(stepInFn: Expr => Expr, stepOutFn: Expr => (Expr => Expr) ) =
            new StepInto(stepInFn, stepOutFn)
    }

    class StepInto(val stepInFn: Expr => Expr, val stepOutFn: Expr => (Expr => Expr) ) extends Tactic {
    	override def apply(anExpr: Expr): Expr = stepInFn(anExpr)

    	override def toString() = {
    		"StepInto "
    	}
    }

    object StepOut {
        def apply() = new StepOut
    }
    class StepOut extends Tactic{
        override def apply(anExpr: Expr): Expr = anExpr
    }
/////////////////////////////////////////////////////////////////////////////////////
    /** SynthNode Companion Object*/
    object SynthNode extends Counter
    /** SynthNode
     * tactic : tactic whose application results in this node. null in case of the root node
     * nodeObj: Expr object.
     * childs: Child SynthNodes
     * SOFnList: StepOut Function list. pushed when StepIn is called and poped when StepOut is called*/
    class SynthNode(val tactic: Tactic,
            var nodeObj: Expr,
            var childs: List[SynthNode],
            var SOFnList: List[Expr => Expr]) {

        val id: Int = SynthNode.getCnt
    	def toXml: Elem = {
        	 <node>
        	   <id> {id} </id>
        	   <tactic> {tactic} </tactic>
        	   <nodeObj>{nodeObj}</nodeObj>
        	   	{childs map (child => child.toXml)}
        	 </node>
    	}
    }

    /** Apply tactic to current node */
    def getNextNode(synthNode: SynthNode, tactic: Tactic): SynthNode = {

		val newNode = new SynthNode(tactic = tactic, nodeObj = null, SOFnList = Nil, childs = Nil )

		if(tactic.isInstanceOf[StepInto]) {
        	val tactic1: StepInto = tactic.asInstanceOf[StepInto]
        	newNode.nodeObj = tactic.apply(synthNode.nodeObj)
        	newNode.SOFnList ::= tactic1.stepOutFn(synthNode.nodeObj)
        } else if (tactic.isInstanceOf[StepOut]){
        	if(! synthNode.SOFnList.isEmpty){
        		newNode.nodeObj = synthNode.SOFnList.head(synthNode.nodeObj) //TODO: head?!
        		newNode.SOFnList = synthNode.SOFnList.tail
        	} else {
        	    newNode.nodeObj = synthNode.nodeObj //TODO: head?!
        	    newNode.SOFnList = synthNode.SOFnList
        	}
        } else {
        	newNode.nodeObj = tactic.apply(synthNode.nodeObj)
        	newNode.SOFnList = synthNode.SOFnList
        }
		newNode
    }


    class SynthTree(val expr: Expr) {
        var rootNode = new SynthNode(tactic = null, nodeObj = expr, SOFnList = Nil, childs = Nil )

    	var curNode = rootNode

    	def applyTactic(tactic: Tactic) = {
    	    val newChildNode = getNextNode(curNode, tactic)
    	    curNode.childs ::= newChildNode
    	    curNode = newChildNode
    	}

    	def toXml: Elem = {
    	    <tree>
                { rootNode.toXml}
                <curNodeId>{ curNode.id }</curNodeId>
    	    </tree>
    	}

    }

///////////////////////////////////////////////////////////////////
    def main(args: Array[String]) {
		val expr1 = BinOp("*", "y", BinOp("+", "x", 1))
		val expr2 = BinOp("+", expr1, 5)
		val expr3 = BinOp("+", expr1, expr2)
    	val synthTree = new SynthTree(expr3)
		//overwriteFile("""d:\tmp\abc.xml""", synthTree.toXml)
		synthTree applyTactic SwapArgTactic()
		//overwriteFile("""d:\tmp\abc.xml""", synthTree.toXml)
		def extractRight(expr: Expr) = expr match {
		    case BinOp(_, left, right) => right
		    case _ => expr
		}
		def updateRight(srcExpr: Expr)(newRight: Expr) = {
		    srcExpr match {
		        case BinOp(opr, left, right) => BinOp(opr, left, newRight)
		        case _ => srcExpr
		    }
		}
		synthTree applyTactic StepInto(extractRight, updateRight)
		synthTree applyTactic SwapArgTactic()
		synthTree applyTactic StepOut()
		overwriteFile("""d:\tmp\abc2.xml""", synthTree.toXml)
		PSDbg.logln("done")
    }
}

//1. Multiple types AnnProg, Formula, Term, Invariant etc.
//2. What if the application of certain tactic fails. It shouldn't return same object.
//3.