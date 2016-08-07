
package jsonprinter

import play.api.libs.json.JsObject

abstract class PrimitiveTV(value: Any) extends TV {
	
	override val tvName = "PrimitiveTV"
}