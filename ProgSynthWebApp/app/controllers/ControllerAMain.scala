package controllers

import scala.util.Failure
import scala.util.Success
import scala.util.Try
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._
import capsstate.FileStorage
import capsstate.GlobalState
import models.derivations.Derivations
import models.parseruntyped.SynthTreeParser
import play.api.libs.json.Json
import play.api.mvc.Controller
import progsynth.logger.PSLogUtils.IdToSideEffect
import progsynth.logger.PSLogUtils.traceBeginEnd
import progsynth.synthesisnew.SynthTree
import views.html
import capsstate.OpenDerivation
import progsynth._
import progsynth.utils.PSUtils._
import play.api.mvc.WithHeaders
import play.api.libs.json.JsValue
import play.api.libs.json.JsArray
import java.io.File
import play.api.Routes
import play.api.mvc.Action


trait ControllerAMain extends Controller with Secured
	with ControllerOpenDerivation with ControllerDebug with ControllerUploadDownload {
    import views._


	implicit val webappLogger= LoggerFactory.getLogger("progsynth.webapp")

	protected def rootUrl[T](implicit request: AuthenticatedRequest[T]) =
	    routes.Application.derivations().absoluteURL()

	protected def openDerivationsMgr[T](implicit request: AuthenticatedRequest[T]) =
		GlobalState.getOpenDerivationsMgr(request.user)

	protected def fileStorage[T](implicit request: AuthenticatedRequest[T]) =
		new FileStorage(request.user)

	def index() = traceBeginEnd("Application.index") {
	    Authenticated { implicit request =>
	    	val email = request.user.email
	    	val openDs = openDerivationsMgr.repo.getOpenDerivations()
	        //Ok(html.derivation.apply(email, rootUrl(request), openDs))
	    	Ok(views.DerivationView.derivation(email, rootUrl(request), openDs))
	    } tap {_ => webappLogger.trace("Index action created")}
	}

	def openDerivations() = traceBeginEnd("Application.openDerivations") {
	    Authenticated { implicit request =>
	    	val email = request.user.email
	    	val activeDerivations = openDerivationsMgr.repo.getOpenDerivations()
	    	//Ok(html.openDerivations.apply(email, activeDerivations))
	    	Ok(views.OpenDerivationsView.openDerivations(email, activeDerivations))
	    } tap {_ => webappLogger.trace("Index action created")}
	}


	def derivations() = Authenticated { implicit request =>
	    	val email = request.user.email
	    	val savedDerivationsT = fileStorage.getDerivationNames()
	    	webappLogger.trace("savedDerivationsT")
	    	webappLogger.trace(savedDerivationsT.toString)
	    	savedDerivationsT match {
	    		case Success(savedDerivations) =>
	    			//Ok(html.derivations.apply(email, savedDerivations))
	    		    Ok(views.DerivationsView.derivations(email, savedDerivations))
	    		case Failure(e) =>
	    			val msg = "Unable to get saved derivation list for the user: " + e.getMessage()
	    			val msg2 = msg + "\n" + "root: " + fileStorage.root
	    			webappLogger.error(msg2)
	    			InternalServerError(msg2)
	    	}
	}

	//Serves JSON response
	def getDerivations() = Authenticated { implicit request =>
	    val savedDerivationsT = fileStorage.getDerivationNames()
        savedDerivationsT match {
	    		case Success(savedDerivations) =>
	    		    Ok(Json.toJson(savedDerivations)).withHeaders(CACHE_CONTROL -> "max-age=0")
	    		case Failure(e) =>
	    			val msg = "Unable to get saved derivation list for the user: " + e.getMessage()
	    			webappLogger.error(msg)
	    			InternalServerError(msg)
	    }
	}

	def rename() = Authenticated(parse.json) { implicit request =>

	    def validateInput(oldName: String, newName: String): Try[Unit] = Try {

	        //Check if the destination name is different
	        if (oldName == newName) {
	            throw new RuntimeException("New name is the same as the current name")
	        }

	        //Check if the new name already exists
	        fileStorage.contains(newName) match {
	            case Success(x) =>
	                if (x)
	                    throw new RuntimeException(s"Derivation $newName already exists")
	                else
	                    ()
	            case Failure(e) =>
	                throw e
	        }
	    }

	    def rename2(oldName: String, newName: String ): (Boolean, String) = {

	        validateInput(oldName, newName) match {
	            case Success(_) =>
	            case Failure(e) => return (false, e.getMessage)
	        }

            val oldPath = fileStorage.getPathFromName(oldName)
            val newPath = fileStorage.getPathFromName(newName)

            Try(new File(oldPath).renameTo(new File(newPath))) match {
                case Success(x) =>
                    if (x)
                        (true, "Rename successful")
                    else
                        (false, "Failed to rename. Unknown error.")
                case Failure(y) =>
                    (false, y.getMessage)
            }

	    }

        val List(oldName, newName) = request.body.as[List[String]]

        val (status, msg) = rename2(oldName, newName)
        val retVal = JsArray(Json.toJson(status) :: Json.toJson(msg) :: Nil)

        Ok(retVal)
    }

	def copy() = Authenticated(parse.json) { implicit request =>

	    def copyFile(srcPath: String, destPath: String) = {
            import java.io.{File,FileInputStream,FileOutputStream}
            var srcStream: FileInputStream = null
            var destStream: FileOutputStream = null
            try{
            	val src = new File(srcPath)
            	val dest = new File(destPath)
            	srcStream = new FileInputStream(src)
            	destStream = new FileOutputStream(dest)
                destStream getChannel() transferFrom(  srcStream getChannel, 0, Long.MaxValue )
            } finally {
                    srcStream.close()
                    destStream.close()
            }
	    }

	    def validateInput(oldName: String, newName: String): Try[Unit] = Try{

	        //Check if the destination name is different
	        if (oldName == newName) {
	            throw new RuntimeException("New name is the same as the current name")
	        }

	        //Check if the new name already exists
	        fileStorage.contains(newName) match {
	            case Success(x) =>
	                if (x)
	                    throw new RuntimeException(s"Derivation $newName already exists")
	                else
	                    ()
	            case Failure(e) =>
	                throw e
	        }
	    }

	    def copy2(oldName: String, newName: String ): (Boolean, String) = {

	        validateInput(oldName, newName) match {
	            case Success(_) =>
	            case Failure(e) => return (false, e.getMessage)
	        }

            val oldPath = fileStorage.getPathFromName(oldName)
            val newPath = fileStorage.getPathFromName(newName)

            Try(copyFile(oldPath, newPath)) match {
                case Success(_) =>
                   (true, "Copy successful")
                case Failure(y) =>
                    (false, y.getMessage)
            }
	    }

        val List(oldName, newName) = request.body.as[List[String]]

        val (status, msg) = copy2(oldName, newName)
        val retVal = JsArray(Json.toJson(status) :: Json.toJson(msg) :: Nil)

        Ok(retVal)
    }

	def delete() = Authenticated(parse.json) { implicit request =>

	    def validateInput(deriv: String): Try[Unit] = {

	        //Check if the derivation exists
	        fileStorage.contains(deriv) match {
	            case Success(x) =>
	                if (x)
	                    Success(())
	                else
	                    throw new RuntimeException(s"Derivation $deriv does not exist")
	            case Failure(e) =>
	                throw e
	        }
	    }

	    def delete2(deriv: String): (Boolean, String) = {

	        validateInput(deriv) match {
	            case Success(_) =>
	            case Failure(e) => return (false, e.getMessage)
	        }

            val derivPath = fileStorage.getPathFromName(deriv)

            Try(new File(derivPath).delete()) match {
                case Success(x) =>
                    if (x)
                        (true, "Delete successful")
                    else
                        (false, "Failed to delete. Unknown error.")
                case Failure(y) =>
                    (false, y.getMessage)
            }

	    }

        val derivs = request.body.as[List[String]]

        val statusMsgList: List[(Boolean, String)] = derivs map delete2

        //val retVal = JsArray(Json.toJson(status) :: Json.toJson(msg) :: Nil)
        val retVal = Json.toJson {
            for {
                (status, msg) <- statusMsgList
            } yield {
                JsArray(Json.toJson(status) :: Json.toJson(msg) :: Nil)
            }
        }

        Ok(retVal)
	}

	def derivation(derivName: String) = Authenticated { implicit request =>
	    val activeDerivations = openDerivationsMgr.repo.getOpenDerivations()
	    //Load the derivation if it is not in the memory.
	    val statusT: Try[Unit] = {
	    	if (!activeDerivations.contains(derivName)) {
		    	val derivStr = fileStorage.getDerivString(derivName)
		    	val synthTreeT = SynthTreeParser.jsonToSynthTree(derivStr)
		    	for(synthTree <- synthTreeT ) yield {
		    		openDerivationsMgr.repo.add(derivName, synthTree)
		    	}
		    }  else
		    	Success(())
	    }
	    statusT match {
	    	//case Success(_) => Ok(html.derivation.apply(request.user.email, rootUrl(request), activeDerivations))
	        case Success(_) => Ok(views.DerivationView.derivation(request.user.email, rootUrl(request), activeDerivations))
	    	case Failure(status) =>
	    		val msg = "Failed to read the derivation: " + status.getMessage()
				webappLogger.error(msg)
	    		InternalServerError(msg)
	    }
	}

	def newDerivation() = Authenticated { implicit request =>
		val openDerivT: Try[OpenDerivation] = openDerivationsMgr.openNewEmptyDerivation(None)
		openDerivT match {
		    //case Success(state) => Redirect(routes.Application.derivation(derivName))
		    case Success(openDeriv) => Ok(Json.toJson(openDeriv.derivName))
		    case Failure(e) =>
		    	val msg = e.getMessage()
		    	webappLogger.error(msg)
		    	InternalServerError(msg) //Todo: user proper error.
		}
	}

	//Refactor: Move gallery derivations to separate controller
	//Serves a Json response
	def galleryDerivations () = Authenticated { implicit request =>
	    val dl = Derivations.galleryDerivations()
	    Ok(Json.toJson(dl))
	}

	def loadGalleryDeriv(derivName: String) = Authenticated(parse.json) { implicit request =>
        openDerivationsMgr.loadGalleryDeriv(derivName) map { case (newName, state) =>
        	Ok(Json.toJson(newName))
        } match {
            case Success(v) => v
            case Failure(e) =>
            	val msg = s"Error on loading the derivation $derivName :" + e.getMessage()
            	webappLogger.error(msg + getStackTraceString(e))
            	InternalServerError(msg)
        }
	}


	// javascript routing
	// https://www.playframework.com/documentation/2.1.x/ScalaJavascriptRouting
    def jsRoutes = Action { implicit request =>
        Ok(
            Routes.javascriptRouter("jsRoutes")(
        		routes.javascript.Application.derivations,
        		routes.javascript.Application.getDerivations,
            	routes.javascript.Application.rename,
        		routes.javascript.Application.delete,
        		routes.javascript.Application.copy,
        		routes.javascript.Application.getNodeTV,
            	routes.javascript.Application.openDerivations,
                routes.javascript.Application.derivation,
                routes.javascript.Application.getState,
                routes.javascript.Application.galleryDerivations,
                routes.javascript.Application.loadGalleryDeriv,
                routes.javascript.Application.applyTactic,
                routes.javascript.Application.editTactic,
                routes.javascript.Application.setCurNode,
                routes.javascript.Application.deleteNode,
                routes.javascript.Application.resetTree,
                routes.javascript.Application.newDerivation,
                routes.javascript.Application.downloadDerivationBin,
                routes.javascript.Application.downloadDerivationTxt,
                routes.javascript.Application.uploadForm,
                routes.javascript.Application.upload,
                routes.javascript.Application.parseTest,
                routes.javascript.Application.parseTestSubmit,
                routes.javascript.Application.logClientMsg,
                routes.javascript.Authentication.login,
                routes.javascript.Authentication.authenticate,
                routes.javascript.Authentication.logout
                )
        ).as("text/javascript")

        //Ok( "Hello")
    }

}
