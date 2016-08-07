package progsynth.provers

import scala.xml.Elem

abstract class PSProofStatus {
    def toHtml(): Elem
    def toShortHtml(): Elem
}

case class PSProofValid() extends PSProofStatus {
    def toHtml() =
        <div class='PSProofStatus'>
    		<div>Valid</div>
    	</div>

    def toShortHtml() = <div>Valid</div>
}
case class PSProofInvalid(counterExample: String) extends PSProofStatus {
    def toHtml() =
        <div class='PSProofStatus'>
    		<div>Invalid</div>
    		<div class='counterExample'>{counterExample}</div>
    	</div>

    def toShortHtml() = <div>Invalid</div>
}
case class PSProofUnknown(info: String) extends PSProofStatus {
	def toHtml() =
        <div class='PSProofStatus'>
    		<div>Unknown</div>
			<div class='info'>{info}</div>
    	</div>

	def toShortHtml() = <div>Unknown</div>
}
case class PSProofTimeout(info: String) extends PSProofStatus {
    def toHtml() =
        <div class='PSProofStatus'>
    		<div>Timeout</div>
    		<div class='info'>{info}</div>
    	</div>

    def toShortHtml() = <div>Timeout</div>
}

case class PSProofError(info: String) extends PSProofStatus {
    def toHtml() =
        <div class='PSProofStatus'>
    		<div>Error</div>
    		<div class='info'>{info}</div>
    	</div>

    def toShortHtml() = <div>Error</div>
}
