package expt
import scala.io._
import progsynth.utils._
import com.twitter.chill.ScalaKryoInstantiator
import java.io.ByteArrayOutputStream
import com.esotericsoftware.kryo.io.{Input, Output}
import scala.util._

object Expt9 extends App {

//    val sumTry = for {
//        int1 <- Try(Integer.parseInt("1"))
//        int2 = Integer.parseInt("2a")
//        int3 <- Try(Integer.parseInt("3"))
//    } yield {
//        int1 + int2 + int3
//    }

	val sumTry1 =
	    Try(Integer.parseInt("1")) map { int1 =>
	    	val int2 = Integer.parseInt("2a")
	    	int1 + int2
		}

	val sumTry2: Try[Int] = throw new RuntimeException("Test Exception")

	sumTry1 match {
        case Failure(thrown) => {
            Console.println("Failure: " + thrown.getMessage())
        }
        case Success(s) => {
            Console.println(s)
        }
    }


//    val sum = {for {
//        int1 <- Try(Integer.parseInt("one"))
//        int2 <- Try(Integer.parseInt("two"))
//    } yield {
//        int1 + int2
//    }} recover {
//        case e => 0
//    }

//    val sum = for {
//        int1 <- Try(Integer.parseInt("one")).recover { case e => 0 }
//        int2 <- Try(Integer.parseInt("two"))
//    } yield {
//        int1 + int2
//    }
//
//    val sum = for {
//        int1 <- Try(Integer.parseInt("one")).recoverWith {
//            case e: NumberFormatException => Failure(new IllegalArgumentException("Try 1 next time"))
//        }
//        int2 <- Try(Integer.parseInt("2"))
//    } yield {
//        int1 + int2
//    }


}

