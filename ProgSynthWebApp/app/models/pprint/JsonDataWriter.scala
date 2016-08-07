package models.pprint

import progsynth.synthesisnew.Tactic
import play.api.libs.json._
import progsynth.synthesisnew._

//object JsonDataWriterObj extends JsonDataWriter

trait JsonDataWriter {
	def mkJSString(t: Tactic): String
	def mkJSVal(t: Tactic): JsValue = {
		t match {
			case it: Init4Tactic =>
				mkConcreteTV("Init4TV", List(
					mkFieldTV("name", null)
				))

				
//					name <- field[String]("name")
//					mutableVars <- field[List[Var]]("mutableVars")
//					immutableVars <- field[List[Var]]("immutableVars")
//					newCtx = ctx.addVals(immutableVars)
//					newCtx2 = newCtx.addVals(mutableVars)
//					preF <- field[TermBool]("preF")(newCtx2)
//					postF <- field[TermBool]("postF")(newCtx2)
//					globalInvs <- field[List[TermBool]]("globalInvs")(newCtx)
		}
	}

	def mkConcreteTV(tvName: String, fields: List[JsValue]): JsValue = {
		JsObject(Seq("concreteTV" ->
			JsObject(Seq(
					"tvName" -> JsString(tvName),
					"fields" -> JsArray(fields)
					))))
	}
	
	def mkFieldTV(fname: String, ftv: JsValue): JsObject = {
			JsObject(Seq(
				"tvName" -> JsString("FieldTV"),
				"fName" -> JsString("name")
				//"ftv" ->
			))
	}
	
	val json: JsValue = JsObject(Seq(
	  "name" -> JsString("Watership Down"),
	  "location" -> JsObject(Seq("lat" -> JsNumber(51.235685), "long" -> JsNumber(-1.309197))),
	  "residents" -> JsArray(Seq(
	    JsObject(Seq(
	      "name" -> JsString("Fiver"),
	      "age" -> JsNumber(4),
	      "role" -> JsNull
	    )),
	    JsObject(Seq(
	      "name" -> JsString("Bigwig"),
	      "age" -> JsNumber(6),
	      "role" -> JsString("Owsla")
	    ))
	  ))
	))

}