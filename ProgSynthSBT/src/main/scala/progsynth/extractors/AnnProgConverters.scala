package progsynth.extractors
/*
import progsynth.types._
import progsynth.types.Types._


object AnnProgConverters {

	object ArrStoreAssignment {

		/** ArrStoreAssignment(arrName: String, indexTerm: Term, valTerm: Term)
		*/
		def unapply(st: ProgramAnn): Option[(String, Term, Term)] = st match {
			case Assignment((arrVar, ExprProg(ArrStore(arrTerm, indexTerm, valTerm))) :: Nil) =>
				val arrName = arrVar.v
				Some(arrName, indexTerm, valTerm)
			case _ => None
		}
	}

}
*/