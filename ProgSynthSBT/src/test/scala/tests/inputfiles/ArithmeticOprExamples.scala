package tests.inputfiles

object ArithmeticOprExamples {
	def testMethod() = {
		var x = 0
		val y = 0
		var z = 0
		val e1 = x + y
		val e2 = x * y
		val e3 = x * y + z
		val e4 = -x * y + z
		val e5 = x % y
		val e6 = x + 5
		val e7 = 5 + x
		val e8 = (5 + x) % 3 / z
	}

}
