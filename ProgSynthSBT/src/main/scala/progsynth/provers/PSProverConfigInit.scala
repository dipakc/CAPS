package progsynth.provers
import scala.collection.mutable.Map

/* Config Hierarchy.
 * proverConf > z3Config
 * 			  > why3Config > why3AltErgoConfig
 * */
object PSProverConfigInit {
	val proverConfig = new Config(None, Map()){
		params("timeout") = "10000"
	}

	val z3Config = new Config(Some(proverConfig), Map()){
	}

	val why3Config = new Config(Some(proverConfig), Map()){
	}

	val why3AltErgoConfig = new Config(Some(why3Config), Map()){
	}

	val why3CVC3Config = new Config(Some(why3Config), Map()){
	}

	val why3SPASSConfig = new Config(Some(why3Config), Map()){
	}

	val why3Z3Config = new Config(Some(why3Config), Map()){
	}
}