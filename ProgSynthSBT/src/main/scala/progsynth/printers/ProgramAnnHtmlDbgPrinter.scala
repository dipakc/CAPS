package progsynth.printers
import progsynth.types.ProgramAnn
import progsynth.utils.PSUtils._

/** Write programAnn to a file. This object will maintain a counter so that fileNames are
 * automatically generated.
 */
object ProgramAnnHtmlDbgPrinter extends Preamble{
	var counter = 0
	var programAnnOpt: Option[ProgramAnn] = None
	def setProgramAnnToMonitor(pa: ProgramAnn) {
		programAnnOpt = Some(pa)
	}
	/** File will be saved to filePrefix_i.html. filePrefix is complete path of the file
	 * without extension. eg. "c:\temp\result"
	 */
	def writeProgramAnnToHtml(htmlMsgToPrepend:String, filePrefix: String) =
		programAnnOpt map { programAnn =>
			var htmlResult = ""
			htmlResult += htmlMsgToPrepend
			htmlResult += XHTMLPrintersOld.programAnnToHtmlMain(programAnn).toString()
			val nextFileLink = """file:\\\""" + filePrefix + (counter+1).toString + ".html"
			htmlResult += "<div><a href="+ nextFileLink + ">Next Result</a></div>"
			htmlResult = "<html>\n" + headElem + "\n<body>\n" + htmlResult + "\n</body>\n</html>\n"
			overwriteFile(filePrefix + counter.toString + ".html", htmlResult)
			counter += 1
		}
}