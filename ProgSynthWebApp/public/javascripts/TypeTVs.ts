
import typeViews = require("TypeViews"); typeViews;
import typeReg = require("TypeRegistry"); typeReg;



export function objToTV(json: any): TV {
    var tvClass = typeReg.getClass(json.tvName);
    var r = tvClass.mkEmptyInstance();
    r.fill(json);
    return r;
}

// TypedValue
export class TV {
    tvName: string;
    constructor() {
        this.tvName = "TV";
    }
    clone(): TV {
        throw new Error("Abstract method clone not implemented in subclass of TV");
    }
    copyTo(arg: TV) {
        arg.tvName = this.tvName;
    }

    fill(obj: any) {
        this.tvName = obj.tvName;
        return this;
    }

    static mkEmptyInstance(): TV {
        return new TV();
    }
}

export class AbstractTV extends TV {
    subTVNames: string[];
    concreteTV: TV;
    constructor(subTVs: string[]) {
        super();
        this.tvName = "AbstractTV";
        this.subTVNames = subTVs;
        this.concreteTV = undefined;
    }
    clone(): AbstractTV {
        var retVal = new AbstractTV(undefined);
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: AbstractTV) {
        super.copyTo(arg);
        arg.subTVNames = this.subTVNames;
        arg.concreteTV = this.concreteTV.clone();
    }
    fill(obj: any) {
        super.fill(obj);
        this.subTVNames = obj.subTVNames;
        this.concreteTV = objToTV(obj.concreteTV);
        return this;
    }

    static mkEmptyInstance(): AbstractTV {
        return new AbstractTV([]);
    }   
}

export class NewVarTV extends TV {
    varNameTV: StringTV;
    varTypeTV: PSTypeTV;
    constructor(varNameTV, varTypeTV) {
        super();
        this.tvName = "NewVarTV";
        this.varNameTV = varNameTV;
        this.varTypeTV = varTypeTV;
    }
    clone(): NewVarTV {
        var retVal = new NewVarTV(undefined, undefined);
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: NewVarTV) {
        super.copyTo(arg);
        arg.varNameTV = this.varNameTV.clone();
        arg.varTypeTV = this.varTypeTV.clone();
    }
    fill(obj: any) {
        super.fill(obj);
        this.varNameTV = <StringTV>objToTV(obj.varNameTV);
        this.varTypeTV = <PSTypeTV>objToTV(obj.varTypeTV);
        return this;
    }

    static mkEmptyInstance(): NewVarTV {
        return new NewVarTV(undefined, undefined);
    }   
}

export class EnumTV extends TV {
    elements: string[];
    selectedElem: string;
    constructor(elements: string[], selectedElem: string) {
        super();
        this.tvName = "EnumTV";
        this.elements = elements;
        this.selectedElem = selectedElem;
    }
    clone(): EnumTV {
        var retVal = new EnumTV(undefined, undefined);
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: EnumTV) {
        super.copyTo(arg);
        arg.elements = this.elements;
        arg.selectedElem = this.selectedElem;
    }

    fill(obj: any) {
        super.fill(obj);
        this.elements = obj.elements;
        this.selectedElem = obj.selectedElem;
        return this;
    }

    static mkEmptyInstance(): EnumTV {
        return new EnumTV([], "");
    }   

}

export class PSTypeTV extends EnumTV {
    constructor(selectedElem: string) {
        super(['Int', 'Bool', 'ArrayInt', 'ArrayBool'], selectedElem);
        this.tvName = "PSTypeTV";
    }
    clone(): PSTypeTV {
        var retVal = new PSTypeTV(undefined);
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: PSTypeTV) {
        super.copyTo(arg);
    }

    fill(obj: any) {
        super.fill(obj);
        return this;
    }

    static mkEmptyInstance(): PSTypeTV {
        return new PSTypeTV("");
    }   
}

export class ClassTV extends TV {
    fields: FieldTV[];
    constructor(fields: FieldTV[]) {
        super();
        this.tvName = "ClassTV";
        this.fields = fields;
    }
    clone(): ClassTV {
        var retVal = new ClassTV(undefined);
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: ClassTV) {
        super.copyTo(arg);
        arg.fields = _.map(this.fields, (field) => field.clone());
    }
    fill(obj: any) {
        super.fill(obj);
        this.fields = _.map(obj.fields, function (f) { return <FieldTV>objToTV(f) });
        return this;
    }

    static mkEmptyInstance(): ClassTV {
        return new ClassTV([]);
    }   
}

export class FieldTV extends TV {
    fname: string;
    displayName: string;
    ftv: TV;
    constructor(fname: string, displayName: string, ftv: TV) {
        super();
        this.tvName = "FieldTV"
            this.fname = fname;
        this.displayName = displayName;
        this.ftv = ftv;
    }
    clone(): FieldTV {
        var retVal = new FieldTV(undefined, undefined, undefined);
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: FieldTV) {
        super.copyTo(arg);
        arg.fname = this.fname;
        arg.displayName = this.displayName;
        arg.ftv = this.ftv.clone();
    }
    fill(obj: any) {
        super.fill(obj);
        this.fname = obj.fname;
        this.displayName = obj.displayName;
        this.ftv = objToTV(obj.ftv);
        return this;
    }

    static mkEmptyInstance(): FieldTV {
        return new FieldTV("", "", undefined);
    }   

}

export class PrimitiveTV extends TV {
    value: any;
    constructor(value?: any) {
        super();
        this.tvName = "PrimitiveTV";
        this.value = value;
    }
    clone(): PrimitiveTV {
        var retVal = new PrimitiveTV(undefined);
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: PrimitiveTV) {
        super.copyTo(arg);
        arg.value = this.value;
    }

    fill(obj: any) {
        super.fill(obj);
        this.value = obj.value;
        return this;
    }

    static mkEmptyInstance(): PrimitiveTV {
        return new PrimitiveTV();
    }   
}

//User TVs
export class IntegerTV extends PrimitiveTV {
    value: number;
    constructor(value?: number) {
        super(value);
        this.tvName = "IntegerTV";
        this.value = value;
    }
    clone(): IntegerTV {
        var retVal = new IntegerTV(undefined);
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: IntegerTV) {
        super.copyTo(arg);
        arg.value = this.value;
    }
    fill(obj: any) {
        super.fill(obj);
        this.value = obj.value;
        return this;
    }

    static mkEmptyInstance(): IntegerTV {
        return new IntegerTV();
    }
}

export class FDisplayIdTV extends IntegerTV {
    value: number;
    constructor(value?: number) {
        super(value);
        this.tvName = "FDisplayIdTV";
        this.value = value;
    }
    clone(): FDisplayIdTV {
        var retVal = new FDisplayIdTV(undefined);
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: FDisplayIdTV) {
        super.copyTo(arg);
        arg.value = this.value;
    }
    fill(obj: any) {
        super.fill(obj);
        this.value = obj.value;
        return this;
    }

    static mkEmptyInstance(): FDisplayIdTV {
        return new FDisplayIdTV();
    }
}

export class PDisplayIdTV extends IntegerTV {
    value: number;
    constructor(value?: number) {
        super(value);
        this.tvName = "PDisplayIdTV";
        this.value = value;
    }
    clone(): PDisplayIdTV {
        var retVal = new PDisplayIdTV(undefined);
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: PDisplayIdTV) {
        super.copyTo(arg);
        arg.value = this.value;
    }
    fill(obj: any) {
        super.fill(obj);
        this.value = obj.value;
        return this;
    }

    static mkEmptyInstance(): PDisplayIdTV {
        return new PDisplayIdTV();
    }
}

export class StringTV extends PrimitiveTV {
    value: string;
    constructor(value?: string) {
        super(value);
        this.tvName = "StringTV";
        this.value = value;
    }
    clone(): StringTV {
        var retVal = new StringTV(undefined);
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: StringTV) {
        super.copyTo(arg);
        arg.value = this.value;
    }
    fill(obj: any) {
        super.fill(obj);
        this.value = obj.value;
        return this;
    }

    static mkEmptyInstance(): StringTV {
        return new StringTV();
    }

}

export class TermTV extends PrimitiveTV {
    value: string;
    constructor(value?: string) {
        super(value);
        this.tvName = "TermTV";
        this.value = value;
    }
    clone(): TermTV {
        var retVal = new TermTV(undefined);
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: TermTV) {
        super.copyTo(arg);
        arg.value = this.value;
    }
    fill(obj: any) {
        super.fill(obj);
        this.value = obj.value;
        return this;
    }

    static mkEmptyInstance(): TermTV {
        return new TermTV();
    }
}

export class VarTV extends PrimitiveTV {
    value: string;
    constructor(value?: string) {
        super(value);
        this.tvName = "VarTV";
        this.value = value;
    }
    clone(): VarTV {
        var retVal = new VarTV(undefined);
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: VarTV) {
        super.copyTo(arg);
        arg.value = this.value;
    }
    fill(obj: any) {
        super.fill(obj);
        this.value = obj.value;
        return this;
    }

    static mkEmptyInstance(): VarTV {
        return new VarTV();
    }

}

//export class NewVarType extends Type {
//    varName: string;
//    varType: PSType;
//    constructor(varName) {
//        super("NewVarType");
//    }
//    clone(): NewVarType {
//        var retVal = new NewVarType();
//        this.copyTo(retVal);
//        return retVal;
//    }
//    copyTo(arg: NewVarType) {
//        super.copyTo(arg);
//    }
//}

export class FOLFormulaTV extends PrimitiveTV {
    value: string;
    constructor(value?: string) {
        super(value);
        this.tvName = "FOLFormulaTV";
        this.value = value;
    }
    clone(): FOLFormulaTV {
        var retVal = new FOLFormulaTV(undefined);
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: FOLFormulaTV) {
        super.copyTo(arg);
        arg.value = this.value;
    }
    fill(obj: any) {
        super.fill(obj);
        this.value = obj.value;
        return this;
    }

    static mkEmptyInstance(): FOLFormulaTV {
        return new FOLFormulaTV();
    }

}

export class TermBoolTV extends PrimitiveTV {
    value: string;
    constructor(value?: string) {
        super(value);
        this.tvName = "TermBoolTV";
        this.value = value;
    }
    clone(): TermBoolTV {
        var retVal = new TermBoolTV(undefined);
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: TermBoolTV) {
        super.copyTo(arg);
        arg.value = this.value;
    }

    fill(obj: any) {
        super.fill(obj);
        this.value = obj.value;
        return this;
    }

    static mkEmptyInstance(): TermBoolTV {
        return new TermBoolTV();
    }
    
}

export class ListTV extends TV {
    metaTV: TV; //meta tv
    items: TV[];
    //TODO: make the val argument optional.        
    ////ListTV constructor can not be optional
    ////An empty list must be able to render itself.
    constructor(itv: TV, items: TV[]) {
        super();
        this.tvName = "ListTV";
        this.metaTV = itv;
        this.items = items;
    }
    clone(): ListTV {
        var retVal = new ListTV(undefined, undefined);
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: ListTV) {
        super.copyTo(arg);
        arg.metaTV = this.metaTV.clone();
        arg.items = _.map(this.items, (item) => item.clone());
    }
    fill(obj: any) {
        super.fill(obj);
        this.metaTV = objToTV(obj.metaTV);
        this.items = <TV[]>(_.map(obj.items, objToTV));
        return this;
    }

    static mkEmptyInstance(): ListTV {
        return new ListTV(undefined, []);
    }

}

export class TupleTV extends TV {
    item1: TV;
    item2: TV;
    constructor(val1: TV, val2: TV) {
        super();
        this.tvName = "TupleTV";
        this.item1 = val1;
        this.item2 = val2;
    }
    clone(): TupleTV {
        var retVal = new TupleTV(undefined, undefined);
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: TupleTV) {
        super.copyTo(arg);
        arg.item1 = this.item1.clone();
        arg.item2 = this.item2.clone();
    }
    fill(obj: any) {
        super.fill(obj);
        this.item1 = objToTV(obj.item1);
        this.item2 = objToTV(obj.item2);
        return this;
    }

    static mkEmptyInstance(): TupleTV {
        return new TupleTV(undefined, undefined);
    }

}

export class StepIntoUnknownProgIdxTV extends ClassTV {
    idx: number;
    constructor(idx?: number) {
        var idxField = new FieldTV('idx', 'idx', new IntegerTV(idx))
        var fields: FieldTV[] = [idxField];
        super(fields);
        this.tvName = "StepIntoUnknownProgIdxTV";
        this.idx = idx;
    }
    clone(): StepIntoUnknownProgIdxTV {
        var retVal = new StepIntoUnknownProgIdxTV();
        this.copyTo(retVal);
        return retVal;
    }
    copyTo(arg: StepIntoUnknownProgIdxTV) {
        super.copyTo(arg);
        arg.idx = this.idx;
    }
    static mkEmptyInstance(): StepIntoUnknownProgIdxTV {
        return new StepIntoUnknownProgIdxTV();
    }
    fill(obj: any) {
        super.fill(obj);
        this.idx = obj.idx;
        return this;
    }
}

//InitTactic(name: String, params: List[Var], retVar: Var, preF: FOLFormula, postF: FOLFormula) 
export class InitTV extends ClassTV {
    constructor(name: string, params: string[], retVar: string, preF: string, postF: string) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('name', 'name', new StringTV(name)));
        var newVarTVList = _.map(params, param => { new NewVarTV(new StringTV(param), new PSTypeTV(undefined)) });
        fields.push(new FieldTV('params', 'params', new ListTV(new NewVarTV(new StringTV(""), new PSTypeTV(undefined)), newVarTVList)));
        fields.push(new FieldTV('retVar', 'retVar', new NewVarTV(new StringTV(name), new PSTypeTV(undefined))));
        fields.push(new FieldTV('preF', 'preF', new TermBoolTV(name)));
        fields.push(new FieldTV('postF', 'postF', new TermBoolTV(name)));
        super(fields);
        this.tvName = "InitTV";
    }
    static mkEmptyInstance(): InitTV {
        return new InitTV("", [], "", "", "");
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }
}

export class RetValTV extends ClassTV {
    constructor(initTerm: string) {
        var initTermF = new FieldTV('initTerm', 'initTerm', new TermTV(initTerm));
        var fields: FieldTV[] = [initTermF];
        super(fields);
        this.tvName = "RetValTV";
    }
    static mkEmptyInstance(): RetValTV {
        return new RetValTV("");
    }

    fill(obj: any) {
        super.fill(obj);
        return this;
    }
}

export class DeleteConjunctTV extends ClassTV {
    //conjunct: FOLFormula, variant: Term
    constructor(conjunct: string, variant: string) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('conjunct', 'conjunct', new TermBoolTV(conjunct)));
        fields.push(new FieldTV('variant', 'variant', new TermTV(variant)));
        super(fields);
        this.tvName = "DeleteConjunctTV";
    }
    static mkEmptyInstance(): DeleteConjunctTV {
        return new DeleteConjunctTV("", "");
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }
}

//IntroAssignmentTactic(lhsRhsTuples:List[(Var, Term)]) extends FunTactic {
export class IntroAssignmentTV extends ClassTV {
    constructor(lhsRhsTuples: string[][]) {
        var fields: FieldTV[] = [];
        var tupleTVList = _.map(lhsRhsTuples,
            function (tuple) {
                var tv1 = new TermTV(tuple[0]);
                var tv2 = new TermTV(tuple[1]);
                new TupleTV(tv1, tv2);
            });

        fields.push(new FieldTV('lhsRhsTuples', 'lhsRhsTuples', new ListTV(new TupleTV(new TermTV(), new TermTV()), tupleTVList)));
        super(fields);
        this.tvName = "IntroAssignmentTV";
    }
    static mkEmptyInstance(): IntroAssignmentTV {
        return new IntroAssignmentTV([[]]);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }
}

export class IntroAssignmentEndTV extends ClassTV {
    constructor(lhsRhsTuples: string[][]) {
        var fields: FieldTV[] = [];
        var tupleTVList = _.map(lhsRhsTuples,
            function (tuple) {
                var tv1 = new TermTV(tuple[0]);
                var tv2 = new TermTV(tuple[1]);
                new TupleTV(tv1, tv2);
            });

        fields.push(new FieldTV('lhsRhsTuples', 'lhsRhsTuples', new ListTV(new TupleTV(new TermTV(), new TermTV()), tupleTVList)));
        super(fields);
        this.tvName = "IntroAssignmentEndTV";
    }
    static mkEmptyInstance(): IntroAssignmentEndTV {
        return new IntroAssignmentEndTV([[]]);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

//Init4Tactic(name: String, mutableVars: List[Var], immutableVars: List[Var],
//	preF: TermBool, postF: TermBool, globalInvs: List[TermBool])
export class Init4TV extends ClassTV {
    constructor(name: string, immutableVars: string[], mutableVars: string[], globalInvs: string[], preF: string, postF: string ) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('name', 'Derivation Name', new StringTV(name)));
        var newVarImmutableTVList = _.map(immutableVars, immutableVar => { new NewVarTV(new StringTV(immutableVar), new PSTypeTV(undefined)) });
        var newVarMutableTVList = _.map(mutableVars, mutableVar => { new NewVarTV(new StringTV(mutableVar), new PSTypeTV(undefined)) });
        fields.push(new FieldTV('immutableVars', 'Constants', new ListTV(new NewVarTV(new StringTV(""), new PSTypeTV(undefined)), newVarImmutableTVList)));            
        fields.push(new FieldTV('mutableVars', 'Variables', new ListTV(new NewVarTV(new StringTV(""), new PSTypeTV(undefined)), newVarMutableTVList)));
        var globalInvsTVList = _.map(globalInvs, x => { new TermBoolTV(x) });
        fields.push(new FieldTV('globalInvs', 'Global Invariants', new ListTV(new TermBoolTV(""), globalInvsTVList)));
        fields.push(new FieldTV('preF', 'Precondition', new TermBoolTV(preF)));
        fields.push(new FieldTV('postF', 'Postcondition', new TermBoolTV(postF)));
        super(fields);
        this.tvName = "Init4TV";
    }
    static mkEmptyInstance(): Init4TV {
        return new Init4TV("", [], [], [], "", "");
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class StartIfDerivationTV extends ClassTV {
    constructor( lhsVars: string[]) {
        var fields: FieldTV[] = [];
        var varTVList = _.map(lhsVars, lVar=> { new VarTV(lVar) });
        fields.push(new FieldTV('lhsVars', 'lhsVars', new ListTV(new VarTV(""), varTVList)));
        super(fields);
        this.tvName = "StartIfDerivationTV";
    }
    static mkEmptyInstance(): StartIfDerivationTV {
        return new StartIfDerivationTV([]);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class StepIntoBATV extends ClassTV {
    constructor(lhsVars: string[]) {
        var fields: FieldTV[] = [];
        var varTVList = _.map(lhsVars, lVar=> { new VarTV(lVar) });
        fields.push(new FieldTV('lhsVars', 'lhsVars', new ListTV(new VarTV(""), varTVList)));
        super(fields);
        this.tvName = "StepIntoBATV";
    }
    static mkEmptyInstance(): StepIntoBATV {
        return new StepIntoBATV([]);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class StepIntoIFBATV extends ClassTV {
    constructor(lhsVars: string[]) {
        var fields: FieldTV[] = [];
        var varTVList = _.map(lhsVars, lVar=> { new VarTV(lVar) });
        fields.push(new FieldTV('lhsVars', 'lhsVars', new ListTV(new VarTV(""), varTVList)));
        super(fields);
        this.tvName = "StepIntoIFBATV";
    }
    static mkEmptyInstance(): StepIntoIFBATV {
        return new StepIntoIFBATV([]);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class IntroSwapTV extends ClassTV {
    constructor(array: string, index1: string, index2: string) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('array', 'array', new VarTV(array)));
        fields.push(new FieldTV('index1', 'index1', new TermTV(index1)));
        fields.push(new FieldTV('index2', 'index2', new TermTV(index2)));
        super(fields);
        this.tvName = "IntroSwapTV";
    }
    static mkEmptyInstance(): IntroSwapTV {
        return new IntroSwapTV("", "", "");
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}
  		
export class IntroIfTV extends ClassTV {
    constructor(guards: string[]) {
        var fields: FieldTV[] = [];
        var guardsTVList = _.map(guards, x => { new TermBoolTV(x) });
        fields.push(new FieldTV('guards', 'guards', new ListTV(new TermBoolTV(""), guardsTVList)));
        super(fields);
        this.tvName = "IntroIfTV";
    }
    static mkEmptyInstance(): IntroIfTV {
        return new IntroIfTV([]);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class InstantiateMetaTV extends ClassTV {
    constructor(primedVarTermList: string[][]) {
        var fields: FieldTV[] = [];
        var tupleTVList = _.map(primedVarTermList,
            function (tuple) {
                var tv1 = new StringTV(tuple[0]);//The metavariable is passed as normal string tv.
                var tv2 = new TermTV(tuple[1]);
                new TupleTV(tv1, tv2);
            });

        fields.push(new FieldTV('primedVarTermList', 'primedVarTermList', new ListTV(new TupleTV(new StringTV(), new TermTV()), tupleTVList)));
        super(fields);
        this.tvName = "InstantiateMetaTV";
    }
    static mkEmptyInstance(): InstantiateMetaTV {
        return new InstantiateMetaTV([[]]);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class ReplaceFormulaTV extends ClassTV {
    constructor(newFormula: string) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('newFormula', 'newFormula', new TermBoolTV(newFormula)));
        super(fields);
        this.tvName = "ReplaceFormulaTV";
    }
    static mkEmptyInstance(): ReplaceFormulaTV {
        return new ReplaceFormulaTV("");
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}


export class GuessGuardTV extends ClassTV {
    constructor(guard: string) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('guard', 'guard', new TermBoolTV(guard)));
        super(fields);
        this.tvName = "GuessGuardTV";
    }
    static mkEmptyInstance(): GuessGuardTV {
        return new GuessGuardTV("");
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}


export class StartGCmdDerivationTV extends ClassTV {
    constructor(guard: string) {
        var fields: FieldTV[] = [];
        super(fields);
        this.tvName = "StartGCmdDerivationTV";
    }
    static mkEmptyInstance(): StartGCmdDerivationTV {
        return new StartGCmdDerivationTV("");
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}


export class MagicTV extends ClassTV {
    constructor(vars: string[], newF: string) {
        var fields: FieldTV[] = [];
        var newVarTVList = _.map(vars, aVar => { new NewVarTV(new StringTV(aVar), new PSTypeTV(undefined)) });
        fields.push(new FieldTV('vars', 'vars', new ListTV(new NewVarTV(new StringTV(""), new PSTypeTV(undefined)), newVarTVList)));
        fields.push(new FieldTV('newF', 'newF', new TermBoolTV(name)));
        super(fields);
        this.tvName = "MagicTV";
    }
    static mkEmptyInstance(): MagicTV {
        return new MagicTV([], "");
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class AssumePreTV extends ClassTV {
    constructor(vars: string[], newF: string) {
        var fields: FieldTV[] = [];
        var newVarTVList = _.map(vars, aVar => { new NewVarTV(new StringTV(aVar), new PSTypeTV(undefined)) });
        fields.push(new FieldTV('freshVariables', 'freshVariables', new ListTV(new NewVarTV(new StringTV(""), new PSTypeTV(undefined)), newVarTVList)));
        fields.push(new FieldTV('assumedPre', 'assumedPre', new TermBoolTV(name)));
        super(fields);
        this.tvName = "AssumePreTV";
    }
    static mkEmptyInstance(): AssumePreTV {
        return new AssumePreTV([], "");
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class StepOutTV extends ClassTV {
    constructor() {
        var fields: FieldTV[] = [];
        super(fields);
        this.tvName = "StepOutTV";
    }
    static mkEmptyInstance(): StepOutTV {
        return new StepOutTV();
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class WhileStrInvSPTV extends ClassTV {
    constructor() {
        var fields: FieldTV[] = [];
        super(fields);
        this.tvName = "WhileStrInvSPTV";
    }
    static mkEmptyInstance(): WhileStrInvSPTV {
        return new WhileStrInvSPTV();
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class CollapseCompositionsTV extends ClassTV {
    constructor() {
        var fields: FieldTV[] = [];
        super(fields);
        this.tvName = "CollapseCompositionsTV";
    }
    static mkEmptyInstance(): CollapseCompositionsTV {
        return new CollapseCompositionsTV();
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }
}

export class AssumeToIfTV extends ClassTV {
    constructor(displayId: number) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('displayId', 'displayId', new IntegerTV(displayId)));
        super(fields);
        this.tvName = "AssumeToIfTV";
    }
    static mkEmptyInstance(): AssumeToIfTV {
        return new AssumeToIfTV(0);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class PropagateAssumeUpTV extends ClassTV {
    constructor(displayId: number) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('displayId', 'displayId', new IntegerTV(displayId)));
        super(fields);
        this.tvName = "PropagateAssumeUpTV";
    }
    static mkEmptyInstance(): PropagateAssumeUpTV {
        return new PropagateAssumeUpTV(0);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }
}

export class PropagateAssertionsDownSPTV extends ClassTV {
    constructor(displayId1: number, displayId2: number) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('displayId1', 'displayId1', new IntegerTV(displayId1)));
        fields.push(new FieldTV('displayId2', 'displayId2', new IntegerTV(displayId2)));
        super(fields);
        this.tvName = "PropagateAssertionsDownSPTV";
    }
    static mkEmptyInstance(): PropagateAssertionsDownSPTV {
        return new PropagateAssertionsDownSPTV(0, 0);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class StrengthenPostSPTV extends ClassTV {
    constructor(displayId: number) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('displayId', 'displayId', new IntegerTV(displayId)));
        super(fields);
        this.tvName = "StrengthenPostSPTV";
    }
    static mkEmptyInstance(): StrengthenPostSPTV {
        return new StrengthenPostSPTV(0);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}    

export class SimplifyAutoTV extends ClassTV {
    constructor() {
        var fields: FieldTV[] = [];
        super(fields);
        this.tvName = "SimplifyAutoTV";
    }
    static mkEmptyInstance(): SimplifyAutoTV {
        return new SimplifyAutoTV();
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class SimplifyTV extends ClassTV {
    constructor() {
        var fields: FieldTV[] = [];
        super(fields);
        this.tvName = "SimplifyTV";
    }
    static mkEmptyInstance(): SimplifyTV {
        return new SimplifyTV();
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class RTVInPostTV extends ClassTV {
    constructor(constant: string, variable: string, initValue: string, bounds: string) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('constant', 'constant', new TermTV(constant)));
        fields.push(new FieldTV('variable', 'variable', new NewVarTV(new StringTV(variable), new PSTypeTV(undefined))));
        //fields.push(new FieldTV('variable', new VarTV(variable)));
        fields.push(new FieldTV('initValue', 'initValue', new TermTV(initValue)));
        fields.push(new FieldTV('bounds', 'bounds', new TermBoolTV(bounds)));

        super(fields);
        this.tvName = "RTVInPostTV";
    }
    static mkEmptyInstance(): RTVInPostTV {
        return new RTVInPostTV("", "", "", "");
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        

}

export class RTVInPost2TV extends ClassTV {
    constructor(displayId: number, variable: string, initValue: string, bounds: string) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('displayId', 'displayId', new IntegerTV(displayId)));
        fields.push(new FieldTV('variable', 'variable', new NewVarTV(new StringTV(variable), new PSTypeTV(undefined))));
        //fields.push(new FieldTV('variable', new VarTV(variable)));
        fields.push(new FieldTV('initValue', 'initValue', new TermTV(initValue)));
        fields.push(new FieldTV('bounds', 'bounds', new TermBoolTV(bounds)));

        super(fields);
        this.tvName = "RTVInPost2TV";
    }
    static mkEmptyInstance(): RTVInPost2TV {
        return new RTVInPost2TV(0, "", "", "");
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class SplitoutBoundVariableTV extends ClassTV {
    constructor(displayId: number, boundVar: string) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('displayId', 'displayId', new FDisplayIdTV(displayId)));
        fields.push(new FieldTV('boundVar', 'boundVar', new NewVarTV(new StringTV(boundVar), new PSTypeTV(undefined))));
        super(fields);
        this.tvName = "SplitoutBoundVariableTV";
    }
    static mkEmptyInstance(): SplitoutBoundVariableTV {
        return new SplitoutBoundVariableTV(0, "");
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

///////////////////////////////////////////////

export class DistributivityTV extends ClassTV {
    constructor(displayId: number) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('displayId', 'displayId', new FDisplayIdTV(displayId)));
        super(fields);
        this.tvName = "DistributivityTV";
    }
    static mkEmptyInstance(): DistributivityTV {
        return new DistributivityTV(0);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}


export class EmptyRangeTV extends ClassTV {
    constructor(displayId: number) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('displayId', 'displayId', new FDisplayIdTV(displayId)));
        super(fields);
        this.tvName = "EmptyRangeTV";
    }
    static mkEmptyInstance(): EmptyRangeTV {
        return new EmptyRangeTV(0);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}


export class OnePointTV extends ClassTV {
    constructor(displayId: number) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('displayId', 'displayId', new FDisplayIdTV(displayId)));
        super(fields);
        this.tvName = "OnePointTV";
    }
    static mkEmptyInstance(): OnePointTV {
        return new OnePointTV(0);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class QDistributivityTV extends ClassTV {
    constructor(displayId: number) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('displayId', 'displayId', new FDisplayIdTV(displayId)));
        super(fields);
        this.tvName = "QDistributivityTV";
    }
    static mkEmptyInstance(): QDistributivityTV {
        return new QDistributivityTV(0);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}


export class RangeSplitTV extends ClassTV {
    constructor(displayId: number) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('displayId', 'displayId', new FDisplayIdTV(displayId)));
        super(fields);
        this.tvName = "RangeSplitTV";
    }
    static mkEmptyInstance(): RangeSplitTV {
        return new RangeSplitTV(0);
    }

    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}


export class ReplaceSubformulaTV extends ClassTV {
    constructor(oldSubFId: number, newSubF: string) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('oldSubFId', 'oldSubFId', new FDisplayIdTV(oldSubFId)));
        fields.push(new FieldTV('newSubF', 'newSubF', new TermBoolTV(newSubF)));
        super(fields);
        this.tvName = "ReplaceSubformulaTV";
    }
    static mkEmptyInstance(): ReplaceSubformulaTV {
        return new ReplaceSubformulaTV(0, "");
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class ReplaceSubTermTV extends ClassTV {
    constructor(subTermId: number, newSubTerm: string) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('subTermId', 'subTermId', new FDisplayIdTV(subTermId)));
        fields.push(new FieldTV('newSubTerm', 'newSubTerm', new TermTV(newSubTerm)));
        super(fields);
        this.tvName = "ReplaceSubTermTV";
    }
    static mkEmptyInstance(): ReplaceSubTermTV {
        return new ReplaceSubTermTV(0, "");
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}


export class StartAsgnDerivationTV extends ClassTV {
    constructor(lhsVars: string[]) {
        var fields: FieldTV[] = [];
        var lhsVarsTVList = _.map(lhsVars, x => { new VarTV(x) });
        fields.push(new FieldTV('lhsVars', 'lhsVars', new ListTV(new VarTV(""), lhsVarsTVList)));
        super(fields);
        this.tvName = "StartAsgnDerivationTV";
    }
    static mkEmptyInstance(): StartAsgnDerivationTV {
        return new StartAsgnDerivationTV([]);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}


export class StepIntoPOTV extends ClassTV {
    constructor() {
        var fields: FieldTV[] = [];

        super(fields);
        this.tvName = "StepIntoPOTV";
    }
    static mkEmptyInstance(): StepIntoPOTV {
        return new StepIntoPOTV();
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}


export class StepIntoProgIdTV extends ClassTV {
    constructor(id: number) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('id', 'id', new IntegerTV(id)));
        super(fields);
        this.tvName = "StepIntoProgIdTV";
    }
    static mkEmptyInstance(): StepIntoProgIdTV {
        return new StepIntoProgIdTV(0);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class StepIntoSubProgTV extends ClassTV {
    constructor(displayId: number) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('displayId', 'displayId', new PDisplayIdTV(displayId)));
        super(fields);
        this.tvName = "StepIntoSubProgTV";
    }
    static mkEmptyInstance(): StepIntoSubProgTV {
        return new StepIntoSubProgTV(0);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class StepIntoSubFormulaTV extends ClassTV {
    constructor(subId: number) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('subId', 'subId', new FDisplayIdTV(subId)));
        super(fields);
        this.tvName = "StepIntoSubFormulaTV";
    }
    static mkEmptyInstance(): StepIntoSubFormulaTV {
        return new StepIntoSubFormulaTV(0);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class StrengthenInvariantTV extends ClassTV {
    constructor(newInvs: string[]) {
        var fields: FieldTV[] = [];
        var newInvsTVList = _.map(newInvs, x => { new TermBoolTV(x) });
        fields.push(new FieldTV('newInvs', 'newInvs', new ListTV(new TermBoolTV(""), newInvsTVList)));
        super(fields);
        this.tvName = "StrengthenInvariantTV";
    }
    static mkEmptyInstance(): StrengthenInvariantTV {
        return new StrengthenInvariantTV([]);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}


export class TradingMoveToTermTV extends ClassTV {
    constructor(displayId: number, termToBeMovedId: number) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('displayId', 'displayId', new FDisplayIdTV(displayId)));
        fields.push(new FieldTV('termToBeMovedId', 'termToBeMovedId', new FDisplayIdTV(termToBeMovedId)));
        super(fields);
        this.tvName = "TradingMoveToTermTV";
    }
    static mkEmptyInstance(): TradingMoveToTermTV {
        return new TradingMoveToTermTV(0, 0);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}


export class UseAssumptionsTV extends ClassTV {
    constructor(subFormulaId: number, newSubF: string) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('subFormulaId', 'subFormulaId', new FDisplayIdTV(subFormulaId)));
        fields.push(new FieldTV('newSubF', 'newSubF', new TermBoolTV(newSubF)));
        super(fields);
        this.tvName = "UseAssumptionsTV";
    }
    static mkEmptyInstance(): UseAssumptionsTV {
        return new UseAssumptionsTV(0, "");
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}


export class Init3TV extends ClassTV {
    constructor(name: string, params: string[], retVar: string, preF: string, postF: string, globalInvs: string[]) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('name', 'name', new StringTV(name)));
        var paramsTVList = _.map(params, x => { new NewVarTV(new StringTV(x), new PSTypeTV(undefined)) });
        fields.push(new FieldTV('params', 'params', new ListTV(new NewVarTV(new StringTV(""), new PSTypeTV(undefined)), paramsTVList)));
        fields.push(new FieldTV('retVar', 'retVar', new NewVarTV(new StringTV(retVar), new PSTypeTV(undefined))));
        fields.push(new FieldTV('preF', 'preF', new TermBoolTV(preF)));
        fields.push(new FieldTV('postF', 'postF', new TermBoolTV(postF)));
        var globalInvsTVList = _.map(globalInvs, x => { new TermBoolTV(x) });
        fields.push(new FieldTV('globalInvs', 'globalInvs', new ListTV(new TermBoolTV(""), globalInvsTVList)));
        super(fields);
        this.tvName = "Init3TV";
    }
    static mkEmptyInstance(): Init3TV {
        return new Init3TV("", [], "", "", "", []);
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

export class InsertVariableTV extends ClassTV {
    constructor(aVar: string, initVal: string) {
        var fields: FieldTV[] = [];
        fields.push(new FieldTV('aVar', 'aVar', new NewVarTV(new StringTV(aVar), new PSTypeTV(undefined))));
        fields.push(new FieldTV('initVal', 'initVal', new TermTV(initVal)));
        super(fields);
        this.tvName = "InsertVariableTV";
    }
    static mkEmptyInstance(): InsertVariableTV {
        return new InsertVariableTV("", "");
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

/////////////////////////////////////////////////////////////////////////////////////
//TODO: rename as TacticSelTV
export class TacticTV extends AbstractTV {
    constructor() {
        var subTVNames = ["StepIntoUnknownProgIdxTV",//TODO: Why TV?
            "RTVInPost", "DeleteConjunct", "StepOut", "SimplifyAuto", "Simplify", "IntroAssignment", "IntroAssignmentEnd",
            "Init4", "InstantiateMeta", "ReplaceFormula", "GuessGuard", 'Distributivity',
            'EmptyRange', 'OnePoint', 'QDistributivity', 'RangeSplit', 'ReplaceSubformula', 'ReplaceSubTerm',
            'StepIntoPO','StepIntoSubProg','StepIntoSubFormula','StrengthenInvariant',
            'TradingMoveToTerm', 'InsertVariable', 'AssumePre',
            'RTVInPost2', 'SplitoutBoundVariable', 'StepIntoBA', 'StepIntoIFBA', 'IntroSwap', 'IntroIf', 'WhileStrInvSP',
            'AssumeToIf', 'PropagateAssumeUp', 'PropagateAssertionsDownSP', 'StrengthenPostSP', 'CollapseCompositions'].sort();
            // 'StartIfDerivation'
            // 'StartAsgnDerivation'
            // 'StartGCmdDerivation'                

        super(subTVNames);
        this.tvName = "TacticTV";
    }

    getView(): typeViews.TacticView {
        var tacticView = new typeViews.TacticView(this);
        tacticView.concreteView = typeReg.createView(this.concreteTV);
        return tacticView;
    }

    static mkEmptyInstance(): TacticTV {
        return new TacticTV();
    }
    fill(obj: any) {
        super.fill(obj);
        return this;
    }        
}

