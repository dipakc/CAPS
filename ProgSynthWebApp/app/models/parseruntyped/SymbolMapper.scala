package models.parseruntyped

trait SymbolMapper {
	val isMQ: Boolean

	/** Get Symbol String */
	def sym(sym: String): String = {
		//isMathQuill
		if (!isMQ)
			sym
		else {
			sym match {
				//TermInt
				case "+" => """+"""
				case "-" => """-"""
				case "*" => """\ast"""
				case "/" => """\slash"""
				case "%" => """\%"""
				case "max" => """max"""
				case "min" => """min"""
				case ":=" => """:="""
				//TermBool
				case """!""" => """!"""
				case """===""" => """==="""
				case """&&""" => """\&\&"""
				case """||""" => """\parallel"""
				case """==>""" => """==>"""
				//case """:=""" => """\\:\\="""//case already handled
				//FOLFormula
				case """~""" => """\neg"""
				case """\exists""" => """\exists"""
				case """\forall""" => """\forall"""
				case """\Sum""" => """\Sum"""
				case """\Max""" => """\Max"""
				case """\Count""" => """\Count"""
				case """:""" => """:"""
				case """\equiv""" => """\equiv"""
				case """\impl""" => """\Rightarrow"""
				case """\/""" => """\vee"""
				case """/\""" => """\wedge"""
				//PredParser
				case """<=""" => """\le"""
				case """>=""" => """\ge"""
				case """<""" => """<"""
				case """>""" => """>"""
				case """eqeq""" => """="""
				//case """==""" => """=""" //added fix parse error for N=n. TODO: is eqeq redundant.
				//parenthesis
				case """[""" => """\left["""
				case """]""" => """\right]"""
				case """(""" => """\left("""
				case """)""" => """\right)"""
				case s => throw new RuntimeException(s"Mathquill symbol for $s not found")
			}
		}
	}
}