package expt
import scala.sys.process._
import scala.actors.Futures
import scala.io.Source

//TODO: move to appropriate place.
object PSTimeout {

    case class ProcessStatus(retcode: Option[Int], stdout: List[String], stderr: List[String])
    /**
     * Runs the block returning T within the given timeout.
     * @param timeoutMs: timeout in milliseconds
     * @param f: lazy block returning T
     * @return Returns `Some(<returnValue of the block>)`. `None` in case of timeout
     * Throws exception if `f` throws an exception.
     */
    def runWithTimeout[T](timeoutMs: Long)(f: => T): Option[T] = {
        Futures.awaitAll(timeoutMs, Futures.future(f)).head.asInstanceOf[Option[T]]
    }

    def runWithTimeout[T](timeoutMs: Long, default: T)(f: => T): T = {
        runWithTimeout(timeoutMs)(f).getOrElse(default)
    }

    /**
     * Runs the command within the given timeout.
     * @param cmd: command line string
     * @param timeoutMs: timeout in milliseconds
     * @return ProcessStatus(retcode: Option[Int], stdout: List[String], stderr: List[String])
     * `retcode` is `None` is the process times out
     */
    def runProcessTimeout(cmd: String, timeoutMs: Long): ProcessStatus = {
        var p: Option[Process] = None
	    var out, err: List[String] = List()

        def runProcess(cmd: String): Int = {
		    val pb = Process(cmd)
		    val pio = new ProcessIO(
		            	stdin => (),
	                    stdout => Source.fromInputStream(stdout).getLines.foreach(out ::= _),
	                    stderr => Source.fromInputStream(stderr).getLines.foreach(err ::= _))
		    p = pb.run(pio) match { //run the process
		        case x@_ if x == null => None
		        case x@_ => Some(x)
		    }

		    val exitCode = p.get.exitValue //blocking //TODO: what is p is None
		    exitCode
	    }

        val res = runWithTimeout(timeoutMs) { runProcess(cmd) }
        if (res.isEmpty)
            p.map(_.destroy) //kills the process in case of timeout
        out = out.reverse; err = err.reverse
	    out = out.filter(!_.stripMargin.isEmpty())
	    err = err.filter(!_.stripMargin.isEmpty())
        ProcessStatus(retcode=res, stdout=out, stderr=err)
    }

    /**
     * Runs the given command. May not terminate
     * @param cmd: command line string
     * @return ProcessStatus(retcode: Option[Int], stdout: List[String], stderr: List[String])
     */
    def runProcess(cmd: String): ProcessStatus = {
	    var out: List[String] = Nil
	    var err: List[String] = Nil
	    val pb = Process(cmd)
	    val pio = new ProcessIO(
	            	stdin => (),
                    stdout => Source.fromInputStream(stdout).getLines.foreach(out ::= _),
                    stderr => Source.fromInputStream(stderr).getLines.foreach(err ::= _))
	    val p = pb.run(pio) //run the process
	    val exitVal = p.exitValue
	    out = out.filter(!_.stripMargin.isEmpty())
	    err = err.filter(!_.stripMargin.isEmpty())
	    ProcessStatus( 	retcode = Some(exitVal),
	            		stdout = out.reverse,
	            		stderr = err.reverse)
    }


    def main(args: Array[String]): Unit = {
        /*
        """ REM hello.bat
        	echo off
    		FOR /L %%A IN (1,1,10) DO (
    			sleep 3
    			echo "hello"
    			echo "hello error" 1>&2
    		)
    		echo "done"
        """
        */
        val retVal1 = runProcessTimeout("""d:\tmp2\hello.bat > d:\tmp2\hello1.log""", 20000)
        println(retVal1)

        val retVal2 = runProcessTimeout("""d:\tmp2\hello.bat > d:\tmp2\hello2.log""", 60000)
        println(retVal2)
    }
}
