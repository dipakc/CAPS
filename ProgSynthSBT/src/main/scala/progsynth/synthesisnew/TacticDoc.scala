package progsynth.synthesisnew
import scala.xml._

case class TacticField(name: String, displayName: String, ftype: String, description: String)

abstract class TacticDoc {
    val name: String
	val shortDescription: String
	val tacticInputs: List[TacticField]
	val appConditions: List[String]
	val hint: String
	val notes: List[String]
    val transfDiv: Option[Elem] //Transformation Div

    def dName(name: String): String = {
	    tacticInputs.find(_.name==name).map{_.displayName}.getOrElse("Error")
    }

    def toHtml(): Elem = {
	    <div class="TacticDoc" id={name}>
   			<div class="TacticName">{name}</div>
   			<div class="TacticDesc">{shortDescription}</div>
   			<div class="TacticInputs"> {
   				if (!tacticInputs.isEmpty) {
	   				<div class="THeader">Input Parameters</div>
	   				<ul>{
	    		    	for (TacticField(_, param, ptype, pdesc) <- tacticInputs) yield {
	    		    		<li>
    		    			<div>
    		    				<span class = 'param'>{param + ":"}  </span>
    		    				<span class = 'ptype'>{ptype}</span>
    		    				<span class = 'pdesc'>{pdesc}</span>
    		    			</div>
    		    			</li>
			    		}
			    	}
	   				</ul>
   				}
   			}
    		</div>
   			<div class="TransfDiv">
   				{ if(transfDiv.isDefined)
   					<div>
   						<div class="THeader">Transformation Details</div>
   						{transfDiv.get}
   					</div>
   				}
   			</div>
    		<div class="AppConditions"> {
    		    if (!appConditions.isEmpty) {
    		        <div class="THeader">Applicability Conditions</div>
    		      	<ul>{
    		       		for (appCondition <- appConditions) yield {
    		       			<div>
								<li>{appCondition}</li>
							</div>
    		       		}
    		       	}
    		       	</ul>
    		    }
    		}
    		</div>
    		<div class="Notes">{
    		    if (!notes.isEmpty) {
    		        <div class="THeader">Notes</div>
    		      	<ul>{
    		       		for (aNote <- notes) yield {
    		       			<div>
								<li>{aNote}</li>
							</div>
    		       		}
    		       	}
    		       	</ul>
    		    }
    		}
    		</div>

    	</div>
    }

}

abstract class FormulaTacticDoc extends TacticDoc {
	val appliesTo = "Formula"

}

abstract class ProgramTacticDoc extends TacticDoc {
	val appliesTo = "Program"
}

abstract class InitTacticDoc extends TacticDoc {
    val appliesTo = "Empty Derivation"
}
