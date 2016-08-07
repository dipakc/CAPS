package progsynth.debug
import progsynth.debug.PSDbg._

import progsynth.config.AppConfig
import scala.xml._
import progsynth.utils.{PSUtils=>psu}
//Generates XSLT
object IdentityXSLT {

	//case 1: elements with child elements
	//case 2: elements with no child elements (child text nodes may exist)
	def applyTemplateChildren(elemStr: String) = {
		<xsl:choose>
		<xsl:when test={elemStr + "/*"}>
			<xsl:apply-templates select={elemStr  + "/*"}/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:apply-templates select={elemStr  + "/text()"}/>
		</xsl:otherwise>
		</xsl:choose>
	}
	def valueOf(selectStr: String) = <xsl:value-of select={selectStr}/>
	def copyOf(selectStr: String) = <xsl:copy-of select={selectStr}/>

	//text nodes
	def genericTextTemplate(): Elem = {
		<xsl:template match="text()">
			{copyOf(".")}
		</xsl:template>
	}

	//element template. Only unimplemented elements will match this.
	def genericNonTextTemplate(): Elem = {
		<xsl:template match="*">
			<unimpl>
				{copyOf(".")}
			</unimpl>
		</xsl:template>
	}

	def xsltTemplate(typeName: String, fieldIds: List[String]) = {
		import scala.xml.{Node, Elem, TopScope, Attribute}
		val childs = for (field <- fieldIds) yield {
			Elem(null, field, null, TopScope, applyTemplateChildren(field))
		}
		val typeElem = Elem(null, typeName, null, TopScope, childs: _*)
		Elem("xsl", "template", Attribute(None, "match", Text(typeName), Null), TopScope, typeElem)
	}

	def compileXslt = {
		var stlElem = <xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"> </xsl:stylesheet>
		stlElem = psu.addChild(xsltTemplate("ProgramAnn", List("id", "pre", "program", "post")), stlElem)
		stlElem = psu.addChild(xsltTemplate("GuardedCmd", List("guard", "cmd")), stlElem)
		stlElem = psu.addChild(xsltTemplate("IfProg", List("grdcmds")), stlElem)
		stlElem = psu.addChild(xsltTemplate("WhileProg", List("grdcmds")), stlElem)
		stlElem = psu.addChild(xsltTemplate("Composition", List("programs")), stlElem)
		stlElem = psu.addChild(xsltTemplate("Assignment", List("lhs", "rhs")), stlElem)
		stlElem = psu.addChild(xsltTemplate("SkipProg", Nil), stlElem)
		stlElem = psu.addChild(xsltTemplate("UnknownProg", Nil), stlElem)
		stlElem = psu.addChild(xsltTemplate("Identifier", List("name", "itype")), stlElem)
		stlElem = psu.addChild(xsltTemplate("LitConstant", List("name")), stlElem)
		stlElem = psu.addChild(xsltTemplate("Invariant", List("loc", "formula", "rvVar")), stlElem)
		stlElem = psu.addChild(xsltTemplate("True1", Nil), stlElem)
		stlElem = psu.addChild(xsltTemplate("False1", Nil), stlElem)
		stlElem = psu.addChild(xsltTemplate("Atom", List("a")), stlElem)
		stlElem = psu.addChild(xsltTemplate("Not", List("f")), stlElem)
		stlElem = psu.addChild(xsltTemplate("And", List("f1", "f2")), stlElem)
		stlElem = psu.addChild(xsltTemplate("Or", List("f1", "f2")), stlElem)
		stlElem = psu.addChild(xsltTemplate("Impl", List("f1", "f2")), stlElem)
		stlElem = psu.addChild(xsltTemplate("Iff", List("f1", "f2")), stlElem)
		stlElem = psu.addChild(xsltTemplate("Forall", List("v", "f")), stlElem)
		stlElem = psu.addChild(xsltTemplate("Exists", List("v", "f")), stlElem)
		stlElem = psu.addChild(xsltTemplate("Unknown", Nil), stlElem)
		stlElem = psu.addChild(xsltTemplate("Pred", List("r", "ts")), stlElem)
		stlElem = psu.addChild(xsltTemplate("Var", List("v", "t")), stlElem)
		stlElem = psu.addChild(xsltTemplate("Fn", List("f", "ts")), stlElem)
		stlElem = psu.addChild(genericTextTemplate, stlElem)
		stlElem = psu.addChild(genericNonTextTemplate, stlElem)
		stlElem
	}

	def main(args: Array[String]) {
		val xslt = compileXslt
		scala.xml.XML.save("""c:\temp\gxslt.xsl""", xslt)
		logln("""Identity xslt generated at c:\temp\gxslt.xsl""")
	}
}

///////////////////////////////////////////////////////

// 		<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />
//	def programAnnTemplate(): Elem = {
//		<xsl:template match="ProgramAnn">
//			<ProgramAnn>
//				<id>{valueOf("id")}</id>
//				<pre>{applyTemplate("pre/*")}</pre>
//				<program>{applyTemplate("program/*")}</program>
//				<post>{applyTemplate("post/*")}</post>
//			</ProgramAnn>
//		</xsl:template>
//	}

//	def xsltTemplate(typeName: String, fieldIds: List[String], baseFields: List[String]) = {
//		import scala.xml.{Node, Elem, TopScope, Attribute}
//		//Elem(prefix, label, attribs, scope, child @ _*)
//		val childs = for (field <- fieldIds) yield {
//			if (baseFields contains field)
//				Elem(null, field, null, TopScope, valueOf(field))
//			else
//				Elem(null, field, null, TopScope, applyTemplate(field + "/*"))
//		}
//		Elem("xsl", "template", Attribute(None, "match", Text(typeName), Null), TopScope, childs: _*)
//	}

//	def programAnnTemplate(): Elem = {
//		<xsl:template match="ProgramAnn">
//			<ProgramAnn>
//				<id>{valueOf("id")}</id>
//				<pre>{applyTemplate("pre/*")}</pre>
//				<program>{applyTemplate("program/*")}</program>
//				<post>{applyTemplate("post/*")}</post>
//			</ProgramAnn>
//		</xsl:template>
//	}
//
//	def invariantTemplate(): Elem = {
//		<xsl:template match="Invariant">
//			<Invariant>
//				<loc>{valueOf("loc")}</loc>
//				<formula>{applyTemplate("formula/*")}</formula>
//				<rvVar>{applyTemplate("rvVar/*")}</rvVar>
//			</Invariant>
//		</xsl:template>
//	}

//	def genericTemplate(): Elem = {
//		<xsl:template match="*">
//			<xsl:choose>
//            	<xsl:when test="* or text()">
//					<unimpl>
//						{copyOf(".")}
//					</unimpl>
//            	</xsl:when>
//            	<xsl:otherwise>
//					{copyOf(".")}
//            	</xsl:otherwise>
//			</xsl:choose>
//		</xsl:template>
//	}
	//val pp = new scala.xml.PrettyPrinter(80, 2)
	//val x = pp.format(xslt)
	//Utils.overwriteFile("""c:\temp\gxslt.xsl""", x)
//	def xsltHeader =
//		<?xml version="1.0" encoding="utf-8"?>
//	def applyTemplate(selectStr:String) = {
//		<xsl:apply-templates select={selectStr}/>
//	}
//
