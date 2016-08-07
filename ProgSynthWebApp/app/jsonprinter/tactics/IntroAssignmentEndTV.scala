package jsonprinter
package tactics

import progsynth.types.TermBool
import progsynth.types.Term
import progsynth.types.Var
import models.mqprinter.MQPrinter.mqprintTerm0
import jsonprinter.VarTV

object IntroAssignmentEndTV {
	
	def apply(lhsRhsTuples:List[(Var, Term)]) = {
		new IntroAssignmentEndTV(lhsRhsTuples.map(t => (mqprintTerm0(t._1), mqprintTerm0(t._2))))
	}
}

//InstantiateMetaTactic(lhsRhsTuples: List[(Var, Term)])
class IntroAssignmentEndTV(lhsRhsTuples:List[(String, String)]) extends ClassTV(
	{
		val metaTV:TV  =
			TupleTV(VarTV.getEmpty,
					TermTV.getEmpty)
		
		val items: List[TV] =
			lhsRhsTuples.map(t =>
				TupleTV(VarTV(t._1), TermTV(t._2)))
		
		List(FieldTV("lhsRhsTuples", "lhsRhsTuples",
				ListTV(metaTV, items)))
	})
{
	override val tvName = "IntroAssignmentEndTV"
	
}

//class IntroAssignmentEndTV(lhsRhsTuples:List[(Term, Term)]) extends ClassTV(
//	{
//		val metaTV:TV  =
//			TupleTV(TermTV.getEmpty,
//					TermTV.getEmpty)
//
//		val items: List[TV] =
//			lhsRhsTuples.map(t =>
//				TupleTV(TermTV(t._1),
//					TermTV(t._2)))
//
//		List(FieldTV("lhsRhsTuples", "lhsRhsTuples",
//				ListTV(metaTV, items)))
//	})
//{
//	override val tvName = "IntroAssignmentEndTV"
//
//}
