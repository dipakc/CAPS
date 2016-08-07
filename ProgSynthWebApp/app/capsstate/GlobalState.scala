package capsstate
import play.api.libs.json.JsValue
import scala.sys.process._
import progsynth.synthesisnew._
import progsynth.types._
import progsynth._
import progsynth._
import progsynth.synthesisnew.SynthUtils._
import progsynth.synthesisold.ProgContext
import models.parseruntyped.JsonDataCSParser
import models.User
import scala.collection.mutable.Map
import scala.util.Try
import scala.util._
import models.derivations._
import models.derivations.scripts._
import progsynth.testobjects.DerivationScript


/**
 * - Holds a map of "(user, derivName) -> userState"
 * - All the derivations in the map are loaded in the memory.
 */
object GlobalState {

	private val mUserStateMap: Map[User, OpenDerivationsMgr] = Map()
	
	def getOpenDerivationsMgr(user: User) = {
		if ( ! mUserStateMap.contains(user) ) {
			mUserStateMap += (user -> new OpenDerivationsMgr(user, new OpenDerivationsRepo(user, Map())))
		}
		mUserStateMap.get(user).get
	}
}
