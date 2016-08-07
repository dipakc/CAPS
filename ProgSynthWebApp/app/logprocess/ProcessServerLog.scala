package logprocess

import progsynth.utils.PSUtils._

case class PatternData(logger: String, level: String, date: String)
/** Converts a log file to html format.
 *  Argument 1: Log file path: Example: ${project_loc}\logs\progsynth.txt
 *  Argument 2: Output file path: Example: ${project_loc}\logs\progsynth.html
 *  RunConfig: SererLogToHtml
 *  Additional Info:  logs/ClientLog.md */
object ProcessServerLog {

    def main(args: Array[String]) {
    	assert(args.length == 2)
    	val logFilePath = args(0)
    	val outputFilePath = args(1)
    	createHtmlLog(logFilePath, outputFilePath, true)
    }

    def createHtmlLog(logFilePath: String, outputFilePath: String, online: Boolean=false) = {
    	println("Converting log file to html...")
    	println(s"Input file path is $logFilePath")
    	println(s"output file path is $outputFilePath")

    	import scala.io.Source._
    	addPreamble(outputFilePath, online)
    	fromFile(logFilePath).getLines().foreach { line =>
    	    val processedLine = processLine(line)
    	    appendFile(outputFilePath, processedLine)
    	}
    	addClosingTxt(outputFilePath)
    	println("done")
    	println(outputFilePath)
    }

    def processLine(line: String) = {
        val PatternMsg = """(.*) ::: (.*)""".r
        line match {
            case PatternMsg(pat, msg) =>
                val patternDataOpt = processPattern(pat);
                processMsg(msg, patternDataOpt)
            case _ => processMsg(line, None)
        }
    }

    def processPattern(pattern: String): Option[PatternData] = {
        val PatternR = """(.*) (.*) (....-..-.. ..:..:.*)""".r
        pattern match {
            case PatternR(logger, level, date) => Some(PatternData(logger, level, date))
            case _ => None
        }
    }

    /**
     * Schema:
     * <div class='Section'>
     *	  <div class='LogInfo'>
     *     	<div id='Logger'> logger </div>
     *      <div id='Level'> level </div>
     *    </div>
     *    <div class='StartDate'>2006-10-20 14:06:49,812</div>
     *    <div class='SectionTitle'>title</div>
     *	  <div class='SectionBody'>
     *    	<div class='Message'>
	 *	  		<div class='LogInfo'>
	 *     			<div class='Logger'> logger </div>
	 *      		<div class='Level'> level </div>
	 *    		</div>
	 *     	<div class='Date'> date </div>
	 *     	<div class='MessageBody'> messageText </div>
     *    </div>
     *    <div class='EndDate'>date</div>
     *    <div class='Duration'>000</div>
     * </div>
     *
     */
    def processMsg(msg: String, patternDataOpt: Option[PatternData]) = {
        val BeginSection = """\s*BeginSection\((.*)\)""".r
        val EndSection = """EndSection\((.*)\)""".r
        (patternDataOpt, msg) match {
            case (Some(patternData), BeginSection(title)) =>
                "<div class='Section'>\n" +
                	<div class='LogInfo'>
                		<div class='Logger'>{patternData.logger}</div>
                		<div class='Level'>{patternData.level}</div>
                	</div> + "\n" +
                	<div class='StartDate'>{patternData.date}</div> + "\n" +
                	<div class='SectionTitle'>{title}</div> + "\n" +
                	"<div class='SectionBody'>" +  "\n"
            case (Some(patternData), EndSection(title)) =>
                	<div class='EndDate'>{patternData.date}</div> + "\n" +
                	<div class='Duration'>000</div> + "\n" +
            		"""</div>
                	</div>""" + "\n"
            case (Some(patternData), "BeginMultiLine") =>
                """<div class='MultiLine'>""" + "\n" +
                	<div class='LogInfo'>
                		<div class='Logger'>{patternData.logger}</div>
                		<div class='Level'>{patternData.level}</div>
                	</div>  + "\n" +
                	<div class='Date'>{patternData.date}</div>  + "\n" +
                	"""<div class='MessageBody'>""" + "\n"
            case (None, "EndMultiLine") => """</div></div>""" + "\n"
            case (None, multilineMsgLine) => multilineMsgLine + "\n"
            case (Some(patternData), messageBody) => //single line messages
                <div class='Message'>
                	<div class='LogInfo'>
                		<div class='Logger'>{patternData.logger}</div>
                		<div class='Level'>{patternData.level}</div>
                	</div>
                	<div class='Date'>{patternData.date}</div>
                	<div class='MessageBody'>{messageBody}</div>
                </div> + "\n"
        }
    }

    def addPreamble(outputFilePath: String, online: Boolean) = {

        val base = if(online ) "../assets/" else "../public/"

        val cssfile = base + "stylesheets/ServerLog.css"
        val progsynthcss = base + "stylesheets/progsynthlog.css"
        val jqueryUrl = base + "javascripts/lib/jquery-1.7.1.min.js"
        val javascript = base + "javascripts/ServerLog.js"

        overwriteFile(outputFilePath,
        """<html>
    	<head>
    	<title>Debug Log</title>
    	<link rel="stylesheet" type="text/css" href=""" + cssfile + """>
    	<link rel="stylesheet" type="text/css" href=""" + progsynthcss + """>
		<script src=""" + jqueryUrl +""" type="text/javascript"></script>
		<script src=""" + javascript + """ type="text/javascript"></script>
    	</head>
    	<body>
    	<div><button type="button" id="clearLog">Clear Log</button></div>
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