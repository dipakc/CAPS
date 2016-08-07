package progsynth.types

//-------------------------------------------------------------------------
// Pred
case class Pred(r: String, ts: List[Term]) extends PredUtils with PSProgTree with CaseRewritable{
	val cp = ComparisonPreds //alias
	//override def toString() =
	//	if (cp.contains(r)) {
	//		"(" + ts(0) + " " + cp.getsym(r) + " " + ts(1) + ")"
	//	} else {
	//		r + "(" + ts.map(_.toString).mkString(",") + ")"
	//	}
	def pprint() = {
	    if (cp contains r){
	        ts.head.pprint + " " + cp.getsym(r) + " " + ts.tail.head.pprint
	    }else
	        this.toString
	}
}

object ComparisonPreds {
	val preds = List("$eq$eq", "$less$eq", "$greater$eq", "$less", "$greater")
	def contains(x: Any) = preds.contains(x)
	/** $bang$eq is not modeled as not $eq$eq in ProgSynth*/
	def getsym(x: String) = x match {
		case "$eq$eq" => "=="
		case "$less$eq" => "<="
		case "$greater$eq" => ">="
		case "$less" => "<"
		case "$greater" => ">"
		case _ => "unknownpred"
	}
}
