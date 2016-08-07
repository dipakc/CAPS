package expt
import scala.tools.nsc._
import java.io._

object RunCompiler2 extends App {

    val g = new Global(new Settings())

    val run = new g.Run

    run.compile(List("""D:\ProgSynth\IDE4.2\ProgSynthSBT\src\main\scala\expt\RunCompiler2Input.scala""",
            """D:\ProgSynth\IDE4.2\ProgSynthSBT\src\main\scala\expt\MyNumber.scala""" )) // invoke compiler.

    val classLoader = new java.net.URLClassLoader(
        Array(new File(".").toURI.toURL), // Using current directory.
        this.getClass.getClassLoader)

    val clazz = classLoader.loadClass("expt.RunCompiler2Input") // load class

    val x = clazz.newInstance // create an instance
    val inputObj = x.asInstanceOf[RunCompiler2Input]
    val no = inputObj.getNumber()
    println(no)
}

