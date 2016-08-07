package expt
import org.kiama.rewriting.Rewriter.{ fail => rwfail, test => rwtest, _ }

object ABC {

    import org.kiama.attribution.Attributable
    import org.kiama.rewriting.Rewriter.{
        congruence,
        rulefs,
        Strategy
    }

    /** Identifiers are represented as strings.
      */
    type Idn = String

    /** Superclass of all imperative language tree node types.  The Product
      * supertype is used here to enable generic access to the children of
      * an ImperativeNode; this capability is only used in the Kiama tests
      * and is not usually needed for normal use of the library.
      */
    trait ImperativeNode extends Attributable

    /** Expressions.
      */
    sealed abstract class Exp extends ImperativeNode {

        /** The numeric value of the expression.
          */
        def value: Double

        /** The set of all variable references in the expression.
          */
        def vars: Set[Idn] = Set()

        /** The number of divisions by the constant zero in the expression.
          */
        def divsbyzero: Int = 0

        /** The depth of the expression, i.e., the number of levels from the
          * root to the leaf values.
          */
        def depth: Int = 0

        /** The number of additions of integer constants in the expression.
          */
        def intadds: Int = 0
    }

    /** Numeric expressions.
      */
    case class Num(d: Double) extends Exp {
        override def value = d
        override def depth = 2
    }

    /** Variable expressions.
      */
    case class Var(s: Idn) extends Exp {
        // Hack to make tests more interesting
        override def value = 3
        override def vars = Set(s)
        override def depth = 2
        override def toString = "Var(\"" + s + "\")"
    }

    /** Unary negation expressions.
      */
    case class Neg(e: Exp) extends Exp {
        override def value = -e.value
        override def vars = e.vars
        override def divsbyzero = e.divsbyzero
        override def depth = 1 + e.depth
        override def intadds = e.intadds
    }

    /** Binary expressions.
      */
    abstract class Binary(l: Exp, r: Exp) extends Exp {
        override def vars = l.vars ++ r.vars
        override def divsbyzero = l.divsbyzero + r.divsbyzero
        override def depth = 1 + (l.depth).max(r.depth)
        override def intadds = l.intadds + r.intadds
    }

    /** Addition expressions.
      */
    case class Add(l: Exp, r: Exp) extends Binary(l, r) {
        override def value = l.value + r.value
        override def intadds =
            (l, r) match {
                case (Num(_), Num(_)) => 1
                case _ => super.intadds
            }
    }

    /** Subtraction expressions.
      */
    case class Sub(l: Exp, r: Exp) extends Binary(l, r) {
        override def value = l.value - r.value
    }

    /** Multiplication expressions.
      */
    case class Mul(l: Exp, r: Exp) extends Binary(l, r) {
        override def value = l.value * r.value
    }

    /** Division expressions.
      */
    case class Div(l: Exp, r: Exp) extends Binary(l, r) {
        // Hack: no errors, so return zero for divide by zero
        override def value = if (r.value == 0) 0 else l.value / r.value
        override def divsbyzero =
            l.divsbyzero + (r match {
                case Num(0) => 1
                case _ => r.divsbyzero
            })
    }

    /** Statements.
      */
    sealed abstract class Stmt extends ImperativeNode {

        /** The set of all variable references in the statement.
          */
        def vars: Set[Idn] = Set()

    }

    /** Empty statements.
      */
    case class Null() extends Stmt

    /** Statement sequences.
      */
    case class Seqn(ss: Seq[Stmt]) extends Stmt {
        override def vars = Set(ss flatMap (_ vars): _*)
    }

    /** Assignment statements.
      */
    case class Asgn(v: Var, e: Exp) extends Stmt {
        override def vars = Set(v.s)
    }

    /** While loops.
      */
    case class While(e: Exp, b: Stmt) extends Stmt {
        override def vars = e.vars ++ b.vars
    }

    // Congruences

    def Num(s1: => Strategy): Strategy =
        rulefs {
            case _: Num =>
                congruence(s1)
        }

    def Var(s1: => Strategy): Strategy =
        rulefs {
            case _: Var =>
                congruence(s1)
        }

    def Neg(s1: => Strategy): Strategy =
        rulefs {
            case _: Var =>
                congruence(s1)
        }

    def Add(s1: => Strategy, s2: => Strategy): Strategy =
        rulefs {
            case _: Add =>
                congruence(s1, s2)
        }

    def Sub(s1: => Strategy, s2: => Strategy): Strategy =
        rulefs {
            case _: Sub =>
                congruence(s1, s2)
        }

    def Mul(s1: => Strategy, s2: => Strategy): Strategy =
        rulefs {
            case _: Mul =>
                congruence(s1, s2)
        }

    def Div(s1: => Strategy, s2: => Strategy): Strategy =
        rulefs {
            case _: Div =>
                congruence(s1, s2)
        }

    def Seqn(s1: => Strategy): Strategy =
        rulefs {
            case _: Seqn =>
                congruence(s1)
        }

    def Asgn(s1: => Strategy, s2: => Strategy): Strategy =
        rulefs {
            case _: Asgn =>
                congruence(s1, s2)
        }

    def While(s1: => Strategy, s2: => Strategy): Strategy =
        rulefs {
            case _: While =>
                congruence(s1, s2)
        }

}
object Expt8 extends App {
    import ABC._
    val t = Add(Add(Num(4), Num(5)), Num(6))
    val eval =
        rule {
            case Add(a, b) => Sub(a, b)
            case Sub(a, b) => Add(a, b)
        }
    val eval2 = rule {
        case Add(Num(i), Num(j)) => Num(i + j)
        case Sub(Num(i), Num(j)) => Num(i - j)
        case Mul(Num(i), Num(j)) => Num(i * j)
        case Div(Num(i), Num(0)) => Num(0) // Hack
        case Div(Num(i), Num(j)) => Num(i / j)
        case Var(_) => Num(3) // Hack
    }

    val eval3 = rule {
		case Add(Num(i), Num(j)) => Num(i + j)
		case Sub(Num(i), Num(j)) => Num(i - j)
		case Mul(Num(i), Num(j)) => Num(i * j)
		case Div(Num(i), Num(0)) => Num(0) // Hack
		case Div(Num(i), Num(j)) => Num(i / j)
		case Var(_) => Num(3) // Hack
		case Num(i) => Num(i)
	}
    val t2 = Add( Num(5), Add(Num(4), Num(5)))
    val s4 = strategy {
    	case Num(5) => Some(Num(4))
    	case a => Some(a)
    }
    val s = bottomup(s4)(t2)

    println(s)
    println("done")
}

