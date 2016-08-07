package progsynth.types

import Types._
import org.scalacheck.Gen
import progsynth.types.TermGen._
import progsynth.types.FormulaGen._
import org.scalacheck.Shrink
import progsynth.{utils=>psu}
import progsynth.debug.PSDbg._

object ProgramAnnGen {
	def functionProgGenHt(ht: Int) = {
		for {
			name <- Gen.value("funName") //TODO
			params <- Gen.listOfN(4, varGen) //TODO: arity
			retVar <- varGen
			annProg <- programAnnGenHt(ht)
		} yield FunctionProg(name, params, retVar, annProg, Nil, null, null) //TODO: set proper pre and post.
	}

	def programAnnGenHt(ht: Int): Gen[ProgramAnn] = {
		ht match {
			case _ if ht > 0 =>
				Gen.frequency(
					(1, ifProgGenHt(ht - 1)),
					(1, whileProgGenHt(ht - 1)),
					(1, varDefProgGenHt(ht - 1)),
					(1, valDefProgGenHt(ht - 1)),
					(1, compositionGenHt(ht - 1)),
					(1, assignmentGenHt(ht - 1)),
					(1, unknownProgGen),
					(1, skipProgGen),
					(1, exprProgGen))
			case _ if ht <= 0 =>
				Gen.frequency(
					(1, exprProgGen),
					(1, skipProgGen),
					(1, unknownProgGen))
		}
	}

	def guardedCmdGenHt(ht: Int) = {
		for {
			guard <- termBoolGenHt(4)
			cmd <- programAnnGenHt(ht)
		} yield GuardedCmd(guard, cmd)
	}

	def ifProgGenHt(ht: Int) = {
		for {
			pre <- invariantGen
			post <- invariantGen
			grdcmds <- Gen.listOfN(1, guardedCmdGenHt(ht))
		} yield mkIfProg(pre, grdcmds, post)
	}

	def whileProgGenHt(ht: Int) = {
		for {
			pre <- invariantGen
			post <- invariantGen
			term <- termBoolGenHt(4)
			grdcmds <- Gen.listOfN(1, guardedCmdGenHt(ht))
		} yield mkWhileProg(pre, Some(term), grdcmds, post)
	}

	def varDefProgGenHt(ht: Int) = {
		for {
			pre <- invariantGen
			post <- invariantGen
			lhs <- varGen
			rhs <- programAnnGenHt(ht)
		} yield mkVarDefProg(pre, lhs, rhs, post)
	}

	def valDefProgGenHt(ht: Int) = {
		for {
			pre <- invariantGen
			post <- invariantGen
			lhs <- varGen
			rhs <- programAnnGenHt(ht)
		} yield mkValDefProg(pre, lhs, rhs, post)
	}

	def compositionGenHt(ht: Int) = {
		for {
			pre <- invariantGen
			post <- invariantGen
			size <- Gen.choose(2, 4)
			programs <- Gen.listOfN(size, programAnnGenHt(ht))
		} yield mkComposition(pre, programs, post)
	}
	def assignmentGenHt(ht: Int) = {
		for {
			pre <- invariantGen
			post <- invariantGen
			lhs <- varGen
			rhs <- programAnnGenHt(ht)
		} yield mkAssignment(pre, lhs, rhs, post)
	}

	def unknownProgGen = {
		for {
			pre <- invariantGen
			post <- invariantGen
			anInt <- Gen.choose(0, 10)
		} yield mkUnknownProg(pre, anInt, post)
	}
	def skipProgGen = {
		for {
			pre <- invariantGen
			post <- invariantGen
		} yield mkSkipProg(pre, post)
	}

	def exprProgGen() = {
		for {
			pre <- invariantGen
			post <- invariantGen
			term <- termGenHt(4)
		} yield mkExprProg(pre, term, post)
	}
	def invariantGen() = {
		for {
			term <- termBoolGenHt(4)
			rvVar <- varGen
			rvVarOpt <- Gen.oneOf(Some(rvVar), None)
		} yield InvariantT(None, term, rvVarOpt)
	}
}
