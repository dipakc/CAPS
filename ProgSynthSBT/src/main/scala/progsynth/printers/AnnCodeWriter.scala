package progsynth.printers
import progsynth.utils.{PSUtils=>psu}
import progsynth.config.AppConfig

object AnnCodeWriterWrapper {

	object AnnCodeWriter extends Preamble {
		def writeAnnCode(annCodeStr: String): Unit = {
			val htmlResult = "<html>\n" + headElem + "\n<body>\n" + annCodeStr + "\n</body>\n</html>\n"
			psu.overwriteFile(AppConfig.annCodeFile, htmlResult)
		}
	}

	trait Preamble extends ToggleScript {
		val headElem =
			<head>
				<link type="text/css" rel="stylesheet" href="progsynthlog.css"/>
				<script class="jsbin" src="http://code.jquery.com/jquery-1.7.2.min.js"> a </script>
				<script type="text/javascript">{ toggleScript }</script>
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
}