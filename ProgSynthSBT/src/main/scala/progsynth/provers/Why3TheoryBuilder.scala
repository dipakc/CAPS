package progsynth.provers

import progsynth.ProgSynth._
import progsynth.types._
import progsynth.types.Types._
import progsynth.methodspecs.InterpretedFns
import progsynth.methodspecs.InterpretedFns._
import scala.collection.mutable.Map
import expt.PSTimeout.ProcessStatus
import scala.PartialFunction._
import scala.util.control.Breaks._
import Why3AST._
import Why3IOStringUtils._
import Why3TypeUtils._
import progsynth.logger.PSLogUtils._
import org.slf4j.LoggerFactory

object Why3TheoryBuilder {
    val getTheoryBuilderFactories = Why3TBFactory1 :: Why3TBFactory4 :: Nil
}

trait Why3TBFactory {
    def mkTheoryBuilder(): Why3TheoryBuilder
    def getId(): Int

}
object Why3TBFactory1 extends Why3TBFactory {
	def mkTheoryBuilder(): Why3TheoryBuilder = new Why3TheoryBuilder with Why3GenQuantEncoderHO {}
	def getId() = 1
}

object Why3TBFactory4 extends Why3TBFactory {

    def mkTheoryBuilder(): Why3TheoryBuilder = new Why3TheoryBuilder with Why3GenQuantEncoderHO {}
    def getId() = 4
}


trait IWhy3GenQuantEncoder {
    def genQTermToWTerm(quant: QTerm, ctxDummies: List[Var]): WTerm
}

abstract class Why3TheoryBuilder extends Why3PoToWTerm with IWhy3GenQuantEncoder with Why3ASTUtils{

    private implicit val logger= LoggerFactory.getLogger("progsynth.Why3TheoryBuilder")

    private var usedNames: List[String] = Nil

    def addToUsedNames(names: List[String]) = {
        usedNames = usedNames ++ names
    }

    def addToUsedName(name: String) = {
        usedNames = name :: usedNames
    }

    def freshName(seed: String): String = {
        var fname = seed
        var id = 0
        while (usedNames contains fname) {
            fname = seed + id
            id = id + 1
        }
        fname
	}

	def addPO(po: TermBool): Unit = traceBeginEnd("Why3TheoryBuilder.addPO") {

        addPreambleToTheory(po)

    	val goalWTerm = poToWTerm(po, Nil)

    	addDeclToTheory(WGoalDecl("G", goalWTerm))
	}

	def getTheory = theory

	//=====================================================
	private var theory: WTheory = WTheory("Test", lbls = Nil, decls = Nil, Nil);

	protected def addDeclToTheory(decl: WDecl) = {
		theory = theory.addDecl(decl)
	}

	protected def addDeclToTheory(decls: List[WDecl]) = {
		theory = theory.addDecl(decls)
	}

    def addPreambleToTheory(po: TermBool): Unit = traceBeginEnd("Why3TheoryBuilder.addPreambleToTheory"){

        def addImports() = addDeclToTheory {
    		WUseDecl("use import bool.Bool") ::
			WUseDecl("use import int.Int") ::
    		WUseDecl("use import array.Array")::
    		WUseDecl("use import int.Power"):: //TODO: Add only when required.
    		//WUseDecl("use HighOrd")::
    	  	Nil
        }

        addImports()

        val separator = "\n"
        addDeclToTheory(WRawDecl(separator))

        def addConstantDecls() = {
            val constDecls = po.getFreeVars.map { fv =>
                val varName = fv.v
                val varTpe = getWhy3Tpe(fv)
                WConstDecl(name = varName, lbls = Nil, tpe = WTypeSymbol(varTpe, Nil), body = None)
            }
            addDeclToTheory(constDecls)
            addToUsedNames(po.getFreeVars.map(fv => fv.v))
        }

        addConstantDecls()
    }

}

