package progsynth.spec

object UnknownFragment {
	def UnknownFragment[T](id: Int): T = {
		object DefaultVal {
			var defVal: T = _
		}
		DefaultVal.defVal
	}
}