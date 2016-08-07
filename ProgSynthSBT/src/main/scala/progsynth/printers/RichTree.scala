package progsynth.printers
import scala.tools.nsc.Global
import scala.util.matching.Regex
import progsynth.PSPredef._

class RichTree(val aTree: Global#Tree) {
	val aMap = Map(
			"""progsynth.spec.StaticAssertions.any2Ensuring\[.*?\]""" -> "a2E",
			"""progsynth.spec.StaticAssertions.sRequire\[.*?\]""" -> "sRequire",
			"""progsynth.types.Formula.True""" -> "True",
			"""progsynth.types.Formula.False""" -> "False",
			"""new progsynth.types.Formula.And\[progsynth.types.Formula.Pred\]""" -> "And", //TODO: use regexp groups
			"""new progsynth.types.Formula.Or\[progsynth.types.Formula.Pred\]""" -> "Or",
			"""new progsynth.types.Formula.Not\[progsynth.types.Formula.Pred\]""" -> "Not",
			"""new progsynth.types.Formula.Impl\[progsynth.types.Formula.Pred\]""" -> "Impl",
			"""new progsynth.types.Formula.Iff\[progsynth.types.Formula.Pred\]""" -> "Iff",
			"""progsynth.spec.StaticAssertions.sAssert""" -> "sAssert",
			"""progsynth.types.FormulaUtils.boolean2Atom""" -> "b2A"
	)

	def pprint() = {
		//TODO: use string buffer
		var aStr = aTree.toString
		for ((str1, str2) <- aMap ) {
			aStr = aStr.replaceAll(str1, str2)
		}
		aStr += "\n"
		aStr
	}

	def printNodes(compiler: Global) = {
		val npr = compiler.nodePrinters
		npr.infolevel = npr.InfoLevel.Normal //Quiet, Verbose
		npr.nodeToString(aTree.asInstanceOf[npr.global.Tree])
	}
}

object RichTree {
	implicit def treeToRichTree(aTree: Global#Tree) = new RichTree(aTree)
}

