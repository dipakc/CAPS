package progsynth.types

//package object extends this trait

trait NaryConstructorsExtractors {
	object AndN {
		def unapply[T](f: Formula[T]): Option[List[Formula[T]]] = {
		    f match {
		        case And(AndN(f1s), AndN(f2s)) => Some(f1s ++ f2s)
		        case _ => Some(f :: Nil)
		    }
		}

		def apply[T](fs: List[Formula[T]]): Formula[T] = fs match {
		    case Nil => True1()
		    case f :: Nil => f
		    case f :: tail => And(f, AndN(tail))
		}
	}

	object AndNTermBool {
		def unapply(f: TermBool): Option[List[TermBool]] = {
		    f match {
		        case AndTermBool(AndNTermBool(f1s), AndNTermBool(f2s)) => Some(f1s ++ f2s)
		        case _ => Some(f :: Nil)
		    }
		}

		def apply(fs: List[TermBool]): TermBool = fs match {
		    case Nil => TermBool.TrueT
		    case f :: Nil => f
		    case f :: tail => AndTermBool(f, AndNTermBool(tail))
		}
	}

	object OrN {
		def unapply[T](f: Formula[T]): Option[List[Formula[T]]] = {
		    f match {
		        case Or(OrN(f1s), OrN(f2s)) => Some(f1s ++ f2s)
		        case _ => Some(f :: Nil)
		    }
		}

		def apply[T](fs: List[Formula[T]]): Formula[T] = fs match {
		    case Nil => False1()
		    case f :: Nil => f
		    case f :: tail => Or(f, OrN(tail))
		}
	}

	object OrNTermBool {
		def unapply(f: TermBool): Option[List[TermBool]] = {
		    f match {
		        case OrTermBool(OrNTermBool(f1s), OrNTermBool(f2s)) => Some(f1s ++ f2s)
		        case _ => Some(f :: Nil)
		    }
		}

		def apply(fs: List[TermBool]): TermBool = fs match {
		    case Nil => TermBool.FalseT
		    case f :: Nil => f
		    case f :: tail => OrTermBool(f, OrNTermBool(tail))
		}
	}

	object ForallN {
		def unapply[T](f: Formula[T]): Option[(List[Var], Formula[T])] = {
		    f match {
		        case Forall(v, ForallN(vs, f)) => Some(v :: vs, f)
		        case _ => Some((Nil, f))
		    }
		}
	}
	object ExistsN {
		def unapply[T](f: Formula[T]): Option[(List[Var], Formula[T])] = {
				f match {
				case Exists(v, ExistsN(vs, f)) => Some(v :: vs, f)
				case _ => Some((Nil, f))
				}
		}
	}
	object QuantN {
		def unapply[T](f: Formula[T]): Option[(List[String], List[Var], Formula[T])] = {
			f match {
				case Forall(v, QuantN(qs, vs, f)) => Some(("""\forall""" :: qs, v :: vs, f))
				case Exists(v, QuantN(qs, vs, f)) => Some(("""\exists""" :: qs, v :: vs, f))
				case Forall(v, f) => Some(("""\forall""":: Nil, List(v), f))
				case Exists(v, f) => Some(("""\exists""":: Nil, List(v), f))
				case _ => None
			}
		}
	}

	object PlusNTermInt{
		def unapply(f: TermInt): Option[List[TermInt]] = {
		    f match {
		        case PlusTermInt(PlusNTermInt(f1s), PlusNTermInt(f2s)) => Some(f1s ++ f2s)
		        case _ => Some(f :: Nil)
		    }
		}

		def apply(fs: List[TermInt]): TermInt = fs match {
		    case Nil => ConstInt("0")
		    case f :: Nil => f
		    case f :: tail => PlusTermInt(f, PlusNTermInt(tail))
		}
	}

}