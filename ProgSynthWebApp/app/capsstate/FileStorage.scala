package capsstate

import models.User
import progsynth.synthesisnew.SynthTree
import models.derivations.Derivations
import controllers.Secured
import play.api.mvc.Controller
import models.parseruntyped.SynthTreeParser
import scala.util.Try
import scalax.io._
import java.io.File
import scalax.io.Output
import org.slf4j.LoggerFactory
import play.api.Play
import progsynth.utils.RichTryT._

class FileStorage(val user: User) extends FileStorageUtils {

	import FileStorage._

	implicit val logger= LoggerFactory.getLogger("progsynth.webapp.filestorage")

	val config = Play.current.configuration

	def root: String = {
	    val dataDir = config.getString("application.data")
	    val userDataDir = dataDir.get  + "/users/" + user.email
	    userDataDir + "/derivations"
	}

	def getDerivString(derivName: String): String = {
		val filePath = root + "/" + derivName
		val in:Input = Resource.fromFile(filePath)
	    in.string
	}

	def getPathFromName(derivName: String): String = {
	    root + "/" + derivName
	}

	def getDerivationNames(): Try[List[String]] = Try {
	    logger.trace("root: " + root)
		val firstLevelFilesInRootFolder = new File(root).listFiles.map(_.getName).toList
		firstLevelFilesInRootFolder
	}

	def contains(derivName: String): Try[Boolean] =
		getDerivationNames().map(_ contains derivName)

	def saveDerivString(derivName: String, synthTreeStr: String): Try[String] = Try {
		val filePath = root + "/" + derivName
		val out:Seekable = Resource fromFile filePath
		out truncate 0
	    out write synthTreeStr
	    derivName
	}

	def saveDerivStringFresh(baseName: String, synthTreeStr: String): Try[String] = {
		val derivName = getFreshName(baseName, ".capstxt")
		saveDerivString(derivName, synthTreeStr)
	}

	def saveDerivStringFresh(synthTreeStr: String): Try[String] = {
		saveDerivStringFresh("new", synthTreeStr)
	}

	def saveDerivStringFresh(baseNameO: Option[String], synthTreeStr: String): Try[String] = {
		baseNameO match {
			case Some(baseName) => saveDerivStringFresh(baseName, synthTreeStr)
			case None => saveDerivStringFresh(synthTreeStr)
		}
	}

	def saveSynthTree(baseNameO: Option[String], st: SynthTree): Try[String] = {
		import jsonprinter.SynthTreePrinter

		for {
			stStr <- SynthTreePrinter(st).synthTreeToJson().perr("Error in synthTreeToJson.")
			derivName <- saveDerivStringFresh(baseNameO, stStr).perr("Error in saveDerivStringFresh.")
		} yield
			derivName

	}
}

trait FileStorageUtils { self: FileStorage =>

	import FileStorage._

	protected def getFreshName(rawName: String, extn: String): String = {
		var retVal = rawName
		while(contains(retVal + extn).get){
		  retVal = getNextName(retVal)
		}
		retVal + extn
	}

}

object FileStorage {
	def apply(user: User): FileStorage = new FileStorage(user)

    def getNextName(derivName: String) = {
        val DerivRE = """^(.*)_(\d+)""".r
        derivName match {
            case DerivRE(prefix, suffix) => prefix + "_" + (suffix.toInt + 1).toString
            case _ => derivName + "_1"
        }
    }
}
