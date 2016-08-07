package progsynth.types

import org.kiama.rewriting.Rewritable
import org.kiama.rewriting.Rewriter.{ Term => KTerm }
import progsynth.debug.PSDbg

// PSProgTree hierarchy docs/ProgSynthDoc.html#PSProgTree
// docs/ProgSynthDoc.html#CaseRewritable
/**Implements kiama Rewritable interface for any case class.
 *
 * Can be mixed with any class that implements product.
 *
 * Although Kiama handles case classes, Rewritable interface is desirable since
 * it can be used internally by ProgSynth. If the case class has mutable fields that are
 * not in case class argument but you want these fields to be part of the tree then override
 * this trait to include the fields. eg. refer [[progsynth.types.ProgramAnnRewritable]]
 *
 * Test class: [[tests.types.CaseRewritableTest]]
 * */
trait CaseRewritable extends Rewritable { self: Product =>
	def arity = self.productArity
	def deconstruct =
		for (i <- 0 until self.productArity) yield self.productElement(i)
	def reconstruct(args: Array[KTerm]) =
		dup(self, args map makechild)
	/**
	 * General product duplication function.  Returns a product that applies
	 * the same constructor as the product `t`, but with the given children
	 * instead of `t`'s children.  Fails if a constructor cannot be found or
	 * if one of the children is not of the appropriate type.
	 *
	 * Performance Warning: the dup function in kiama has a cache for the constructor.
	 * However this function does not use the cache.
	 * There might be performance implications in kiama rewriting.
	 */
	protected def dup[T <: Product](t: T, children: Array[AnyRef]): T = {
		val clazz = t.getClass
		val ctor = (clazz.getConstructors())(0)
		try {
			ctor.newInstance(children: _*).asInstanceOf[T]
		} catch {
			case e: IllegalArgumentException =>
				sys.error("dup illegal arguments: " + ctor + " (" +
					children.deep.mkString(",") + "), expects " +
					ctor.getParameterTypes.length)
		}
	}

	/**
	 * Make an arbitrary value `c` into a term child, checking that it worked
	 * properly. Object references will be returned unchanged; other values
	 * will be boxed.
	 */
	private def makechild(c: Any): AnyRef =
		c.asInstanceOf[AnyRef]

}

object CaseRewritableTestApp {
	case class P(q: Q) extends CaseRewritable
	case class Q(s: String) extends CaseRewritable
	case class R(i: Int) extends CaseRewritable
	case class T() extends CaseRewritable
	def main(args: Array[String]) {
		val p = P(Q("abc"))
		PSDbg.writeln0(p.arity)
		PSDbg.writeln0(p.deconstruct)
		PSDbg.writeln0(p.reconstruct(Array(Q("lmn"))))

		val r = R(5)
		PSDbg.writeln0(r.arity)
		PSDbg.writeln0(r.deconstruct)
		PSDbg.writeln0(r.reconstruct(Array(6)))

		val t = T()
		PSDbg.writeln0(t.arity)
		PSDbg.writeln0(t.deconstruct)
		PSDbg.writeln0(t.reconstruct(Array()))
	}
}