package controllers

import scala.util.Failure
import scala.util.Success
import play.api.mvc.Controller
import play.api.templates.Html
import scalaz.Scalaz._
import progsynth.logger.PSLogUtils._
import progsynth.synthesisnew.SynthTree
import progsynth.utils.RichTryT._
import progsynth.utils.PSUtils._
import scala.util.Try
import java.io.StringWriter
import java.io.PrintWriter
import play.api.libs.json.JsValue
import play.api.libs.json.Json

trait ControllerOpenDerivation { self: ControllerAMain with Controller with Secured =>

	def getState(derivName: String) = traceBeginEnd("Application.getState") {

	    Authenticated { implicit request =>
		    val retT = for {
		    	state <- openDerivationsMgr.repo.getOpenDerivation(derivName).perr(s"Unable to open the derivation $derivName: ")
		    	synthTree <- Try(state.getState()).perr(s"Unable to get state: ")
		    	ret <- Try(views.xhtml.showState(synthTree)).perr(s"Failure in views.xhtml.showState: ")
		    } yield {
		    	ret
		    }
			retT match {
	        	case Success(v) => Ok(v)
	        	case Failure(e) =>
	        		webappLogger.error("Failure in getState(): " + e.getMessage() + getStackTraceString(e))
	        		InternalServerError(e.getMessage()) //Todo: user proper error.
			}
	    }
	}

	def applyTactic(derivName: String) = Authenticated(parse.json) { implicit request =>
	    val stateT = openDerivationsMgr.repo.getOpenDerivation(derivName)
	    val retT = for(state <- stateT) yield {
			state.applyTactic(request.body) match {
				case Success(synthTree) =>
					val curNode = synthTree.curNode
					curNode |> views.xhtml.showNodeState |>
					(Ok(_).as("text/html"))
				case Failure(e) =>
					webappLogger.error("Error in applyTactic: " + e.getMessage)
					InternalServerError("Internal ServerError: " + e.getMessage)
			}
	    }
	    retT match {
	        case Success(v) => v
	        case Failure(e) =>
	            val msg = "Error in applyTactic: " + e.getMessage()
	            println(getStackTraceString(e))
	        	webappLogger.error(msg + "\n" + getStackTraceString(e))
	        	InternalServerError(msg + e.getMessage())
	    }
	}

	def editTactic(derivName: String) = Authenticated(parse.json) { implicit request =>
	    val stateT = openDerivationsMgr.repo.getOpenDerivation(derivName)
	    val retT = for(state <- stateT) yield {
			state.editTactic(request.body) match {
				case Success(synthTree) =>
					val curNode = synthTree.curNode
					curNode |> views.xhtml.showNodeState |>
					(Ok(_).as("text/html"))
				case Failure(e) =>
					webappLogger.error("Error in editTactic: " + e.getMessage)
					InternalServerError("Internal ServerError: " + e.getMessage)
			}
	    }
	    retT match {
	        case Success(v) => v
	        case Failure(e) =>
	            val msg = "Error in applyTactic: " + e.getMessage()
	        	webappLogger.error(msg + "\n" + getStackTraceString(e))
	        	InternalServerError(msg + e.getMessage())
	    }
	}

	def setCurNode(derivName: String) = Authenticated(parse.json) { implicit request =>
	    webappLogger.trace("setCurNode called")
	    val openDerivationT = openDerivationsMgr.repo.getOpenDerivation(derivName)
	    val retT = openDerivationT map { openDeriv =>
	        openDeriv.setCurNode(request.body) match {
	            case Some(_) =>
	            	Ok(new Html(new StringBuilder("set current node successful")))
	            case None => InternalServerError("Internal ServerError: " + "set current Node failed")
	        }
	    }
	    retT match {
	        case Success(v) => v
	        case Failure(e) =>
	        	webappLogger.error("Error in setCurNode: " + e.getMessage())
	        	InternalServerError("Error in setCurNode: " + e.getMessage())
	    }
	}

	def deleteNode(derivName: String) = Authenticated(parse.json) { implicit request =>
	    webappLogger.trace("setCurNode called")
	    val openDerivationT = openDerivationsMgr.repo.getOpenDerivation(derivName)
	    val retT = openDerivationT map { openDeriv =>
	        openDeriv.deleteNode(request.body) match {
	            case Some(_) =>
	            	Ok(new Html(new StringBuilder("Delete node successful")))
	            case None => InternalServerError("Internal ServerError: " + "Delete Node failed")
	        }
	    }
	    retT match {
	        case Success(v) => v
	        case Failure(e) =>
	        	webappLogger.error("Error in setCurNode: " + e.getMessage())
	        	InternalServerError("Error in setCurNode: " + e.getMessage())
	    }
	}

	def resetTree(derivName: String) = Authenticated(parse.json) { implicit request =>
	    val stateT = openDerivationsMgr.repo.getOpenDerivation(derivName)
	    val retT = for(state <- stateT) yield {
			state.resetTree()
	    }
	    retT match {
	        case Success(_) => Ok(new Html(new StringBuilder("")))
	        case Failure(e) =>
	        	webappLogger.error("Error in resetTree: " + e.getMessage())
	        	InternalServerError("Error in resetTree: " + e.getMessage())
	    }
	}

	def getNodeTV(derivName: String, nodeId: Int) = Authenticated { implicit request =>
	    val stateT = openDerivationsMgr.repo.getOpenDerivation(derivName)
   	    val tvT: Try[JsValue] =
   	        for{
   	            state <- stateT
   	            tv <- state.getNodeTV(nodeId)
   	        } yield tv

   	    val retVal = tvT match {
   	        case Success (tv) => Ok(tv).withHeaders(CACHE_CONTROL -> "max-age=0")
   	        case Failure(e) =>
	        	webappLogger.error("Error in getNodeTV: " + e.getMessage())
	        	InternalServerError("Error in getNodeTV: " + e.getMessage())
        }

   	    retVal
	}

}