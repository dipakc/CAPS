package progsynth.utils

import scala.util.Try
import scala.util.Failure

/**
 * Usage: import progsynth.utils
 * */
object RichTryT extends RichTryT
trait RichTryT {

	implicit class RichTry[T](t: Try[T]) {
		
		def perr(msg: String) = t.recoverWith(prependMsg(msg))
		
		private def prependMsg[T](msg: String):  PartialFunction[Throwable, Try[T]] = {
			case e =>
				val newExc = new RuntimeException(msg + e.getMessage)
				newExc.setStackTrace(e.getStackTrace())
				Failure(newExc)
		}
	}

}