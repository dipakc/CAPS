package progsynth.printers
import progsynth.logger.XMLLogWriter
import progsynth.config.AppConfig

trait XmlLogFileEvents {
	def xmlLogFileStart() = {
        XMLLogWriter.init(AppConfig.logFile)
        XMLLogWriter.writelog("<psxmllog>")
	}
	def xmlLogFileEnd() = {
		XMLLogWriter.writelog("</psxmllog>")
		XMLLogWriter.close()
	}
	def xmlLogFileCUStart() = {
		XMLLogWriter.writelog("<srcfile>")
	}
	def xmlLogFileCUEnd() = {
		XMLLogWriter.writelog("</srcfile>")
	}
}
