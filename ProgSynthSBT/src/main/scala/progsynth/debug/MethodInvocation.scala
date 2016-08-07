package progsynth.debug
import progsynth.debug.PSDbg._
import scala.util.control.Exception._

object MethodInvocation {
	implicit def toMethods(obj: AnyRef) = new {
		def zeroArityMethods = obj.getClass.getMethods.filter(_.getParameterTypes().length == 0).toList
		def methods = obj.getClass.getMethods.toList
	}

	type Item = (String, Object)

	def invokeMethods(obj: Object, num: Int): List[String] = {
		invokeMethods(obj).take(num).toList
	}

	def invokeMethods(obj: Object): Stream[String] = {
		val stream = invokeMethods(Stream(("obj", obj)))
		stream map {
			case (desc, obj) =>
				desc + " ===> " + obj
		}
	}

	private def invokeMethods(list: Stream[Item]): Stream[Item] = {
		list #::: (invokeMethods(invokeMethodsNxt(list)))
	}

	private def invokeMethodsNxt(list: Stream[Item]): Stream[Item] = {
		val abc =
			for ((objStr, obj) <- list; if(obj != null)) yield {
				for (m <- obj.zeroArityMethods) yield {
					allCatch opt {
						val nxtObjStr = objStr + "." + m.getName()
						val nxtObj = m.invoke(obj)
						(nxtObjStr, nxtObj)
					}
				}
			}
		abc.flatten.flatten
	}

	private def invokeMethodsNxt2(list: Stream[Item]): Stream[Item] = {
		for {
			(objStr, obj) <- list
			if (obj != null)
			m <- obj.zeroArityMethods
			nxtObjStr = objStr + "." + m.getName()
			nxtObj <- allCatch opt m.invoke(obj)
		} yield {
			(nxtObjStr, nxtObj)
		}
	}
}

object PSDbg {
	def writeln1(arg: Any) = println(arg)//TODO: rename to println1
	def logln(arg: Any) = println("[PSInfo] " + arg)
	def writeln0(arg: Any) = ()
}