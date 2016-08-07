package jsonprinter
package tactics

import progsynth.types.TermBool
import progsynth.types.Term
import progsynth.types.Var
import models.mqprinter.MQPrinter.mqprintTerm0
import jsonprinter.VarTV

object InstantiateMetaTV {
	
	def apply(primedVarTermList:List[(Var, Term)]) = {
		new InstantiateMetaTV(primedVarTermList.map(t => (mqprintTerm0(t._1), mqprintTerm0(t._2))))
	}
}

//InstantiateMetaTactic(primedVarTermList: List[(Var, Term)])
class InstantiateMetaTV(primedVarTermList:List[(String, String)]) extends ClassTV(
	{
		val metaTV:TV  =
			TupleTV(StringTV.getEmpty,
					TermTV.getEmpty)
		
		val items: List[TV] =
			primedVarTermList.map(t =>
				TupleTV(StringTV(t._1), TermTV(t._2)))
		
		List(FieldTV("primedVarTermList", "primedVarTermList",
				ListTV(metaTV, items)))
	})
{
	override val tvName = "InstantiateMetaTV"
	
}

//class InstantiateMetaTV(lhsRhsTuples:List[(Term, Term)]) extends ClassTV(
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
//	override val tvName = "InstantiateMetaTV"
//
//}
