package progsynth.logger
import progsynth.PSPredef._
//import progsynth.logger.Logger.{ addElem, writexml }
import progsynth.printers.RichTree._
import scala.tools.nsc.Global

object FunLogger {
	val xlw = XMLLogWriter //alias
	def funlog[T, R](m: String, a: T)(f: (T) => R)(implicit global:Global): R = {
		xlw.writelog("""<fcall m="%s">""".format(m))
		val args =  <args><arg>{a.toLogString}</arg></args>
		xlw.writelog(args)
		val r = f(a)
		xlw.writelog(<retval>{ r.toLogString }</retval>)
		xlw.writelog("""</fcall>""")
		r
	}
	def funlog[T1, T2, R](m: String, a1: T1, a2: T2)(f: (T1, T2) => R)(implicit global:Global): R = {
		xlw.writelog("""<fcall m="%s">""".format(m))
		val args = <args><arg>{ a1.toLogString }</arg><arg>{  a2.toLogString }</arg></args>
		xlw.writelog(args)
		val r = f(a1, a2)
		xlw.writelog(<retval>{ r.toLogString }</retval>)
		xlw.writelog("""</fcall>""")
		r
	}
	def funlog[T1, T2, T3, R](m: String, a1: T1, a2: T2, a3: T3)(f: (T1, T2, T3) => R)(implicit global:Global): R = {
		xlw.writelog("""<fcall>""")
		xlw.writelog("""<fcall m="%s">""".format(m))
		val args = <args><arg>{ a1.toLogString }</arg><arg>{ a2.toLogString }</arg><arg>{ a3.toLogString }</arg></args>
		xlw.writelog(args)
		val r = f(a1, a2, a3)
		xlw.writelog(<retval>{ r.toLogString }</retval>)
		xlw.writelog("""</fcall>""")
		r
	}
	def funlog[T1, T2, T3, T4, R](m: String, a1: T1, a2: T2, a3: T3, a4: T4)(f: (T1, T2, T3, T4) => R)(implicit global:Global): R = {
		xlw.writelog("""<fcall m="%s">""".format(m))
		val args = <args><arg>{ a1.toLogString }</arg><arg>{ a2.toLogString }</arg><arg>{ a3.toLogString }</arg><arg>{ a4.toLogString }</arg></args>
		xlw.writelog(args)
		val r = f(a1, a2, a3, a4)
		xlw.writelog(<retval>{ r.toLogString }</retval>)
		xlw.writelog("""</fcall>""")
		r
	}
}

object FunLogger2 {

	def funlog[T, R](m: String, a: T)(f: T => R): R = {
		f(a)
	}
	def funlog[T1, T2, R](m: String, a1: T1, a2: T2)(f: (T1, T2) => R): R = {
		f(a1, a2)
	}
	def funlog[T1, T2, T3, R](m: String, a1: T1, a2: T2, a3: T3)(f: (T1, T2, T3) => R): R = {
		f(a1, a2, a3)
	}
	def funlog[T1, T2, T3, T4, R](m: String, a1: T1, a2: T2, a3: T3, a4: T4)(f: (T1, T2, T3, T4) => R): R = {
		f(a1, a2, a3, a4)
	}

}
