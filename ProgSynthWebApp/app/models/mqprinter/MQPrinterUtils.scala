package models.mqprinter

object MQPrinterUtils {
	def surround(x: String, y: String) = y + x + y
	def space(x: String) = " " + x + " "
	def paren(x: String) = "\\left(" + x + "\\right)"
	def sparen(x: String) = "\\left[" + x + "\\right]"
	def parenIf(x: String, cond: Boolean) = if (cond) "\\left(" + x + "\\right)" else x
	val SP = " "
	val COL = ":"
	val COM = ","
	val COMSP = ", "
	val ASGN = ":="
}