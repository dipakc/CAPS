package progsynth.logger
import progsynth.PSPredef._
import XMLLogWriter._

/** Methods for logging case statements.
 * All these methods return false
 * Usage:
 * {{{
 * abc match {
 * 	case xxx => ppp
 * 	case yyy => qqq
 * 	case _ => rrr
 * }
 * }}}
 *
 * should be implemented as
 * {{{
 * abc match {
 * 	case _ if casestart("case_xxx") => zzz
 * 	case xxx => ppp
 * 	case _ if casemiddle("case_xxx") => zzz
 * 	case yyy => qqq
 * 	case _ if casemiddle("case_xxx") => zzz
 * 	case _ => rrr
 * }
 * caseend()
 * }}}
 */
object CaseStmtLogger {
	def casestart(msg: String) = {
		writelog("""<case name="%s">""".format(msg))
		false
	}
	def casemiddle(msg: String) = {
		writelog("""</case><case name="%s">""".format(msg))
		false
	}
	def caseend() = {
		writelog("""</case>""".format())
		false
	}
}

object CaseStmtLogging2 {
	def casestart(msg: String) = false
	def casemiddle(msg: String) = false
	def caseend() = false
}
