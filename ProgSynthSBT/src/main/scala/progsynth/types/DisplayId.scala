package progsynth.types
import org.kiama.rewriting.Rewriter.{Term => RTerm, _}
import progsynth.debug.PSDbg

// PSProgTree hierarchy docs/ProgSynthDoc.html#PSProgTree

trait DisplayId extends Cloneable { self =>
	var displayId: Int = -1
	def setDisplayId(newId: Int) = { displayId = newId }
	def cloneobj(): DisplayId = super.clone().asInstanceOf[DisplayId]
	
	private def cloneSharedNodes(): Option[RTerm] = {
		var i = 0
	    def cloneRuleF(term: RTerm): RTerm = term match {
			case n: DisplayId =>
				i += 1
				val node =  if (n.displayId != -1) n.cloneobj() else n
				node.setDisplayId(i)
				node
			case x =>
			    x
		}

	    i = 0
	    //topdown will not here. Rewriting with mutable variables.
	    bottomup(rulef{cloneRuleF})(this)
	}

	def setDisplayIdAll(): Option[RTerm] = {
		val newObjOpt = cloneSharedNodes()
		newObjOpt map { newObj =>
    		var i = 0

    		def setId(term: RTerm): RTerm = term match {
			case n: DisplayId =>
				i += 1
					n.setDisplayId(i)
					n
    			case x =>
    			    x
				}
    		//Execute topdown for side effects since it involves mutable variable displayId.
		    topdown(rulef{setId})(newObj) //Run for sideEffect
		    newObj
		}
	}
}

// PSProgTree hierarchy docs/ProgSynthDoc.html#PSProgTree
object DisplayIdPrinter {
	def makeString(displayId: Int, className: String, args: Seq[Any]) = {
		val argStrCsv= (args map toIdString).mkString(", ")
		<a>{displayId}@{className}({argStrCsv})</a>.text
	}
	def toIdString(obj: Any): String = obj match {
		case lst: List[_] =>
			(lst map toIdString).toString
		case opt: Option[_] =>
			(opt map toIdString).toString
		case progNode : PSProgTree =>
			val cs = progNode.deconstruct
			makeString(progNode.displayId, progNode.getClass.getSimpleName, cs)
		case x => x.toString
	}
}
