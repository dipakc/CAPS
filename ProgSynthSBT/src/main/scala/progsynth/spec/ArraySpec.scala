package progsynth.spec

/**Spec DSL for specifying array properties*/
object ArraySpec {
	/**For "Select", use the standard scala apply function, arr(i)*/

	/**"Store(array, index, value)"*/
	def store[T]( arr: Array[T], index: Int, value: T): Array[T] = {
		val arrNew = arr.clone()
		arrNew(index) = value
		arrNew
	}

	/**Initialized array. eg [2, 2, 2, ...]*/
	def arrayInit[T](arr: Array[T], value: T): Array[T] = arr
}