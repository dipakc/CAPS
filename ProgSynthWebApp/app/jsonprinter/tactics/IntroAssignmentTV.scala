package jsonprinter
package tactics

import progsynth.types.TermBool
import progsynth.types.Term
import progsynth.types.Var
import models.mqprinter.MQPrinter.mqprintTerm0
import jsonprinter.VarTV

object IntroAssignmentTV {
	
	def apply(lhsRhsTuples:List[(Term, Term)]) = {
		new IntroAssignmentTV(lhsRhsTuples.map(t => (mqprintTerm0(t._1), mqprintTerm0(t._2))))
	}
}

class IntroAssignmentTV(lhsRhsTuples:List[(String, String)]) extends ClassTV(
	{
		val metaTV:TV  =
			TupleTV(TermTV.getEmpty,
					TermTV.getEmpty)
		
		val items: List[TV] =
			lhsRhsTuples.map(t =>
				TupleTV(TermTV(t._1),
					TermTV(t._2)))
		
		List(FieldTV("lhsRhsTuples", "lhsRhsTuples",
				ListTV(metaTV, items)))
	})
{
	override val tvName = "IntroAssignmentTV"
	
}

//class IntroAssignmentTV(lhsRhsTuples:List[(Term, Term)]) extends ClassTV(
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
//	override val tvName = "IntroAssignmentTV"
//
//}
