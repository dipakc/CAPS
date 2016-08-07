package models.pprint

trait PPrintUtils {
	def paren(x: String) = "(" + x + ")"
	def parenIf(x: String, cond: Boolean) = if (cond) "(" + x + ")" else x
}