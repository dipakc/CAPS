package expt

import progsynth.provers.PSWhy3AltErgoProver
import progsynth.ProgSynth._
import progsynth.types._
import progsynth.types.Types._
import progsynth.provers._

object Expt11 {
	val x = VarInt("x")                       //> x  : progsynth.types.VarInt = VarInt(x)
	val y = VarInt("y")                       //> y  : progsynth.types.VarInt = VarInt(y)
	val f: TermBool = (x <= y) impl ( (x < y) || (x eqeq y))

    val altErgoProver = new PSWhy3AltErgoProver()
                                                  //> altErgoProver  : progsynth.provers.PSWhy3AltErgoProver = progsynth.provers.P
                                                  //| SWhy3AltErgoProver@1d63e39
	altErgoProver.config.setParam("timeout", "20000")
	val result = altErgoProver.prove(f)
	println(result)

}

object Expt11SPASS extends App {
	val x = VarInt("x")                       //> x  : progsynth.types.VarInt = VarInt(x)
	val y = VarInt("y")                       //> y  : progsynth.types.VarInt = VarInt(y)

    val f: TermBool = (x <= y) impl ( (x < y) && (x eqeq y))
	val prover = new PSWhy3SPASSProver()
	prover.config.setParam("timeout", "40000")
	val result = prover.prove(f)
	println(result)
}

object Expt11_2 extends App {
    val cmd = """D:\ProgramFilesPortable\SSHWrapper\SSHWrapper.bat why3 -P spass D:\tmp2\progsynth\po.mlw"""
	val processStatus = expt.PSTimeout.runProcess(cmd)//TODO: enable timeout
	println(processStatus.stdout)
}

object Expt11CVC3 extends App {
	val x = VarInt("x")                       //> x  : progsynth.types.VarInt = VarInt(x)
	val y = VarInt("y")                       //> y  : progsynth.types.VarInt = VarInt(y)

    val f: TermBool = (x <= y) impl ( (x < y) && (x eqeq y))
	val prover = new PSWhy3CVC3Prover()
	prover.config.setParam("timeout", "40000")
	val result = prover.prove(f)
	println(result)
}

object Expt11Z3 extends App {
	val x = VarInt("x")                       //> x  : progsynth.types.VarInt = VarInt(x)
	val y = VarInt("y")                       //> y  : progsynth.types.VarInt = VarInt(y)

    val f: TermBool = (x <= y) impl ( (x < y) && (x eqeq y))
	val prover = new PSWhy3Z3Prover()
	prover.config.setParam("timeout", "40000")
	val result = prover.prove(f)
	println(result)
}


object Expt11_3 extends App {
    val check:Option[xml.Text] = Some(xml.Text("abc"))
    val div = <div check={check}> </div>
    println(div)
}