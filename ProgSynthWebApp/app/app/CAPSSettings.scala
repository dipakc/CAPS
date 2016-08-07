package app

import play.api.Application
import play.api.GlobalSettings

trait CAPSSettings extends GlobalSettings with ProverSettings {
	override def onStart(app: Application) {
		initConfig()
	}

	override def onStop(app: Application) {

	}
}
