package progsynth.types

sealed abstract class PSType {
	def getCleanName() = "^PS".r.replaceFirstIn(this.toString, "")
}
case object PSInt extends PSType
case object PSReal extends PSType
case object PSBool extends PSType
case object PSUnit extends PSType
case object PSArrayInt extends PSType
case object PSArrayBool extends PSType
case object PSArrayReal extends PSType
case object PSAny extends PSType

object PSType {
	def getPSType(tpeStr: String): Option[PSType] = {
		//writeln0(tpeStr)
		tpeStr match {
			case "Int" => Some(PSInt)
			case "Double" => Some(PSReal)
			case "Boolean" => Some(PSBool)
			case "Unit" => Some(PSUnit)
			case "Array[Int]" => Some(PSArrayInt)
			case _ => None
		}
	}
	val psTypes = List(PSInt, PSArrayInt, PSBool, PSArrayBool, PSReal, PSArrayReal, PSUnit)
	val psArrayTypes = List(PSArrayInt, PSArrayBool, PSArrayReal)
	val psBasicTypes = List(PSInt, PSBool, PSReal)

	/**Returns array type corresponding to a type
	 * getArrTpe(PSInt) should equal (PSArrayInt)
	 * */
	def getArrTpe(tpe: PSType) = tpe match {
		case PSInt => PSArrayInt
		case PSBool => PSArrayBool
		case PSReal => PSArrayReal
		case _ => throw new RuntimeException("getArrTpe called for tpe "+ tpe)
	}

	/**Returns base type corresponding to an array type
	 * getBaseTpe(PSArrayInt) should equal (PSInt)
	 * */
	def getBasicTpe(tpe: PSType) = tpe match {
		case PSArrayInt => PSInt
		case PSArrayBool => PSBool
		case PSArrayReal => PSReal
		case _ => throw new RuntimeException("getBaseTpe called for tpe "+ tpe)
	}
	def isArrayType(tpe: PSType) = psArrayTypes contains tpe
	def isBasicType(tpe: PSType) = psBasicTypes contains tpe
}
