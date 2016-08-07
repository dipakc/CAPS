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

case class WEncodingLHHO(decls: List[WDecl], oprName: String, rangeConstName: String, termConstName: String)

trait Why3GenQuantEncoderLHHO extends IWhy3GenQuantEncoder with EncodingCreatorLHHO with Why3ASTUtils { self: Why3TheoryBuilder  =>
    var encodingMap: HashMap[GenQTermLoHi, WEncodingLHHO] = HashMap()

    object X {
	    val logger= LoggerFactory.getLogger("progsynth.Why3GenQuantEncoder")
	}

    def genQTermToWTerm(quant: Term, ctxDummies: List[Var]): WTerm = {
        //implicit val logger = X.logger

        //traceTerm("quantified Term", quant)

        val genqLH = termToGenQTermLoHi(quant).get //TODO: Avoid get

        val we: WEncodingLHHO = mkEncodingAndUpdateTheory(genqLH, ctxDummies)

        val wterm: WFnApp = createFnApp(genqLH, we, ctxDummies)

        //logger.trace(multiline(wterm.str()))

        wterm
    }

	def mkEncodingAndUpdateTheory(genqLH: GenQTermLoHi, ctxDummies: List[Var]): WEncodingLHHO = {
	    encodingMap.find{case (e, n) => e isSame genqLH} match {
			case Some((encoding, we)) =>
				we
			case None =>
				val we = createEncoding(genqLH, ctxDummies)

				println("encoding created")

				val WEncodingLHHO(decls, bigOpr, rangeConstName, termConstName) = we

				encodingMap = encodingMap + ((genqLH, we))

				println("encoding added to theory")
				self.addDeclToTheory(we.decls)

				we
		}
	}

}

trait EncodingCreatorLHHO extends Why3ASTUtils { self: Why3TheoryBuilder =>

    def createEncoding(ce: GenQTermLoHi, ctxDummies: List[Var]): WEncodingLHHO = {
        import SABOperatorRepo._

        /** Returns (moduleFile, moduleName, bigOprName )*/
        def getQTheoryName(opr: SABOperator): (String, String, String) = opr match {
            case MaxIntOpr => ("bigMax", "BigMax", "bigMax")
            case MinIntOpr => ("bigMin", "BigMin", "bigMin")
            case TimesIntOpr => ("bigTimes", "BigTimes", "bigTimes")
            case PlusIntOpr => ("bigPlus", "BigPlus", "bigPlus")
            case _ => throw new RuntimeException(s"Operator $opr not handled")
        }

        val (moduleFile: String, moduleName: String, bigOprName: String) = getQTheoryName(ce.opr)
        val bigOprTheory = moduleFile + "." + moduleName

        /** import the theory */
        val importStmt = WRawDecl(s"use import $bigOprTheory as $moduleName")

        /** range lambda constant */
        val rangeConstName = freshName("r")
        addToUsedName(rangeConstName)
        val wRange = poToWTerm(ce.range, ctxDummies :+ ce.dummy)
        val rangeConstant = mkWLambdaConstant2(
                constName = rangeConstName,
                params =  ctxDummies :+ ce.dummy,
                term = wRange,
                bodyTpe = getWType(ce.range))

        /** term lambda constant */
        val termConstName = freshName("t")
        addToUsedName(termConstName)
        val wTerm = poToWTerm(ce.term, ctxDummies :+ ce.dummy)
        val termConstant = mkWLambdaConstant2(
                constName = termConstName,
                params = ctxDummies :+ ce.dummy,
                term = wTerm,
                bodyTpe = getWType(ce.term))

        val decls = importStmt :: rangeConstant :: termConstant :: Nil

        WEncodingLHHO(decls, bigOprName, rangeConstName, termConstName)
    }

    def createFnApp(ce: GenQTermLoHi, wEncoding: WEncodingLHHO, ctxDummies: List[Var]) = {
        val WEncodingLHHO(_, opr, rcn, tcn) = wEncoding

        val fvWs: List[WTerm] = ce.fvs.map { x => WSymbol(x.v) }
        val loW: WTerm = poToWTerm(ce.lo, ctxDummies :+ ce.dummy)
        val hiW: WTerm = poToWTerm(ce.hi, ctxDummies :+ ce.dummy)

        val rcnPartialApp = WFnApp(rcn, ctxDummies.map(d => WSymbol(d.v)))
        val tcnPartialApp = WFnApp(tcn, ctxDummies.map(d => WSymbol(d.v)))

        WFnApp(opr, loW :: hiW :: rcnPartialApp :: tcnPartialApp :: Nil)
    }
}

