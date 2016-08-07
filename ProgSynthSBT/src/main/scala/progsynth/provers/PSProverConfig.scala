package progsynth.provers
import scala.collection.mutable.Map

class Config(val parentOpt: Option[Config], protected val params: Map[String, String]) {
    def getParam(key: String): Option[String] = {
        params.get(key).orElse{
            parentOpt.flatMap(_.getParam(key))
        }
    }

    def setParam(key: String, value: String) = {
        params(key) = value
    }
}
