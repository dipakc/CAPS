package progsynth.methodspecs
/*
import scala.collection.mutable.HashMap
import progsynth.types._
import progsynth.types.Types._


class MethodSpec(val methodId: String, val pre: Invariant, val post: Invariant )
object MethodSpec{
	def apply(methodId: String, pre: Invariant, post: Invariant): MethodSpec = {
		new MethodSpec(methodId, pre, post)
	}
}

object MethodSpecRepository {
	val methodSpecs = new HashMap[String, MethodSpec]
	def addMethodSpec(ms: MethodSpec) =
		methodSpecs += ms.methodId -> ms
}

object InitMethodSpecRepository {
//	MethodSpecRepository.addMethodSpec(plusMethodSpec)
//
//	def plusMethodSpec() = {
//		val pre = (Invariant(None, True, None))
//		val post = Invariant(None, Atom(Pred("$eq$eq", List(VarInt("rv"), FnApp("$plus", List(VarInt("a1"), VarInt("a2")), PSInt)))), Some(VarInt("rv")))
//		val methodId = """$plus"""
//		MethodSpec(methodId, pre, post)
//	}
}
*/