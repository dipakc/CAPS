package progsynth.utils
//import scala.reflect.generic.Universe
//import scala.reflect.generic.Trees
//import scala.reflect.generic.Names
import progsynth.PSPredef._
import scala.io._
import progsynth.debug.PSDbg
import scala.collection.mutable.ListBuffer

/**
 * Usage: import progsynth.utils
 * */

object PSUtils extends PSUtils
trait PSUtils {
	//http://stackoverflow.com/questions/2199040/scala-xml-building-adding-children-to-existing-nodes
	import scala.xml.{Node, Elem}
	def addChild(newChild: Node, n: Node) = n match {
		case Elem(prefix, label, attribs, scope, child @ _*) =>
			Elem(prefix, label, attribs, scope, child ++ newChild : _*)
		case _ => sys.error("Can only add children to elements!")
	}

	import java.io._
	def appendFile(fileName: String, line: Any) = {
		val fw = new FileWriter(fileName, true)
		try {
			fw.write(line.toString)
		}
		finally fw.close()
	}
	def overwriteFile(fileName: String, line: Any) = {
		val fw = new FileWriter(fileName, false)
		try {
			fw.write(line.toString)
		}
		finally fw.close()
	}

	def getDirNameFromFullPath(fullPath: String): String = {
		val file = new java.io.File(fullPath)
		file.getParent()
	}

	def formatXml(xmlNode: Node): String = {
		val pp = new scala.xml.PrettyPrinter(200, 1)
		val sb = new StringBuilder
		pp.format(xmlNode, sb)
		sb.toString()
	}

	def formatXml(xmlNodes: Seq[Node]): String = {
		val pp = new scala.xml.PrettyPrinter(200, 1)
		val sb = new StringBuilder
		xmlNodes foreach { xmlNode =>
			pp.format(xmlNode, sb)
		}
		sb.toString()
	}

	def crossProd[T](lists: List[List[T]]): List[List[T]] = {
		val zss: List[List[T]] = List(List())
		def fun(xs: List[T], zss: List[List[T]]): List[List[T]] = {
			for {
				x <- xs
				zs <- zss
			} yield {
				x :: zs
			}
		}
		lists.foldRight(zss)(fun _)
	}

	def crossProd2[T](lists: List[Stream[T]]): List[Stream[T]] = {
		val zss: List[Stream[T]] = List(Stream())
		def fun(xs: Stream[T], zss: List[Stream[T]]): List[Stream[T]] = {
			for {
				zs <- zss
				x <- xs
			} yield {
				x +: zs
			}
		}
		lists.foldRight(zss)(fun _)
	}

	//Copies src to dest
	//src: Source full file path
	//dest: Destination full file path. Destination directory must exist.
	def copyFile(src: String, dest: String): Boolean = {
		import java.io.{ File, FileInputStream, FileOutputStream }
		try {
			val srcF = new File(src)
			val destF = new File(dest)
			new FileOutputStream(destF) getChannel () transferFrom (
					new FileInputStream(srcF) getChannel, 0, Long.MaxValue)
			true
		} catch {
			case _ => false
		}
	}

	//Returns full path given the relative path wrt resource directory
	//input: "/scalac-plugin.xml"
	//outout: "file:/D:/EclipseProjects/ProgSynthSBT/target/scala-2.9.1/classes/scalac-plugin.xml"
	def getFullFilePath(relPath: String): Option[String] = {
		try {
			Some(getClass.getResource(relPath).getPath())
		} catch {
			case x => PSDbg.logln(x); None
		}
	}

	def getBufFromRelativePath(filePath: String):Option[BufferedSource] = {
			var bufSource: BufferedSource = null
			try {
				val resourceUrl = getClass.getResource(filePath)
				bufSource = scala.io.Source.fromURL(resourceUrl)
				Some(bufSource)
			} catch {
				case _ => None
			}
	}


	def getLines(filePath: String): Option[Iterator[String]] = {
		try {
			val bufSource = Source.fromFile(filePath)
			Some(bufSource.getLines())
		} catch {
			case _ => None
		}

	}

	//	def foo(t: Int) = {println(t); if (t == 5) None else Some(t)}
	//val ts = List(1, 2, 3, 4, 5, 6, 7, 8)
	//println(allmap(ts, foo))
	//expected output:
	//	1
	//	2
	//	3
	//	4
	//	5
	//	None
	//val ts2 = List(1, 2, 3, 4, 6, 7, 8)
	//println(allmap(ts2, foo))
	//expected output:
	//	1
	//	2
	//	3
	//	4
	//	6
	//	7
	//	8
	//	Some(List(1, 2, 3, 4, 6, 7, 8))

	def allmap[T, U](xs: List[T], f: T => Option[U]): Option[List[U]] = {
		val res = xs.toStream.map(f).takeWhile(_.isDefined).map(_.get).toList
		if (res.length == xs.length)
			Some(res)
		else
			None
	}

	//Same functionality as allmap. But easier to debug.
	def allmap2[T, U](xs: List[T], f: T => Option[U]): Option[List[U]] = {
		val resList = new ListBuffer[U]()
		def processList(ys: List[T]): Unit = ys match {
			case y::zs =>
				val fyOpt = f(y)
				fyOpt match {
					case Some(fy) =>
						resList.append(fy)
						processList(zs)
					case None =>
				}
			case Nil =>
		}
		processList(xs)
		if(resList.length == xs.length)
			Some(resList.toList)
		else
			None
	}

	def getStackTraceString(exception: Throwable) = {
		val sw = new StringWriter
		exception.printStackTrace(new PrintWriter(sw))
		sw.toString
	}
}
