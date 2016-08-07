package progsynth.logger
import java.io._
import scala.xml.{Elem, Node, XML}
import scala.xml.transform
import scala.xml.transform.RewriteRule
import scala.xml.transform.RuleTransformer
import progsynth.PSPredef._
import progsynth.utils.PSUtils
import progsynth.config.AppConfig

object XMLLogWriter {
	var fw: Option[FileWriter] = None

	def init(fileName: String) {
		if (!fw.isDefined) {
			fw = Some(new FileWriter(fileName, false)) //overwrite
		} else {
			//TODO: Investigate why init is called twice before close during sbt "test"
			fw.get.close()
			fw = Some(new FileWriter(fileName, false)) //overwrite
			//throw new RuntimeException("Can not init fw. Already has some value")
		}
	}

	def close() = if(fw.isDefined) fw.get.close()

	def writelog(line: Any) = {
		try {
			if(fw.isDefined) fw.get.write(line.toString)
		}
		catch{
			case _ => fw.get.close()
		}
	}
}

