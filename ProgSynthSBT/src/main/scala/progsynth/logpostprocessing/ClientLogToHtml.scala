package progsynth.logpostprocessing
import progsynth.utils.PSUtils._

/** Converts a log file to html format.
 *  Argument 1: Log file path: ${project_loc}\logs\ClientLog.txt
 *  Argument 2: Output file path: ${project_loc}\logs\ClientLog.html
 *  RunConfig: ClientLogToHtml
 *  Additional Info:  logs/ClientLog.md */
object ClientLogToHtml {
    val jqueryUrl = """"jquery-1.7.1.min.js""""
    val javascript = """"ClientLog.js""""
    val cssfile = """"ClientLog.css""""

    def main(args: Array[String]) {
    	assert(args.length == 2)
    	val logFilePath = args(0)
    	val outputFilePath = args(1)
    	import scala.io.Source._
    	addPreamble(outputFilePath)
    	fromFile(logFilePath).getLines().foreach { line =>
    	    val processedLine = processLine(line)
    	    appendFile(outputFilePath, processedLine)
    	}
    	addClosingTxt(outputFilePath)
    }

    def processLine(line: String) = {
        val BeginSection = """\s*BeginSection\((.*)\)""".r
        val EndSection = """EndSection\((.*)\)""".r
        line match {
            case BeginSection(title) =>
                "<div class='Section'>\n" +
                	<div class='SectionTitle'>{title}</div> + "\n" +
                	"<div class='SectionBody'>" +  "\n"
            case EndSection(title) =>
            		"""</div>
                	</div>"""
            case contentText => <div class='ContentText'>{contentText}</div> + "\n"
        }
    }

    def addPreamble(outputFilePath: String) = {
        overwriteFile(outputFilePath,
        """<html>
    	<head>
    	<link rel="stylesheet" type="text/css" href=""" + cssfile + """>
		<script src=""" + jqueryUrl +"""></script>
		<script src=""" + javascript + """></script>
    	</head>
    	<body>
        <div class='Section'>
          	<div class='SectionTitle'>RootSection</div>
            <div class='SectionBody'>
        """)
    }

    def addClosingTxt(outputFilePath: String) = {
        appendFile(outputFilePath,
        """
        </div>
        </div>
        </body>
        </html>""")
    }
}