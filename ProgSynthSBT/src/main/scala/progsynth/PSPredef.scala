package progsynth
import scala.tools.nsc._
import printers.RichTree._
import types._
import scala.tools.nsc.Global
//import progsynth.printers.XMLPrinters.toXml
import progsynth.printers.ProgramPPrinter._

object PSPredef {
	implicit def anyToRichLogger(x: Any) = new RichLogger(x)

	class RichLogger(val x: Any){
		//Return type of this function is Any since some of the cases return xml element.
		def toLogString(implicit global: Global): Any = {
			val progAnnOpt = 2
			val treeOpt = 0
			x match {
				case y: ProgramAnn if progAnnOpt == 0 => prettyPrint(y)
				//case y: ProgramAnn if progAnnOpt == 1 => toXml(y)
				//case y: ProgramAnn if progAnnOpt == 2 => <xr>{toXml(y)}</xr><pp>{prettyPrint(y)}</pp>
				case y: Global#Tree if treeOpt == 0 => y.pprint
				//case y: Global#Tree if treeOpt == 1 => toXml(y)

				case Some(y) => y.toLogString
				case None => "None"
				case (y1, y2) => "(" + y1.toLogString+ ", " +
								y2.toLogString + ")"
				case (y1, y2, y3) => "(" + y1.toLogString + ", " +
								y2.toLogString + ", "
								y3.toLogString + ")"
				case (y1, y2, y3, y4) => "(" + y1.toLogString + ", " +
								y2.toLogString + ", "
								y3.toLogString + ", "
								y4.toLogString + ")"
				case y => y.toString
			}
		}
	}
}
