package controllers

import play.api.libs.json.JsValue
import play.api.mvc.Controller
import play.api.templates.Html
import logprocess.ProcessServerLog
import progsynth.utils.PSUtils._

trait ControllerDebug { self: ControllerAMain with Controller with Secured =>

	def parseTest() = Authenticated { implicit request =>
		val contentHtml = views.xhtml.parseTest()
		//Ok(views.html.parseTest.apply(contentHtml))
		Ok(views.ParseTestView(contentHtml))
	}

	def parseTestSubmit() = Authenticated(parse.json) { implicit request =>
		//val z: Html = views.html.derivation.apply("dummyuser", rootUrl(request), Nil)
		val z: Html = views.DerivationView.derivation("dummyuser", rootUrl(request), Nil)
		Ok(z)
	}

	def debugLog() = Authenticated { implicit request =>
	    val logFilePath = """logs\progsynth.txt"""
	    val outputFilePath = """logs\progsynth.html"""
	    ProcessServerLog.createHtmlLog(logFilePath, outputFilePath, true)
	    val debugLog = {
    		val source = scala.io.Source.fromFile(outputFilePath)
	        val ret = source.getLines mkString "\n"
	        source.close()
	        ret
	    }

	    //http://localhost:9000/assets/stylesheets/progsynthlog.css
	    //http://localhost:9000/assets/stylesheets/ServerLog.css
	    //http://localhost:9000/assets/stylesheets/ServerLog.js

	    //val debugLog = <div>Debug Log Content</div>
	    /*
	    val content =
	        <div>
	        	<button type="button">Clear Log</button>
				<div class='DebugLog'> {debugLog} </div>
			</div>
		*/
	    Ok(new Html(new StringBuilder(debugLog.toString)))
	}

	def clearDebugLog () = Authenticated { implicit request =>
	    val logFilePath = """logs\progsynth.txt"""
	    overwriteFile(logFilePath, "")
	    Ok(new Html(new StringBuilder("Debug Log Cleared")))
	}

	//def parseTestSubmit() = Authenticated(parse.json) { implicit request =>
	//	val reqBody: JsValue = request.body
	//	webappLogger.trace("request javalue received by server: " + reqBody.toString)
	//	val progContext = new ProgContext(Nil, Nil, Nil)
	//	val parserObj = new PSCSParser(true)
	//	val result =
	//		reqBody.asOpt[Seq[String]] match {
	//			case Some("Term" :: pInput :: Nil) =>
	//				val pr = parserObj.parseTerm(pInput)
	//				pr.toString
	//			case Some("TermInt" :: pInput :: Nil) =>
	//				val pr = parserObj.parseTermInt(pInput)
	//				pr.toString
	//			case Some("TermBool" :: pInput :: Nil) =>
	//				val pr = parserObj.parseTermBool(pInput)
	//				pr.toString
	//			case Some("TermArrayInt" :: pInput :: Nil) =>
	//				val pr = parserObj.parseTermArrayInt(pInput)
	//				pr.toString
	//			case Some("TermArrayBool" :: pInput :: Nil) =>
	//				val pr = parserObj.parseTermArrayBool(pInput)
	//				pr.toString
	//			case Some("FOLFormula" :: pInput :: Nil) =>
	//				val pr = parserObj.parseFOLFormula(pInput)
	//				pr.toString
	//			case x =>
	//				"Error in parsing the input: " + x
	//		}
	//	Ok(new Html(new StringBuilder("<div>"+ result + "</div>"))).as("text/html")
	//}

	//def parseResultToHtml(pr: ParseResult): Html = pr match {
	//	case Success(_, _) =>
	//	case Failure(_, _) =>
	//	case Error(_, _) =>
	//}

	def logClientMsg() = Authenticated(parse.json) { implicit request =>
		//webappLogger.trace(request.body)
		val arrOpt = request.body.asOpt[List[JsValue]]
		//webappLogger.trace("arrOPT ##############" + arrOpt)
		arrOpt match {
			case Some(arr) =>
			    for(jsVal <- arr) {
				    val logger = (jsVal \ "logger").asOpt[String]
				    val timestamp = (jsVal \ "timestamp").asOpt[Long]
				    val level = (jsVal \ "level").asOpt[String]
				    val url = (jsVal \ "url").asOpt[String]
				    val message = (jsVal \ "message").asOpt[String]
				    webappLogger.trace("SSSSSSSSSSS" + logger + timestamp + level + url + message)
				    (timestamp, message) match {
				        case (Some(time), Some(msg)) => {
				            val timedMsg = time + "\t" + msg
        				    (level, message) match {
						        case (Some("ERROR"), Some(msg)) => webappLogger.error(timedMsg)
						        case (Some("WARNING"), Some(msg)) => webappLogger.warn(timedMsg)
						        case (Some("INFO"), Some(msg)) => webappLogger.info(timedMsg)
						        case (Some("DEBUG"), Some(msg)) => webappLogger.debug(timedMsg)
						        case (Some("TRACE"), Some(msg)) => webappLogger.trace(timedMsg)
						        case _ => webappLogger.error("Unknown Message")
        				    }
				        }
				        case _ => webappLogger.error("Unknown Message")
				    }
			    }
			case _ => webappLogger.error("Unknown Message")
		}

	  Ok(new Html(new StringBuilder("")))
	}

}