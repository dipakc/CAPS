package worksheets
import scala.xml._

object Test {
	/*
	val elem = <a>content</a>
	val elem2 =
		elem % Attribute(pre= None, key = "class", value = Text("bracketed_scroll_mode"), next = xml.Null)

	val elem3 =
		elem2 % Attribute(pre= None, key = "attr1", value = Text("attr1val"), next = xml.Null)
	elem3.attributes.asAttrMap.get("class")
	elem3.attributes.get("class").get.asInstanceOf[Text].mkString

	val attr1 = Attribute(pre= None, key = "key1", value = Text("val1"), next = xml.Null)
	val attr2 = Attribute(pre= None, key = "key2", value = Text("val2"), next = attr1)
	val elem4 = elem % attr2

	//val attr3 = Attribute(pre= None, key = "key3", value = Text("val3"), next = xml.Null)
*/

	implicit def addGoodCopyToAttribute(attr: Attribute) = new {
		def goodcopy(key: String = attr.key, value: Any = attr.value): Attribute =
			Attribute(attr.pre, key, Text(value.toString), attr.next)
	}                                         //> addGoodCopyToAttribute: (attr: scala.xml.Attribute)AnyRef{def goodcopy(key: 
                                                  //| String,value: Any): scala.xml.Attribute; def goodcopy$default$1: String @sca
                                                  //| la.annotation.unchecked.uncheckedVariance; def goodcopy$default$2: Any @scal
                                                  //| a.annotation.unchecked.uncheckedVariance}
	implicit def iterableToMetaData(items: Iterable[MetaData]): MetaData = {
		items match {
			case Nil => Null
			case head :: tail => head.copy(next = iterableToMetaData(tail))
		}
	}                                         //> iterableToMetaData: (items: Iterable[scala.xml.MetaData])scala.xml.MetaData
                                                  //| 
	val elem = <b attr1="100" attr2="50"/>    //> elem  : scala.xml.Elem = <b attr1="100" attr2="50"/>

	val elem2 = elem.copy(attributes =
		for (attr <- elem.attributes) yield attr match {
			case attr @ Attribute("attr1", _, _) =>
				attr.goodcopy(value = attr.value.text.toInt * 2)
			case attr @ Attribute("attr2", _, _) =>
				attr.goodcopy(value = attr.value.text.toInt * -1)
			case other => other
		})                                //> elem2  : scala.xml.Elem = <b attr1="200" attr2="-50"/>


	val elem3 = <a class="x y"> content </a>  //> elem3  : scala.xml.Elem = <a class="x y"> content </a>

		val x: Option[String] = elem3.attributes match {
			case attr@ Attribute("class", _, _) => Some(attr.value.text)
			case _ => None
		}                                 //> x  : Option[String] = Some(x y)

	val minus = """&minus;"""                 //> minus  : String = &minus;
	val node = <div> 5 {minus} 6</div>        //> node  : scala.xml.Elem = <div> 5 &amp;minus; 6</div>

	val minusNew = scala.xml.Unparsed("""&minus;""")
                                                  //> minusNew  : scala.xml.Unparsed = &minus;
	val nodeNew = <div> 5 {minusNew} 6</div>  //> nodeNew  : scala.xml.Elem = <div> 5 &minus; 6</div>
}