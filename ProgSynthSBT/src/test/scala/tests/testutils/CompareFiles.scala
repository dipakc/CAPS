package tests.testutils
import scala.io._
import progsynth.debug.MethodInvocation

object CompareFiles extends App {
	//writeln0((compareFiles("""c:\temp\abc.txt""", """c:\temp\abc1.txt"""))

	def compareFiles(file1: String, file2: String): Boolean = {
		def getBufSource(filePath: String) = {
			var bufSource: BufferedSource = null
			try {
				bufSource = Source.fromFile(filePath)
				Some(bufSource)
			} catch {
				case _ => getBufFromRelativePath(filePath)
			}
		}

		//This function will work only after sbt has copied the resources to the target directory
		def getBufFromRelativePath(filePath: String):Option[BufferedSource] = {
			var bufSource: BufferedSource = null
			try {
				val resourceUrl = getClass.getResource(filePath)
				bufSource = scala.io.Source.fromURL(resourceUrl)
				Some(bufSource)
			} catch {
				case _ => None
			}
		}

		val bufSrcOpt1 = getBufSource(file1)
		if (bufSrcOpt1.isEmpty) return false

		val bufSrcOpt2 = getBufSource(file2)
		if (bufSrcOpt2.isEmpty) return false

		val (strIte1, strIte2) = (bufSrcOpt1.get.getLines(), bufSrcOpt2.get.getLines())

		while (strIte1.hasNext == true && strIte2.hasNext == true) {
			val (line1, line2) = (strIte1.next(), strIte2.next())
			if (line1 != line2) {
				bufSrcOpt1.get.close(); bufSrcOpt2.get.close()
				return false
			}
		}

		if (strIte1.hasNext != strIte2.hasNext) {
			bufSrcOpt1.get.close(); bufSrcOpt2.get.close()
			return false
		} else {
			bufSrcOpt1.get.close(); bufSrcOpt2.get.close()
			return true
		}
	}
}
