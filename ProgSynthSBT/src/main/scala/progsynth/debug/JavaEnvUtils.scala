package progsynth.debug

import java.net.URLClassLoader

import progsynth.debug.PSDbg._

//TODO: Add individual property print methods.
object JavaEnvUtils {
  //TODO: add newline seperator
  def printJavaProperties() = {
    logln("#########################################")
    logln("java.class.path = " + System.getProperty("java.class.path"))
    logln("#########################################")
    logln("java.library.path = " + System.getProperty("java.library.path"))
    logln("#########################################")
    logln("sun.boot.class.path = " + System.getProperty("sun.boot.class.path"))
    logln("#########################################")
    logln("sun.boot.library.path = " + System.getProperty("sun.boot.library.path"))
    logln("#########################################")
    logln("java.ext.dirs = " + System.getProperty("java.ext.dirs"))
    logln("-------------------------------------------")
  }

  //val loader = classOf[ProgSynthComponent].getClassLoader()
  //writeln0(progsynth.debug.JavaEnvUtils.getCLStr(loader))
  def getCLStr(cl: ClassLoader): String = {
    if (cl == null)
      return ""
    val nl = "\n"
    val parentStr =
          if (cl.getParent() != null)
            "parent(" + nl +
             	getCLStr(cl.getParent()) + nl +
             ")" + nl
          else
            ""
    val urlStr = getURLStr(cl)
    val retVal =
      "ClassLoader(" + nl +
      	"hashcode(" + cl.hashCode() + ")"  + nl +
      	"Class( " + cl.getClass() + ")" +  nl +
      	urlStr + nl +
      	parentStr +
      ")"  + nl
    retVal
  }

  def getURLStr(cl: ClassLoader): String = {
    val nl = "\n"
    try {
      val urlcl = cl.asInstanceOf[URLClassLoader]
       "URLs(" + urlcl.getURLs().mkString(",") + ")"  + nl
    } catch {
      case _ => ""
    }
  }
}
