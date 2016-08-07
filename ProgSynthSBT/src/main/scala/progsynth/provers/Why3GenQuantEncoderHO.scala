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
import GenQTermLoHi.termToGenQTermLoHi
import scala.collection.immutable.HashMap
import progsynth.provers.Why3AST._
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._
import SABOperatorRepo._

case class WEncodingHO(decls: List[WDecl], oprName: String, rangeConstName: String, termConstName: String)

trait Why3GenQuantEncoderHO extends EncodingCreatorHO with IWhy3GenQuantEncoder { self: Why3TheoryBuilder with Why3ASTUtils=>

    var encodingMapHO: HashMap[QTerm, WEncodingHO] = HashMap()

    object X {
	    val logger= LoggerFactory.getLogger("progsynth.Why3GenQuantEncoder")
	}

    def genQTermToWTerm(quant: QTerm, ctxDummies: List[Var]): WTerm = {
        //implicit val logger = X.logger

        //traceTerm("quantified Term", quant)

        val (wEncoding, newlyCreated) = mkEncodingAndUpdateTheory(quant, ctxDummies)

        val wFnApp = createFnApp(quant, wEncoding, ctxDummies)

        if (newlyCreated)
            self.addDeclToTheory(wEncoding.decls)

        wFnApp
    }

	def mkEncodingAndUpdateTheory(inQuant: QTerm, ctxDummies: List[Var]): (WEncodingHO, Boolean) = {
	    def areSame(a: QTerm, b: QTerm): Boolean =
	        a.range == b.range && a.term == b.term

	    encodingMapHO.find{case (qt, encoding) => areSame(qt, inQuant)} match {
			case Some((qterm, we)) =>
				(we, false)
			case None =>
				val we = createEncoding(inQuant, ctxDummies)
				encodingMapHO = encodingMapHO + ((inQuant, we))
				(we, true)
		}
	}

}



trait EncodingCreatorHO { self: Why3GenQuantEncoderHO with Why3TheoryBuilder with Why3ASTUtils=>

    /** Returns (moduleFile, moduleName, bigOprName )*/
    def getQTheoryName(opr: SABOperator): (String, String, String) = opr match {
        case MaxIntOpr => (s"bigMax", s"BigMax", "bigMax")
        case MinIntOpr => (s"bigMin", s"BigMin", "bigMin")
        case TimesIntOpr => (s"bigTimes", s"BigTimes", "bigTimes")
        case PlusIntOpr => (s"bigPlus", s"BigPlus", "bigPlus")
        case _ => throw new RuntimeException(s"Operator $opr not handled")
    }

    def createEncoding(qterm: QTerm, ctxDummies: List[Var]): WEncodingHO = {
        import SABOperatorRepo._

        assert(qterm.dummies.length == 1)
        val dummy = qterm.dummies.head

        val sabOpr = getSABOperator(qterm.opr).get //TODO: avoid get

        val (moduleFile, moduleName, bigOprName) = getQTheoryName(sabOpr)
        val bigOprTheory = moduleFile + "." + moduleName

        /** import the theory */
        val importStmt = WRawDecl(s"use import $bigOprTheory as $moduleName")

        /** range lambda constant */
        val rangeConstName = freshName("r")
        addToUsedName(rangeConstName)
        val wRange = poToWTerm(qterm.range, ctxDummies :+ dummy)
        val rangeConstant = mkWLambdaConstant2(
                constName = rangeConstName,
                params =  ctxDummies :+ dummy,
                term = wRange,
                bodyTpe = getWType(qterm.range))

        /** term lambda constant */
        val termConstName = freshName("t")
        addToUsedName(termConstName)
        val wTerm = poToWTerm(qterm.term, ctxDummies :+ dummy)
        val termConstant = mkWLambdaConstant2(
                constName = termConstName,
                params = ctxDummies :+ dummy,
                term = wTerm,
                bodyTpe = getWType(qterm.term))

        val decls = importStmt :: rangeConstant :: termConstant :: Nil

        WEncodingHO(decls, bigOprName, rangeConstName, termConstName)
    }

    def createFnApp(qterm: QTerm, wEncoding: WEncodingHO, ctxDummies: List[Var]) = {
        val WEncodingHO(_, opr, rcn, tcn) = wEncoding

        assert(qterm.dummies.length == 1)
        val dummy = qterm.dummies.head
        val sabOpr = getSABOperator(qterm.opr).get //TODO: avoid get

        val fvWs: List[WTerm] = qterm.getFreeVars().map { x => WSymbol(x.v) }

        val rcnPartialApp = WFnApp(rcn, ctxDummies.map(d => WSymbol(d.v)))
        val tcnPartialApp = WFnApp(tcn, ctxDummies.map(d => WSymbol(d.v)))

        WFnApp(opr, rcnPartialApp :: tcnPartialApp :: Nil)
    }

}
