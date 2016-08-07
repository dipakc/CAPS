package app

import play.api.Play
import scala.collection.JavaConversions.asScalaBuffer
import org.slf4j.LoggerFactory //
import progsynth.logger.PSLogUtils._

trait ProverSettings {

	def initConfig() = {
		val logger= LoggerFactory.getLogger("progsynth.ProverSettings")

	    val config = Play.current.configuration

	    var configMap: Map[String, Any] = Map()

	    config.getString("application.data") match {
	        case Some(tmpPath) => configMap += ("application.data" -> tmpPath)
	        case None => throw new RuntimeException("application.data not set in the config file")
	    }

	    for (z3Path <- config.getString("provers.path.z3")) {
	        configMap += ("provers.path.z3" -> z3Path)
	    }
	    for( why3Path <- config.getString("provers.path.why3")) {
	        configMap += ("provers.path.why3" -> why3Path)
	    }

	    for( why3LibPath <- config.getString("provers.path.why3lib")) {
	        configMap += ("provers.path.why3lib" -> why3LibPath)
	    }

	    config.getStringList("provers.sequence") match {
	        case Some(sequence) =>
	            configMap += ("provers.sequence" -> sequence.toList)
	        case None => throw new RuntimeException("provers.sequence not set in the config file")
	    }

	    config.getString("ostype") match {
	        case Some(ostype) =>
	            configMap += ("ostype" -> ostype)
	        case None => throw new RuntimeException("ostype not set in the config file")
	    }

	    //initialize the progsynth config map
	    progsynth.config.AppConfig.configMap ++= configMap
	    logger.trace("ConfigMap Start ##################")
	    logger.trace(progsynth.config.AppConfig.configMap.toString)
	    logger.trace("ConfigMap End ##################")
	}
}
