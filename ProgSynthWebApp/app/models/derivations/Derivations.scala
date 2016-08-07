package models.derivations
import progsynth.synthesisnew._
import progsynth.types._
import progsynth.types.Types._
import progsynth.{utils=>psu, _}
import progsynth._
import progsynth.ProgSynth._
import progsynth.testobjects._
import scala.util._
import models.derivations.scripts._
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._

object Derivations {

	val logger= LoggerFactory.getLogger("progsynth.xxx")

	def initDerivation(): SynthTree = {
		TTFFAuto()
	}

	def testDerivations() = {
		List(PropagateAssertionDownSPTest, PropagateAssertionDownSPTestBase)
	}

	def getDerivationObjs(): List[DerivationScript] = {
		val addTestEntries = true
		//LOC_GalleryEntries
        //val galleryEntries = List(TTFF7NoBranching, /*TTFF10, */intSqrt, intDiv, intDivMacroTest, arrayMin, binarySearch, /*TTFF7, */allTrue, Max)
        //val galleryEntries = List(intSqrt, intDiv, ArraySwap, allTrue)
		val galleryEntries = List(IntSqrt, IntDiv, LinearSearch, BinarySearch,
				ExistsTrue, TTFF7, TTFF71, TTFF7NoBranching, TTFF8, TTFF9,
				DutchNationalFlag, MaxSegSum, HornersRule, ArrayMax, ArrayMin, ArraySum, ArrayProd, ArraySwap, SelectionSort)//xx

		// TODO: top down ordering for : TTFF7, TTFF71, TTFF7NoBranching, TTFF8, TTFF9,
		galleryEntries ++ testDerivations
    }

	def galleryDerivations(): List[String] = {
	    getDerivationObjs.map(_.name)
	}

	def getDerivation(derivName: String): Try[SynthTree] = {

	    val derivationObjs = getDerivationObjs.filter(obj => obj.name == derivName)
	    derivationObjs match {
		    case derivObj :: Nil => Success(derivObj.apply())
	        case Nil =>
	        	logger.error("Error in getDerivation: " + "Derivation with name " + derivName + " not found")
	        	Failure(throw new RuntimeException("Derivation with name " + derivName + " not found") )
	        case _ =>
	        	logger.error("Error in getDerivation: " + "Multiple derivations found with name " + derivName)
	        	Failure(throw new RuntimeException("Multiple derivations found with name " + derivName) )
	    }
	}
}