package expt
import scala.tools.nsc.interpreter._
import scala.tools.nsc.interpreter.ILoop._
import progsynth.debug.PSDbg._


//object ReplDebugger {
//  def main(args: Array[String]) {
//    0 to 10 foreach { i =>
//      breakIf(i == 5, "i" -> i, "j" -> i)
//      logln(i)
//      if (i == 7) break(Nil)
//    }
//  }
//}