package expt
import progsynth.ProgSynth.Counter
import progsynth.types._
import progsynth.types.Types._

import scala.xml.Elem
import progsynth.utils.PSUtils._

object Expt5 {
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

    class SwapArgTactic() extends Tactic {
    	override def apply(anExpr: Expr): Expr = anExpr match {
    	    case BinOp(opr, left, right) => BinOp(opr, right, left)
    	    case _ => anExpr
    	}
    	override def toString() = {
    		"SwapArgTactic "
    	}
    }

    class StepInto(val stepinFn: Expr => Expr, val stepoutFn: (Expr, Expr) => Expr) extends Tactic {
    	override def apply(anExpr: Expr): Expr = stepinFn(anExpr)

    	override def toString() = {
    		"StepInto "
    	}
    }

    class StepOut extends Tactic{
        override def apply(anExpr: Expr): Expr = anExpr
    }
/////////////////////////////////////////////////////////////////////////////////////
    object SynthNode extends Counter {
        /** prevNode -- tactic --> ?? */
        def make(prevNode: SynthNode, tactic: Tactic): SynthNode = {
            if(tactic.isInstanceOf[StepInto]) {
                val tactic1 = tactic.asInstanceOf[StepInto]
            	new SynthNode(tactic,
            		prevNode.stack match {
            			case (top, _) :: tail => (tactic1.stepinFn(top), tactic1.stepoutFn) :: prevNode.stack
            			case _ => prevNode.stack
            		})
            } else if(tactic.isInstanceOf[StepOut]) {
            	new SynthNode(tactic,
            		prevNode.stack match {
            			case (top, _) :: tail => tail
            			case _ => prevNode.stack
            		})
            } else {
            	new SynthNode(tactic,
            		prevNode.stack match {
            			case (expr, cfun) :: tail => (tactic.apply(expr), cfun) :: tail
            			case _ => prevNode.stack
            		}
    			)
            }
        }
    }

    class SynthNode(val tactic: Tactic, var stack: List[(Expr, (Expr, Expr)=>Expr)]) {
    	val id: Int = SynthNode.getCnt
        //var stack: List[(Expr, Expr=>Expr)] = List((tacticObj.apply(prevExpr), {x: Expr => x}))
        var childs: List[SynthNode] = Nil

        /** apply tactic to current node */
        def applyTactic(nextTactic: Tactic) = {
           val newChildNode = SynthNode.make(this, nextTactic)
           childs =  newChildNode :: childs
           newChildNode
        }

    	def toXml: Elem = {
        	 <node>
        	   <id> {id} </id>
        	   <tactic> {tactic} </tactic>
        	   <nodeObj>{stack.head._1}</nodeObj>
        	   	{childs map (child => child.toXml)}
        	 </node>
    	}
    }

    class SynthTree(val expr: Expr) {
        var rootNode = new SynthNode(new InitTactic(expr), List((expr, {(x: Expr, y:Expr) => x})))
    	var curNode = rootNode

    	def applyTactic(tactic: Tactic) = {
    	    val newChildNode = curNode.applyTactic(tactic)
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
		overwriteFile("""d:\tmp\abc.xml""", synthTree.toXml)
		synthTree.applyTactic(new SwapArgTactic() )
		overwriteFile("""d:\tmp\abc.xml""", synthTree.toXml)
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
		//synthTree.applyTactic(new StepInto(extractRight)(updateRight))
    }
}