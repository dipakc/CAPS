package progsynth.config
import progsynth.PSPredef._

trait PSConfig {
	var configMap: Map[String, Any]
}

object AppConfig extends PSConfig {
	val logFile = "c:\\Temp\\progsynth.xml"
	val resultFile = "c:\\Temp\\progsynthresult.html"
	var resultFileName: Option[String] = None
	val resultDir = """D:\EclipseProjects\ProgSynthSBT\target\scala-2.9.1\test-classes\output"""

	//Following does not work in case of plugins. Test cases that extend PSPluginTester.
	//val resultDir = getClass.getResource("/output").getPath.replace("/", "\\").tail

	val annCodeFile = "c:\\Temp\\annCode.html"
	/** Synthesize unknown program fragments */
	var synthUnk =  true

	/* set by app.Global.onStart */
	var configMap: Map[String, Any] = Map()
}
