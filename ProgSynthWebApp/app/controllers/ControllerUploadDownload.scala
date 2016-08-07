package controllers

import java.io.File
import java.io.FileOutputStream

import scala.util.Failure
import scala.util.Success

import capsstate.OpenDerivation
import play.api.mvc.Controller
import progsynth.logger.PSLogUtils.IdToSideEffect
import progsynth.logger.PSLogUtils.traceBeginEnd
import scalax.io.Input
import scalax.io.Output
import scalax.io.Resource


trait ControllerUploadDownload { self: ControllerAMain with Controller with Secured =>
	def downloadDerivationBin(derivName: String) = Authenticated { implicit request =>
    	openDerivationsMgr.repo.getOpenDerivation(derivName) map { state =>
    	    val derivation: Array[Byte] = state.serializeDerivation()
			val fileName = "tmp/" + request.user.email + derivName + ".caps"
			val out:Output =  Resource.fromOutputStream( new FileOutputStream(fileName))
			out.write(derivation)

			Ok.sendFile(
				content = new java.io.File(fileName),
				fileName = _ => derivName + ".caps"
			)
    	} match {
    	    case Success(v) => v
    	    case Failure(e) =>
    	    	webappLogger.error("Error in downloadDerivationBin: " + e.getMessage())
    	    	BadRequest(e.getMessage())
    	}
	}

	def downloadDerivationTxt(derivName: String) = Authenticated { implicit request =>
    	openDerivationsMgr.repo.getOpenDerivation(derivName) map { state =>
    	    val derivation: String = state.derivationToString()
			val fileName = "tmp/" + request.user.email + derivName + ".capstxt"
			val out:Output =  Resource.fromOutputStream( new FileOutputStream(fileName))
			out.write(derivation)

			Ok.sendFile(
				content = new java.io.File(fileName),
				fileName = _ => derivName + ".capstxt"
			)
    	} match {
    	    case Success(v) => v
    	    case Failure(e) =>
    	    	webappLogger.error("Error in downloadDerivationTxt: " + e.getMessage())
    	    	BadRequest(e.getMessage())
    	}
	}

    def upload() = Authenticated(parse.multipartFormData) { implicit request =>

        def onError(msg: String) = {
        	webappLogger.error("Error in upload: " + msg + "\n" + "Redirecting to upload form: ")
        	Redirect(routes.Application.uploadForm).flashing("error" -> msg)
        }

        request.body.file("derivation").map { derivation =>

            val (derivName, isBinary) = {
        		val fileName = derivation.filename
        		val binFileRE = """(.*)\.caps$""".r
        		val txtFileRE = """(.*)\.capstxt$""".r
        		fileName match {
        			case binFileRE(fileName) => (fileName, true)
        			case txtFileRE(fileName) => (fileName, false)
        			case _ =>
        				throw new RuntimeException(
        					"Unknown file types. Only .caps and .capstxt files are supported")
        		}
            }
            def uploadBinary() = {
	            val derivBytes = {
		    		val toFileName = "tmp/" + request.user.email + "derivation"
					derivation.ref.moveTo(new File(toFileName ), true)
					val in:Input = Resource.fromFile(toFileName)
	                in.byteArray
	            }

	            openDerivationsMgr.openNewEmptyDerivation(Some(derivName)) map {synthTreeW: OpenDerivation =>
		            val res = synthTreeW.deserializeDerivation(derivBytes )
		            res match {
		                case Success(v) => Redirect(routes.Application.derivation(derivName))
		                				   .flashing("msg" -> "file loaded successfully")
		                case Failure(e) => onError("Failed to deserialize the file:" + e.getMessage())
		            }
	            } match {
	                case Success(v) => v
	                case Failure(e) => {
	                    webappLogger.error(e.getMessage())
	                    BadRequest(e.getMessage())
	                }
	            }
            }

            def uploadText() = {
	            val derivString = {
		    		val toFileName = "tmp/" + request.user.email + "derivation"
					derivation.ref.moveTo(new File(toFileName ), true)
					val in:Input = Resource.fromFile(toFileName)
	                in.string
	            }

	            openDerivationsMgr.openNewEmptyDerivation(Some(derivName)) map {state: OpenDerivation =>
		            val res = state.replayDerivation(derivString)
		            res match {
		                case Success(v) => Redirect(routes.Application.derivation(derivName))
		                				   .flashing("msg" -> "file loaded successfully")
		                case Failure(e) => onError("Failed to deserialize the file:" + e.getMessage())
		            }
	            } match {
	                case Success(v) => v
	                case Failure(e) => {
	                    webappLogger.trace("XXXXXXXXXX: " + e.getMessage())
	                    BadRequest(e.getMessage())
	                }
	            }
            }

            if(isBinary)
            	uploadBinary()
            else
            	uploadText()

        }.getOrElse {
            onError("Missing File")
        }
    }


	def uploadForm() = traceBeginEnd("Application.uploadForm") {
	    Authenticated { implicit request =>
	    	//Ok(views.html.uploadForm.apply())
	    	Ok(views.UploadFormView())
	    } tap {_ => webappLogger.trace("upload form action created")}
	}
}