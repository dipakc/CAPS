package models.derivations

import progsynth.synthesisnew._
import progsynth.types._
import progsynth.types.Types._
import progsynth.{utils=>psu, _}
import progsynth._
import progsynth.ProgSynth._
import progsynth.methodspecs.InterpretedFns._
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.Level
import com.twitter.chill.ScalaKryoInstantiator
import java.io.ByteArrayOutputStream
import com.esotericsoftware.kryo.io.{Input, Output}
import play.api.libs.iteratee.Enumerator
import scala.util._


object DerivationUtils {

	def prime(aVar: Var) = MetaVarUtilities.mkPrimedVar(aVar)

	def c(aConst: Int) = ConstInt(aConst.toString)

    ///////////////////
	def serialize[T](t: T): Array[Byte] = ScalaKryoInstantiator.defaultPool.toBytesWithClass(t)

	//def serializeEnumerator[T](t: T): Enumerator[Array[Byte]] = Enumerator(serialize(t))

	def deserialize[T](bytes: Array[Byte]): Try[T] = {
        val v:T = ScalaKryoInstantiator.defaultPool.fromBytes(bytes).asInstanceOf[T]
        Try(v)
	}

	def rt[T](t: T): Try[T] = deserialize(serialize(t))
    ///////////////////

	object varObj  {
		val r = VarBool("r")
		val c1 = ConstInt("1")
		val c0 = ConstInt("0")
		val i = VarInt("i")
		val n = VarInt("n")
		val p = VarInt("p")
		val s = VarBool("s")
		val N = VarInt("N")
		val arr = VarArrayBool("arr")
		var params:List[VarInt] =  List(n, p)
	}
}