package capsstate

import scala.collection.mutable.Map
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import models.derivations.Derivations
import progsynth.synthesisnew.SynthTree
import progsynth.testobjects.DerivationScript
import models.User
import jsonprinter.SynthTreePrinter
import progsynth.utils.RichTryT._

class OpenDerivationsMgr(	val owner: User,
							val repo: OpenDerivationsRepo)
{
	private val mFS = FileStorage(owner)
	
	def openNewEmptyDerivation(derivNameO: Option[String]): Try[OpenDerivation] = {
		val st = new SynthTree()
		
		for{
			savedName <- mFS.saveSynthTree(derivNameO, st).perr("Error in Saving the synthTree.")
			od <- repo.add(savedName, st).perr("Error in adding the derivation to repository")
		} yield od
	}

	def loadGalleryDeriv(derivName: String): Try[(String, OpenDerivation)] = {
		for {
			st <- Derivations.getDerivation(derivName).perr("Error in Derivations.getDerivation")
			savedName <- mFS.saveSynthTree(Some(derivName), st).perr("Error in Saving the synthTree.")
			od <- repo.add(savedName, st).perr("Error in adding the derivation to repository")
		} yield (savedName, od)
	}
	
}
