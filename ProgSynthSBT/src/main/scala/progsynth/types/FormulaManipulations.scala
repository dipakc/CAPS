package progsynth.types

trait FormulaManipulations[T] { self: Formula[T] =>

	def splitRange(fid: Int): Formula[T] = {
	    //TODO: fid is not used.
		self.applyRec{
			case Exists(v, a Or b) =>
				Exists(v, a) || Exists(v, b)
			case Exists(v, (a Or b) And f ) =>
				Exists(v, a && f ) || Exists(v, (b && f))
			case Exists(v, f And (a Or b)) =>
				Exists(v, f && a ) || Exists(v, (f && b))
			case Forall(v, (a Or b ) Impl f) =>
				Forall(v, a impl f ) || Forall(v, (b impl f))
			case Forall(v, ((a Or b ) And c) Impl f) =>
				Forall(v, a && c  impl f )|| Forall(v, (b  && c impl f))
			case Forall(v, (c And (a Or b)) Impl f) =>
				Forall(v, c && a  impl f )|| Forall(v, (c && b impl f))
		}
	}
	/** a && (b || c) ===> (a && b) || (a && c)
	 *  (b || c) && a  ===> (b || c) && a
	 *  (a || b) && (c || d) ===> ((a || b) && c) || ((a || b) && d)
	 *  */
	def distributeAndRight(andId: Int): Formula[T] = {
		self.applyRec{
		    case And(a, Or(b, c)) if (self.fid == andId) => Or(And(a, b), And(a, c))
		}
	}

	def distributeAndLeft(andId: Int): Formula[T] = {
		self.applyRec{
		    case And(Or(a, b), c) if (self.fid == andId)=> Or(And(a, c), And(b, c))
		}
	}


	//def dnf(): Formula[T] = {
	  //  d:\tmp\abc.jpg
	//}
}
