package progsynth.printers
import progsynth.utils.PSUtils._
import progsynth.config.AppConfig

trait ResultHtmlWriter extends Preamble{
	/**List: (Output filename, result html String) */
	var fileName_Htmls_Map: Map[String, List[String]]  = Map()

	/** to be called for each method.
	 * 'fileName' is constructed from ownerChain of the method.symbol */
	def addToHtmlResult(fileName: String, result: String) = {
		val oldResults = fileName_Htmls_Map.getOrElse(fileName, Nil)
		val updatedResults = result::oldResults
		fileName_Htmls_Map += (fileName -> updatedResults)
	}

	def writeHtmlResult() = {
		fileName_Htmls_Map foreach {case (file, htmls) =>
			val htmlBody = htmls.mkString("\n")
			val htmlContent = "<html>\n" + headElem + "\n<body>\n" + htmlBody + "\n</body>\n</html>\n"
			val outputFilePath = AppConfig.resultDir + "\\" + file
			overwriteFile(outputFilePath, htmlContent)
		}
	}
}

trait Preamble extends ToggleScript {
	val headElem =
	<head>
		<link type="text/css" rel="stylesheet" href="progsynthlog.css"/>
		<script class="jsbin" src="http://code.jquery.com/jquery-1.7.2.min.js"> a </script>
    	<script type="text/javascript">{toggleScript}</script>
	</head>
}

trait ToggleScript {
	val toggleScript = scala.xml.Unparsed("""
			jQuery(document).ready(function() {
			jQuery("collapse").hide();

			jQuery("collapse").click(function(event)
			{
			if(event.target == this) {
			jQuery(this).next().slideToggle(500);
			jQuery(this).slideToggle(50);
			}
			});

			jQuery("collapse + *").dblclick(function(event)
			{
			if(event.target == this) {
			jQuery(this).prev().slideToggle(500);
			jQuery(this).slideToggle(500);
			}
			//e.stopPropogation();
			});

	});""")
}
