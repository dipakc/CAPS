package progsynth.synthesisnew
import progsynth.types._

// Macro class
// printer: XHtmlPrinter2.macroToHtml
// Example: Macro("square", VarInt("x") :: Nil, PSInt, x * x )
// def square(x: PSInt): PSInt = x * x
case class Macro(name: String, params: List[Var], retType: PSType, body: Term){

}