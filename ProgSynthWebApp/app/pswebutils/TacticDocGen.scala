package pswebutils
import scala.xml._
import progsynth.utils.PSUtils._
import progsynth.synthesisnew._

object TacticDocGen extends App {
	def printTacticDocs(html: Elem, htmlRelPath: String) = {

	    val userDir = System.getProperty("user.dir")
	    val htmlFullPath = userDir + "\\" + htmlRelPath
		overwriteFile(htmlFullPath, html)
	}

	def main() {
    	val html = TacticDocRepo.getHtml()
    	printTacticDocs(html, "public\\docs\\Tactics.html")
    	println("done")
	}

	main()
}
