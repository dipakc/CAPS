package capsstate

import scala.collection.mutable.Map
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import models.derivations.Derivations
import progsynth.synthesisnew.SynthTree
import progsynth.testobjects.DerivationScript
import models.User
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._

class OpenDerivationsRepo(val owner: User, private val mSynthTreeMap: Map[String, OpenDerivation]) {
	import OpenDerivationsRepo._

	private val logger = LoggerFactory.getLogger("progsynth.OpenDerivationsRepo")

	
	def isOpen(derivName: String): Boolean = mSynthTreeMap.exists { _._1 == derivName }
		
	def getOpenDerivations(): List[String] = mSynthTreeMap.keys.toList

	def getOpenDerivation(derivName: String): Try[OpenDerivation] = Try {
	    mSynthTreeMap.get(derivName).getOrElse{
	    		throw new RuntimeException(s"Derivation $derivName is not found.")
	    }
	}

	def canBeAdded(derivName: String): Try[Unit] = {
		if( isOpen(derivName) ) {
			logger.error(s"$derivName is already open.")
			Failure(new RuntimeException(s"$derivName is already open."))
		} else if (!isValidPath(derivName)) {
			logger.error(s"$derivName not a valid name.")
			Failure(new RuntimeException(s"$derivName not a valid name."))
		} else {
			Success()
		}
	}

	def add(derivName: String, openDerivation: OpenDerivation): Try[Unit] = Try {
		for(_ <- canBeAdded(derivName)) yield {
			mSynthTreeMap += (derivName -> openDerivation)
		}
	}
	

	def add(derivName: String, synthTree: SynthTree): Try[OpenDerivation] =
		for(_ <- canBeAdded(derivName)) yield {
			val od = new OpenDerivation(owner, derivName, synthTree)
			mSynthTreeMap += (derivName -> od)
			od
		}

	//abc.capstxt, abc_1.capstxt, abc_2.capstxt, ...
	def getFreshName(rawName: String, extn: String) = {
		var retVal = rawName
		while(isOpen(retVal + extn)){
		  retVal = getNextName(retVal)
		}
		retVal + extn
	}
}

object OpenDerivationsRepo {
	private val logger = LoggerFactory.getLogger("progsynth.OpenDerivationsRepo")
	
    def checkForValidPath(path: String): Try[String] = {
    	val FilePathR = """[A-Za-z0-9._\-]+""".r
    	if (FilePathR.pattern.matcher(path).matches && path.length <= 254) {
    	    Success(path)
    	}else {
    		val msg = """Derivation name not valid.
    	            \n Should match [A-Za-z0-9._-]+ and should be shorter than 254 characters."""
    		logger.error(msg)
    	    Failure(new RuntimeException(msg))
    	}
    }
    
    def isValidPath(path: String): Boolean = checkForValidPath(path) match {
    	case Success(_) => true
    	case Failure(_) => false
    }

    def getNextName(derivName: String) = {
        val DerivRE = """^(.*)_(\d+)""".r
        derivName match {
            case DerivRE(prefix, suffix) => prefix + "_" + (suffix.toInt + 1).toString
            case _ => derivName + "_1"
        }
    }
}
