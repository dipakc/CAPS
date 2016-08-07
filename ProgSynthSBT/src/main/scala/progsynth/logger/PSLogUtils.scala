package progsynth.logger
import scala.xml._
import org.slf4j.Logger
import progsynth.types.Term
import progsynth.printers.XHTMLPrinters2._

object PSLogUtils {
	def beginSection(title: String) = {
	    "BeginSection(" + title + ")"
	}

	def endSection(title: String) = {
	    "EndSection(" + title + ")"
	}

    def multiline(msg: String) = {
	    "BeginMultiLine" + "\n" + msg + "\n" + "EndMultiLine" + "\n"
    }

    def inlineList(msgs: List[Node]): String = {
        val msgDivs = msgs map {msg => <div class='PSListItem'>{msg}</div>}
        multiline(<div class='PSList'>{msgDivs}</div>.toString)
    }

	implicit class IdToSideEffect[A](val a: A) extends AnyVal {
	    def tap(fun: A => Unit): A = { fun(a); a}
	}

	def traceBeginEnd[A](msg:String)(f: =>A)(implicit logger: Logger): A = {
	    logger.trace(beginSection(msg))
	    val retVal = f
	    logger.trace(endSection(msg))
	    retVal
	}

	def debugBeginEnd[A](msg:String)(f: =>A)(implicit logger: Logger): A = {
	    logger.debug(beginSection(msg))
	    val retVal = f
	    logger.debug(endSection(msg))
	    retVal
	}

	def errorBeginEnd[A](msg:String)(f: =>A)(implicit logger: Logger): A = {
	    logger.error(beginSection(msg))
	    val retVal = f
	    logger.error(endSection(msg))
	    retVal
	}

	def infoBeginEnd[A](msg:String)(f: =>A)(implicit logger: Logger): A = {
	    logger.info(beginSection(msg))
	    val retVal = f
	    logger.info(endSection(msg))
	    retVal
	}

	def warnBeginEnd[A](msg:String)(f: =>A)(implicit logger: Logger): A = {
	    logger.warn(beginSection(msg))
	    val retVal = f
	    logger.warn(endSection(msg))
	    retVal
	}

	def traceTerm(label: String, term:Term)(implicit logger: Logger): Unit = {
	    logger.trace(label)
	    logger.trace(multiline(termToHtml(term).toString))
	}

	def traceTerm(term:Term)(implicit logger: Logger): Unit = {
	    logger.trace(multiline(termToHtml(term).toString))
	}

}

