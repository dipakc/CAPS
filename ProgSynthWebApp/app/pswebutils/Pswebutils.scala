package pswebutils
import play.api.templates.Html
import play.api.libs.json.JsValue

object Pswebutils {
	
	//http://stackoverflow.com/questions/2060395/is-there-any-scala-feature-that-allows-you-to-call-a-method-whose-name-is-stored
	case class Caller[T>:Null<:Any](klass:T) {
	  def call(methodName:String,args:AnyRef*):AnyRef = {
	    def argtypes = args.map(_.getClass)
	    def method = klass.getClass.getMethod(methodName, argtypes: _*)
	    method.invoke(klass, args: _*)
	  }
	}
	implicit def anyref2callable[T>:Null<:Any](klass:T):Caller[T] = new Caller(klass)
	
	def xmlToHtml(node: scala.xml.Node): Html = new Html(new StringBuilder(node.toString))

	def pprintJsValue(jsValue:  JsValue): String = {
		jsValue.toString
	}
}