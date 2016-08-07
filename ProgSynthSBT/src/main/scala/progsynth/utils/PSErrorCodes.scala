package progsynth.utils
import progsynth.types._

object PSErrorCodes {
	def ErrNumParamMismatch(value1: Int, value2: Int) =
		"Number of parameters of procedure call does not match with that of the definition: " + value1 + " != " + value2
	
	def ErrTypeMismatch(aVar: Var) =
		"Type mismatch for variable " + aVar.v
	def ErrModifiableParamNotLValue(aVar: Var) =
		"Actual parameter for a modifiable variable " + aVar.v + " should be of type Var"
	def ErrVariableInitValueTypeMismatch() =
		"Variable and init value type does not match"
	
	def PSError(msg: String) =
		throw new RuntimeException(msg)
}