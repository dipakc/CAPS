var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "TypeViews", "TypeRegistry"], function(require, exports, typeViews, typeReg) {
    typeViews;
    typeReg;

    function objToTV(json) {
        var tvClass = typeReg.getClass(json.tvName);
        var r = tvClass.mkEmptyInstance();
        r.fill(json);
        return r;
    }
    exports.objToTV = objToTV;

    // TypedValue
    var TV = (function () {
        function TV() {
            this.tvName = "TV";
        }
        TV.prototype.clone = function () {
            throw new Error("Abstract method clone not implemented in subclass of TV");
        };
        TV.prototype.copyTo = function (arg) {
            arg.tvName = this.tvName;
        };

        TV.prototype.fill = function (obj) {
            this.tvName = obj.tvName;
            return this;
        };

        TV.mkEmptyInstance = function () {
            return new TV();
        };
        return TV;
    })();
    exports.TV = TV;

    var AbstractTV = (function (_super) {
        __extends(AbstractTV, _super);
        function AbstractTV(subTVs) {
            _super.call(this);
            this.tvName = "AbstractTV";
            this.subTVNames = subTVs;
            this.concreteTV = undefined;
        }
        AbstractTV.prototype.clone = function () {
            var retVal = new AbstractTV(undefined);
            this.copyTo(retVal);
            return retVal;
        };
        AbstractTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
            arg.subTVNames = this.subTVNames;
            arg.concreteTV = this.concreteTV.clone();
        };
        AbstractTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            this.subTVNames = obj.subTVNames;
            this.concreteTV = exports.objToTV(obj.concreteTV);
            return this;
        };

        AbstractTV.mkEmptyInstance = function () {
            return new AbstractTV([]);
        };
        return AbstractTV;
    })(TV);
    exports.AbstractTV = AbstractTV;

    var NewVarTV = (function (_super) {
        __extends(NewVarTV, _super);
        function NewVarTV(varNameTV, varTypeTV) {
            _super.call(this);
            this.tvName = "NewVarTV";
            this.varNameTV = varNameTV;
            this.varTypeTV = varTypeTV;
        }
        NewVarTV.prototype.clone = function () {
            var retVal = new NewVarTV(undefined, undefined);
            this.copyTo(retVal);
            return retVal;
        };
        NewVarTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
            arg.varNameTV = this.varNameTV.clone();
            arg.varTypeTV = this.varTypeTV.clone();
        };
        NewVarTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            this.varNameTV = exports.objToTV(obj.varNameTV);
            this.varTypeTV = exports.objToTV(obj.varTypeTV);
            return this;
        };

        NewVarTV.mkEmptyInstance = function () {
            return new NewVarTV(undefined, undefined);
        };
        return NewVarTV;
    })(TV);
    exports.NewVarTV = NewVarTV;

    var EnumTV = (function (_super) {
        __extends(EnumTV, _super);
        function EnumTV(elements, selectedElem) {
            _super.call(this);
            this.tvName = "EnumTV";
            this.elements = elements;
            this.selectedElem = selectedElem;
        }
        EnumTV.prototype.clone = function () {
            var retVal = new EnumTV(undefined, undefined);
            this.copyTo(retVal);
            return retVal;
        };
        EnumTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
            arg.elements = this.elements;
            arg.selectedElem = this.selectedElem;
        };

        EnumTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            this.elements = obj.elements;
            this.selectedElem = obj.selectedElem;
            return this;
        };

        EnumTV.mkEmptyInstance = function () {
            return new EnumTV([], "");
        };
        return EnumTV;
    })(TV);
    exports.EnumTV = EnumTV;

    var PSTypeTV = (function (_super) {
        __extends(PSTypeTV, _super);
        function PSTypeTV(selectedElem) {
            _super.call(this, ['Int', 'Bool', 'ArrayInt', 'ArrayBool'], selectedElem);
            this.tvName = "PSTypeTV";
        }
        PSTypeTV.prototype.clone = function () {
            var retVal = new PSTypeTV(undefined);
            this.copyTo(retVal);
            return retVal;
        };
        PSTypeTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
        };

        PSTypeTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };

        PSTypeTV.mkEmptyInstance = function () {
            return new PSTypeTV("");
        };
        return PSTypeTV;
    })(EnumTV);
    exports.PSTypeTV = PSTypeTV;

    var ClassTV = (function (_super) {
        __extends(ClassTV, _super);
        function ClassTV(fields) {
            _super.call(this);
            this.tvName = "ClassTV";
            this.fields = fields;
        }
        ClassTV.prototype.clone = function () {
            var retVal = new ClassTV(undefined);
            this.copyTo(retVal);
            return retVal;
        };
        ClassTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
            arg.fields = _.map(this.fields, function (field) {
                return field.clone();
            });
        };
        ClassTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            this.fields = _.map(obj.fields, function (f) {
                return exports.objToTV(f);
            });
            return this;
        };

        ClassTV.mkEmptyInstance = function () {
            return new ClassTV([]);
        };
        return ClassTV;
    })(TV);
    exports.ClassTV = ClassTV;

    var FieldTV = (function (_super) {
        __extends(FieldTV, _super);
        function FieldTV(fname, displayName, ftv) {
            _super.call(this);
            this.tvName = "FieldTV";
            this.fname = fname;
            this.displayName = displayName;
            this.ftv = ftv;
        }
        FieldTV.prototype.clone = function () {
            var retVal = new FieldTV(undefined, undefined, undefined);
            this.copyTo(retVal);
            return retVal;
        };
        FieldTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
            arg.fname = this.fname;
            arg.displayName = this.displayName;
            arg.ftv = this.ftv.clone();
        };
        FieldTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            this.fname = obj.fname;
            this.displayName = obj.displayName;
            this.ftv = exports.objToTV(obj.ftv);
            return this;
        };

        FieldTV.mkEmptyInstance = function () {
            return new FieldTV("", "", undefined);
        };
        return FieldTV;
    })(TV);
    exports.FieldTV = FieldTV;

    var PrimitiveTV = (function (_super) {
        __extends(PrimitiveTV, _super);
        function PrimitiveTV(value) {
            _super.call(this);
            this.tvName = "PrimitiveTV";
            this.value = value;
        }
        PrimitiveTV.prototype.clone = function () {
            var retVal = new PrimitiveTV(undefined);
            this.copyTo(retVal);
            return retVal;
        };
        PrimitiveTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
            arg.value = this.value;
        };

        PrimitiveTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            this.value = obj.value;
            return this;
        };

        PrimitiveTV.mkEmptyInstance = function () {
            return new PrimitiveTV();
        };
        return PrimitiveTV;
    })(TV);
    exports.PrimitiveTV = PrimitiveTV;

    //User TVs
    var IntegerTV = (function (_super) {
        __extends(IntegerTV, _super);
        function IntegerTV(value) {
            _super.call(this, value);
            this.tvName = "IntegerTV";
            this.value = value;
        }
        IntegerTV.prototype.clone = function () {
            var retVal = new IntegerTV(undefined);
            this.copyTo(retVal);
            return retVal;
        };
        IntegerTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
            arg.value = this.value;
        };
        IntegerTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            this.value = obj.value;
            return this;
        };

        IntegerTV.mkEmptyInstance = function () {
            return new IntegerTV();
        };
        return IntegerTV;
    })(PrimitiveTV);
    exports.IntegerTV = IntegerTV;

    var FDisplayIdTV = (function (_super) {
        __extends(FDisplayIdTV, _super);
        function FDisplayIdTV(value) {
            _super.call(this, value);
            this.tvName = "FDisplayIdTV";
            this.value = value;
        }
        FDisplayIdTV.prototype.clone = function () {
            var retVal = new FDisplayIdTV(undefined);
            this.copyTo(retVal);
            return retVal;
        };
        FDisplayIdTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
            arg.value = this.value;
        };
        FDisplayIdTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            this.value = obj.value;
            return this;
        };

        FDisplayIdTV.mkEmptyInstance = function () {
            return new FDisplayIdTV();
        };
        return FDisplayIdTV;
    })(IntegerTV);
    exports.FDisplayIdTV = FDisplayIdTV;

    var PDisplayIdTV = (function (_super) {
        __extends(PDisplayIdTV, _super);
        function PDisplayIdTV(value) {
            _super.call(this, value);
            this.tvName = "PDisplayIdTV";
            this.value = value;
        }
        PDisplayIdTV.prototype.clone = function () {
            var retVal = new PDisplayIdTV(undefined);
            this.copyTo(retVal);
            return retVal;
        };
        PDisplayIdTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
            arg.value = this.value;
        };
        PDisplayIdTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            this.value = obj.value;
            return this;
        };

        PDisplayIdTV.mkEmptyInstance = function () {
            return new PDisplayIdTV();
        };
        return PDisplayIdTV;
    })(IntegerTV);
    exports.PDisplayIdTV = PDisplayIdTV;

    var StringTV = (function (_super) {
        __extends(StringTV, _super);
        function StringTV(value) {
            _super.call(this, value);
            this.tvName = "StringTV";
            this.value = value;
        }
        StringTV.prototype.clone = function () {
            var retVal = new StringTV(undefined);
            this.copyTo(retVal);
            return retVal;
        };
        StringTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
            arg.value = this.value;
        };
        StringTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            this.value = obj.value;
            return this;
        };

        StringTV.mkEmptyInstance = function () {
            return new StringTV();
        };
        return StringTV;
    })(PrimitiveTV);
    exports.StringTV = StringTV;

    var TermTV = (function (_super) {
        __extends(TermTV, _super);
        function TermTV(value) {
            _super.call(this, value);
            this.tvName = "TermTV";
            this.value = value;
        }
        TermTV.prototype.clone = function () {
            var retVal = new TermTV(undefined);
            this.copyTo(retVal);
            return retVal;
        };
        TermTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
            arg.value = this.value;
        };
        TermTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            this.value = obj.value;
            return this;
        };

        TermTV.mkEmptyInstance = function () {
            return new TermTV();
        };
        return TermTV;
    })(PrimitiveTV);
    exports.TermTV = TermTV;

    var VarTV = (function (_super) {
        __extends(VarTV, _super);
        function VarTV(value) {
            _super.call(this, value);
            this.tvName = "VarTV";
            this.value = value;
        }
        VarTV.prototype.clone = function () {
            var retVal = new VarTV(undefined);
            this.copyTo(retVal);
            return retVal;
        };
        VarTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
            arg.value = this.value;
        };
        VarTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            this.value = obj.value;
            return this;
        };

        VarTV.mkEmptyInstance = function () {
            return new VarTV();
        };
        return VarTV;
    })(PrimitiveTV);
    exports.VarTV = VarTV;

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
    var FOLFormulaTV = (function (_super) {
        __extends(FOLFormulaTV, _super);
        function FOLFormulaTV(value) {
            _super.call(this, value);
            this.tvName = "FOLFormulaTV";
            this.value = value;
        }
        FOLFormulaTV.prototype.clone = function () {
            var retVal = new FOLFormulaTV(undefined);
            this.copyTo(retVal);
            return retVal;
        };
        FOLFormulaTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
            arg.value = this.value;
        };
        FOLFormulaTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            this.value = obj.value;
            return this;
        };

        FOLFormulaTV.mkEmptyInstance = function () {
            return new FOLFormulaTV();
        };
        return FOLFormulaTV;
    })(PrimitiveTV);
    exports.FOLFormulaTV = FOLFormulaTV;

    var TermBoolTV = (function (_super) {
        __extends(TermBoolTV, _super);
        function TermBoolTV(value) {
            _super.call(this, value);
            this.tvName = "TermBoolTV";
            this.value = value;
        }
        TermBoolTV.prototype.clone = function () {
            var retVal = new TermBoolTV(undefined);
            this.copyTo(retVal);
            return retVal;
        };
        TermBoolTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
            arg.value = this.value;
        };

        TermBoolTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            this.value = obj.value;
            return this;
        };

        TermBoolTV.mkEmptyInstance = function () {
            return new TermBoolTV();
        };
        return TermBoolTV;
    })(PrimitiveTV);
    exports.TermBoolTV = TermBoolTV;

    var ListTV = (function (_super) {
        __extends(ListTV, _super);
        //TODO: make the val argument optional.
        ////ListTV constructor can not be optional
        ////An empty list must be able to render itself.
        function ListTV(itv, items) {
            _super.call(this);
            this.tvName = "ListTV";
            this.metaTV = itv;
            this.items = items;
        }
        ListTV.prototype.clone = function () {
            var retVal = new ListTV(undefined, undefined);
            this.copyTo(retVal);
            return retVal;
        };
        ListTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
            arg.metaTV = this.metaTV.clone();
            arg.items = _.map(this.items, function (item) {
                return item.clone();
            });
        };
        ListTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            this.metaTV = exports.objToTV(obj.metaTV);
            this.items = (_.map(obj.items, exports.objToTV));
            return this;
        };

        ListTV.mkEmptyInstance = function () {
            return new ListTV(undefined, []);
        };
        return ListTV;
    })(TV);
    exports.ListTV = ListTV;

    var TupleTV = (function (_super) {
        __extends(TupleTV, _super);
        function TupleTV(val1, val2) {
            _super.call(this);
            this.tvName = "TupleTV";
            this.item1 = val1;
            this.item2 = val2;
        }
        TupleTV.prototype.clone = function () {
            var retVal = new TupleTV(undefined, undefined);
            this.copyTo(retVal);
            return retVal;
        };
        TupleTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
            arg.item1 = this.item1.clone();
            arg.item2 = this.item2.clone();
        };
        TupleTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            this.item1 = exports.objToTV(obj.item1);
            this.item2 = exports.objToTV(obj.item2);
            return this;
        };

        TupleTV.mkEmptyInstance = function () {
            return new TupleTV(undefined, undefined);
        };
        return TupleTV;
    })(TV);
    exports.TupleTV = TupleTV;

    var StepIntoUnknownProgIdxTV = (function (_super) {
        __extends(StepIntoUnknownProgIdxTV, _super);
        function StepIntoUnknownProgIdxTV(idx) {
            var idxField = new FieldTV('idx', 'idx', new IntegerTV(idx));
            var fields = [idxField];
            _super.call(this, fields);
            this.tvName = "StepIntoUnknownProgIdxTV";
            this.idx = idx;
        }
        StepIntoUnknownProgIdxTV.prototype.clone = function () {
            var retVal = new StepIntoUnknownProgIdxTV();
            this.copyTo(retVal);
            return retVal;
        };
        StepIntoUnknownProgIdxTV.prototype.copyTo = function (arg) {
            _super.prototype.copyTo.call(this, arg);
            arg.idx = this.idx;
        };
        StepIntoUnknownProgIdxTV.mkEmptyInstance = function () {
            return new StepIntoUnknownProgIdxTV();
        };
        StepIntoUnknownProgIdxTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            this.idx = obj.idx;
            return this;
        };
        return StepIntoUnknownProgIdxTV;
    })(ClassTV);
    exports.StepIntoUnknownProgIdxTV = StepIntoUnknownProgIdxTV;

    //InitTactic(name: String, params: List[Var], retVar: Var, preF: FOLFormula, postF: FOLFormula)
    var InitTV = (function (_super) {
        __extends(InitTV, _super);
        function InitTV(name, params, retVar, preF, postF) {
            var fields = [];
            fields.push(new FieldTV('name', 'name', new StringTV(name)));
            var newVarTVList = _.map(params, function (param) {
                new NewVarTV(new StringTV(param), new PSTypeTV(undefined));
            });
            fields.push(new FieldTV('params', 'params', new ListTV(new NewVarTV(new StringTV(""), new PSTypeTV(undefined)), newVarTVList)));
            fields.push(new FieldTV('retVar', 'retVar', new NewVarTV(new StringTV(name), new PSTypeTV(undefined))));
            fields.push(new FieldTV('preF', 'preF', new TermBoolTV(name)));
            fields.push(new FieldTV('postF', 'postF', new TermBoolTV(name)));
            _super.call(this, fields);
            this.tvName = "InitTV";
        }
        InitTV.mkEmptyInstance = function () {
            return new InitTV("", [], "", "", "");
        };
        InitTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return InitTV;
    })(ClassTV);
    exports.InitTV = InitTV;

    var RetValTV = (function (_super) {
        __extends(RetValTV, _super);
        function RetValTV(initTerm) {
            var initTermF = new FieldTV('initTerm', 'initTerm', new TermTV(initTerm));
            var fields = [initTermF];
            _super.call(this, fields);
            this.tvName = "RetValTV";
        }
        RetValTV.mkEmptyInstance = function () {
            return new RetValTV("");
        };

        RetValTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return RetValTV;
    })(ClassTV);
    exports.RetValTV = RetValTV;

    var DeleteConjunctTV = (function (_super) {
        __extends(DeleteConjunctTV, _super);
        //conjunct: FOLFormula, variant: Term
        function DeleteConjunctTV(conjunct, variant) {
            var fields = [];
            fields.push(new FieldTV('conjunct', 'conjunct', new TermBoolTV(conjunct)));
            fields.push(new FieldTV('variant', 'variant', new TermTV(variant)));
            _super.call(this, fields);
            this.tvName = "DeleteConjunctTV";
        }
        DeleteConjunctTV.mkEmptyInstance = function () {
            return new DeleteConjunctTV("", "");
        };
        DeleteConjunctTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return DeleteConjunctTV;
    })(ClassTV);
    exports.DeleteConjunctTV = DeleteConjunctTV;

    //IntroAssignmentTactic(lhsRhsTuples:List[(Var, Term)]) extends FunTactic {
    var IntroAssignmentTV = (function (_super) {
        __extends(IntroAssignmentTV, _super);
        function IntroAssignmentTV(lhsRhsTuples) {
            var fields = [];
            var tupleTVList = _.map(lhsRhsTuples, function (tuple) {
                var tv1 = new TermTV(tuple[0]);
                var tv2 = new TermTV(tuple[1]);
                new TupleTV(tv1, tv2);
            });

            fields.push(new FieldTV('lhsRhsTuples', 'lhsRhsTuples', new ListTV(new TupleTV(new TermTV(), new TermTV()), tupleTVList)));
            _super.call(this, fields);
            this.tvName = "IntroAssignmentTV";
        }
        IntroAssignmentTV.mkEmptyInstance = function () {
            return new IntroAssignmentTV([[]]);
        };
        IntroAssignmentTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return IntroAssignmentTV;
    })(ClassTV);
    exports.IntroAssignmentTV = IntroAssignmentTV;

    var IntroAssignmentEndTV = (function (_super) {
        __extends(IntroAssignmentEndTV, _super);
        function IntroAssignmentEndTV(lhsRhsTuples) {
            var fields = [];
            var tupleTVList = _.map(lhsRhsTuples, function (tuple) {
                var tv1 = new TermTV(tuple[0]);
                var tv2 = new TermTV(tuple[1]);
                new TupleTV(tv1, tv2);
            });

            fields.push(new FieldTV('lhsRhsTuples', 'lhsRhsTuples', new ListTV(new TupleTV(new TermTV(), new TermTV()), tupleTVList)));
            _super.call(this, fields);
            this.tvName = "IntroAssignmentEndTV";
        }
        IntroAssignmentEndTV.mkEmptyInstance = function () {
            return new IntroAssignmentEndTV([[]]);
        };
        IntroAssignmentEndTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return IntroAssignmentEndTV;
    })(ClassTV);
    exports.IntroAssignmentEndTV = IntroAssignmentEndTV;

    //Init4Tactic(name: String, mutableVars: List[Var], immutableVars: List[Var],
    //	preF: TermBool, postF: TermBool, globalInvs: List[TermBool])
    var Init4TV = (function (_super) {
        __extends(Init4TV, _super);
        function Init4TV(name, immutableVars, mutableVars, globalInvs, preF, postF) {
            var fields = [];
            fields.push(new FieldTV('name', 'Derivation Name', new StringTV(name)));
            var newVarImmutableTVList = _.map(immutableVars, function (immutableVar) {
                new NewVarTV(new StringTV(immutableVar), new PSTypeTV(undefined));
            });
            var newVarMutableTVList = _.map(mutableVars, function (mutableVar) {
                new NewVarTV(new StringTV(mutableVar), new PSTypeTV(undefined));
            });
            fields.push(new FieldTV('immutableVars', 'Constants', new ListTV(new NewVarTV(new StringTV(""), new PSTypeTV(undefined)), newVarImmutableTVList)));
            fields.push(new FieldTV('mutableVars', 'Variables', new ListTV(new NewVarTV(new StringTV(""), new PSTypeTV(undefined)), newVarMutableTVList)));
            var globalInvsTVList = _.map(globalInvs, function (x) {
                new TermBoolTV(x);
            });
            fields.push(new FieldTV('globalInvs', 'Global Invariants', new ListTV(new TermBoolTV(""), globalInvsTVList)));
            fields.push(new FieldTV('preF', 'Precondition', new TermBoolTV(preF)));
            fields.push(new FieldTV('postF', 'Postcondition', new TermBoolTV(postF)));
            _super.call(this, fields);
            this.tvName = "Init4TV";
        }
        Init4TV.mkEmptyInstance = function () {
            return new Init4TV("", [], [], [], "", "");
        };
        Init4TV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return Init4TV;
    })(ClassTV);
    exports.Init4TV = Init4TV;

    var StartIfDerivationTV = (function (_super) {
        __extends(StartIfDerivationTV, _super);
        function StartIfDerivationTV(lhsVars) {
            var fields = [];
            var varTVList = _.map(lhsVars, function (lVar) {
                new VarTV(lVar);
            });
            fields.push(new FieldTV('lhsVars', 'lhsVars', new ListTV(new VarTV(""), varTVList)));
            _super.call(this, fields);
            this.tvName = "StartIfDerivationTV";
        }
        StartIfDerivationTV.mkEmptyInstance = function () {
            return new StartIfDerivationTV([]);
        };
        StartIfDerivationTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return StartIfDerivationTV;
    })(ClassTV);
    exports.StartIfDerivationTV = StartIfDerivationTV;

    var StepIntoBATV = (function (_super) {
        __extends(StepIntoBATV, _super);
        function StepIntoBATV(lhsVars) {
            var fields = [];
            var varTVList = _.map(lhsVars, function (lVar) {
                new VarTV(lVar);
            });
            fields.push(new FieldTV('lhsVars', 'lhsVars', new ListTV(new VarTV(""), varTVList)));
            _super.call(this, fields);
            this.tvName = "StepIntoBATV";
        }
        StepIntoBATV.mkEmptyInstance = function () {
            return new StepIntoBATV([]);
        };
        StepIntoBATV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return StepIntoBATV;
    })(ClassTV);
    exports.StepIntoBATV = StepIntoBATV;

    var StepIntoIFBATV = (function (_super) {
        __extends(StepIntoIFBATV, _super);
        function StepIntoIFBATV(lhsVars) {
            var fields = [];
            var varTVList = _.map(lhsVars, function (lVar) {
                new VarTV(lVar);
            });
            fields.push(new FieldTV('lhsVars', 'lhsVars', new ListTV(new VarTV(""), varTVList)));
            _super.call(this, fields);
            this.tvName = "StepIntoIFBATV";
        }
        StepIntoIFBATV.mkEmptyInstance = function () {
            return new StepIntoIFBATV([]);
        };
        StepIntoIFBATV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return StepIntoIFBATV;
    })(ClassTV);
    exports.StepIntoIFBATV = StepIntoIFBATV;

    var IntroSwapTV = (function (_super) {
        __extends(IntroSwapTV, _super);
        function IntroSwapTV(array, index1, index2) {
            var fields = [];
            fields.push(new FieldTV('array', 'array', new VarTV(array)));
            fields.push(new FieldTV('index1', 'index1', new TermTV(index1)));
            fields.push(new FieldTV('index2', 'index2', new TermTV(index2)));
            _super.call(this, fields);
            this.tvName = "IntroSwapTV";
        }
        IntroSwapTV.mkEmptyInstance = function () {
            return new IntroSwapTV("", "", "");
        };
        IntroSwapTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return IntroSwapTV;
    })(ClassTV);
    exports.IntroSwapTV = IntroSwapTV;

    var IntroIfTV = (function (_super) {
        __extends(IntroIfTV, _super);
        function IntroIfTV(guards) {
            var fields = [];
            var guardsTVList = _.map(guards, function (x) {
                new TermBoolTV(x);
            });
            fields.push(new FieldTV('guards', 'guards', new ListTV(new TermBoolTV(""), guardsTVList)));
            _super.call(this, fields);
            this.tvName = "IntroIfTV";
        }
        IntroIfTV.mkEmptyInstance = function () {
            return new IntroIfTV([]);
        };
        IntroIfTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return IntroIfTV;
    })(ClassTV);
    exports.IntroIfTV = IntroIfTV;

    var InstantiateMetaTV = (function (_super) {
        __extends(InstantiateMetaTV, _super);
        function InstantiateMetaTV(primedVarTermList) {
            var fields = [];
            var tupleTVList = _.map(primedVarTermList, function (tuple) {
                var tv1 = new StringTV(tuple[0]);
                var tv2 = new TermTV(tuple[1]);
                new TupleTV(tv1, tv2);
            });

            fields.push(new FieldTV('primedVarTermList', 'primedVarTermList', new ListTV(new TupleTV(new StringTV(), new TermTV()), tupleTVList)));
            _super.call(this, fields);
            this.tvName = "InstantiateMetaTV";
        }
        InstantiateMetaTV.mkEmptyInstance = function () {
            return new InstantiateMetaTV([[]]);
        };
        InstantiateMetaTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return InstantiateMetaTV;
    })(ClassTV);
    exports.InstantiateMetaTV = InstantiateMetaTV;

    var ReplaceFormulaTV = (function (_super) {
        __extends(ReplaceFormulaTV, _super);
        function ReplaceFormulaTV(newFormula) {
            var fields = [];
            fields.push(new FieldTV('newFormula', 'newFormula', new TermBoolTV(newFormula)));
            _super.call(this, fields);
            this.tvName = "ReplaceFormulaTV";
        }
        ReplaceFormulaTV.mkEmptyInstance = function () {
            return new ReplaceFormulaTV("");
        };
        ReplaceFormulaTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return ReplaceFormulaTV;
    })(ClassTV);
    exports.ReplaceFormulaTV = ReplaceFormulaTV;

    var GuessGuardTV = (function (_super) {
        __extends(GuessGuardTV, _super);
        function GuessGuardTV(guard) {
            var fields = [];
            fields.push(new FieldTV('guard', 'guard', new TermBoolTV(guard)));
            _super.call(this, fields);
            this.tvName = "GuessGuardTV";
        }
        GuessGuardTV.mkEmptyInstance = function () {
            return new GuessGuardTV("");
        };
        GuessGuardTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return GuessGuardTV;
    })(ClassTV);
    exports.GuessGuardTV = GuessGuardTV;

    var StartGCmdDerivationTV = (function (_super) {
        __extends(StartGCmdDerivationTV, _super);
        function StartGCmdDerivationTV(guard) {
            var fields = [];
            _super.call(this, fields);
            this.tvName = "StartGCmdDerivationTV";
        }
        StartGCmdDerivationTV.mkEmptyInstance = function () {
            return new StartGCmdDerivationTV("");
        };
        StartGCmdDerivationTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return StartGCmdDerivationTV;
    })(ClassTV);
    exports.StartGCmdDerivationTV = StartGCmdDerivationTV;

    var MagicTV = (function (_super) {
        __extends(MagicTV, _super);
        function MagicTV(vars, newF) {
            var fields = [];
            var newVarTVList = _.map(vars, function (aVar) {
                new NewVarTV(new StringTV(aVar), new PSTypeTV(undefined));
            });
            fields.push(new FieldTV('vars', 'vars', new ListTV(new NewVarTV(new StringTV(""), new PSTypeTV(undefined)), newVarTVList)));
            fields.push(new FieldTV('newF', 'newF', new TermBoolTV(name)));
            _super.call(this, fields);
            this.tvName = "MagicTV";
        }
        MagicTV.mkEmptyInstance = function () {
            return new MagicTV([], "");
        };
        MagicTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return MagicTV;
    })(ClassTV);
    exports.MagicTV = MagicTV;

    var AssumePreTV = (function (_super) {
        __extends(AssumePreTV, _super);
        function AssumePreTV(vars, newF) {
            var fields = [];
            var newVarTVList = _.map(vars, function (aVar) {
                new NewVarTV(new StringTV(aVar), new PSTypeTV(undefined));
            });
            fields.push(new FieldTV('freshVariables', 'freshVariables', new ListTV(new NewVarTV(new StringTV(""), new PSTypeTV(undefined)), newVarTVList)));
            fields.push(new FieldTV('assumedPre', 'assumedPre', new TermBoolTV(name)));
            _super.call(this, fields);
            this.tvName = "AssumePreTV";
        }
        AssumePreTV.mkEmptyInstance = function () {
            return new AssumePreTV([], "");
        };
        AssumePreTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return AssumePreTV;
    })(ClassTV);
    exports.AssumePreTV = AssumePreTV;

    var StepOutTV = (function (_super) {
        __extends(StepOutTV, _super);
        function StepOutTV() {
            var fields = [];
            _super.call(this, fields);
            this.tvName = "StepOutTV";
        }
        StepOutTV.mkEmptyInstance = function () {
            return new StepOutTV();
        };
        StepOutTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return StepOutTV;
    })(ClassTV);
    exports.StepOutTV = StepOutTV;

    var WhileStrInvSPTV = (function (_super) {
        __extends(WhileStrInvSPTV, _super);
        function WhileStrInvSPTV() {
            var fields = [];
            _super.call(this, fields);
            this.tvName = "WhileStrInvSPTV";
        }
        WhileStrInvSPTV.mkEmptyInstance = function () {
            return new WhileStrInvSPTV();
        };
        WhileStrInvSPTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return WhileStrInvSPTV;
    })(ClassTV);
    exports.WhileStrInvSPTV = WhileStrInvSPTV;

    var CollapseCompositionsTV = (function (_super) {
        __extends(CollapseCompositionsTV, _super);
        function CollapseCompositionsTV() {
            var fields = [];
            _super.call(this, fields);
            this.tvName = "CollapseCompositionsTV";
        }
        CollapseCompositionsTV.mkEmptyInstance = function () {
            return new CollapseCompositionsTV();
        };
        CollapseCompositionsTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return CollapseCompositionsTV;
    })(ClassTV);
    exports.CollapseCompositionsTV = CollapseCompositionsTV;

    var AssumeToIfTV = (function (_super) {
        __extends(AssumeToIfTV, _super);
        function AssumeToIfTV(displayId) {
            var fields = [];
            fields.push(new FieldTV('displayId', 'displayId', new IntegerTV(displayId)));
            _super.call(this, fields);
            this.tvName = "AssumeToIfTV";
        }
        AssumeToIfTV.mkEmptyInstance = function () {
            return new AssumeToIfTV(0);
        };
        AssumeToIfTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return AssumeToIfTV;
    })(ClassTV);
    exports.AssumeToIfTV = AssumeToIfTV;

    var PropagateAssumeUpTV = (function (_super) {
        __extends(PropagateAssumeUpTV, _super);
        function PropagateAssumeUpTV(displayId) {
            var fields = [];
            fields.push(new FieldTV('displayId', 'displayId', new IntegerTV(displayId)));
            _super.call(this, fields);
            this.tvName = "PropagateAssumeUpTV";
        }
        PropagateAssumeUpTV.mkEmptyInstance = function () {
            return new PropagateAssumeUpTV(0);
        };
        PropagateAssumeUpTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return PropagateAssumeUpTV;
    })(ClassTV);
    exports.PropagateAssumeUpTV = PropagateAssumeUpTV;

    var PropagateAssertionsDownSPTV = (function (_super) {
        __extends(PropagateAssertionsDownSPTV, _super);
        function PropagateAssertionsDownSPTV(displayId1, displayId2) {
            var fields = [];
            fields.push(new FieldTV('displayId1', 'displayId1', new IntegerTV(displayId1)));
            fields.push(new FieldTV('displayId2', 'displayId2', new IntegerTV(displayId2)));
            _super.call(this, fields);
            this.tvName = "PropagateAssertionsDownSPTV";
        }
        PropagateAssertionsDownSPTV.mkEmptyInstance = function () {
            return new PropagateAssertionsDownSPTV(0, 0);
        };
        PropagateAssertionsDownSPTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return PropagateAssertionsDownSPTV;
    })(ClassTV);
    exports.PropagateAssertionsDownSPTV = PropagateAssertionsDownSPTV;

    var StrengthenPostSPTV = (function (_super) {
        __extends(StrengthenPostSPTV, _super);
        function StrengthenPostSPTV(displayId) {
            var fields = [];
            fields.push(new FieldTV('displayId', 'displayId', new IntegerTV(displayId)));
            _super.call(this, fields);
            this.tvName = "StrengthenPostSPTV";
        }
        StrengthenPostSPTV.mkEmptyInstance = function () {
            return new StrengthenPostSPTV(0);
        };
        StrengthenPostSPTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return StrengthenPostSPTV;
    })(ClassTV);
    exports.StrengthenPostSPTV = StrengthenPostSPTV;

    var SimplifyAutoTV = (function (_super) {
        __extends(SimplifyAutoTV, _super);
        function SimplifyAutoTV() {
            var fields = [];
            _super.call(this, fields);
            this.tvName = "SimplifyAutoTV";
        }
        SimplifyAutoTV.mkEmptyInstance = function () {
            return new SimplifyAutoTV();
        };
        SimplifyAutoTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return SimplifyAutoTV;
    })(ClassTV);
    exports.SimplifyAutoTV = SimplifyAutoTV;

    var SimplifyTV = (function (_super) {
        __extends(SimplifyTV, _super);
        function SimplifyTV() {
            var fields = [];
            _super.call(this, fields);
            this.tvName = "SimplifyTV";
        }
        SimplifyTV.mkEmptyInstance = function () {
            return new SimplifyTV();
        };
        SimplifyTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return SimplifyTV;
    })(ClassTV);
    exports.SimplifyTV = SimplifyTV;

    var RTVInPostTV = (function (_super) {
        __extends(RTVInPostTV, _super);
        function RTVInPostTV(constant, variable, initValue, bounds) {
            var fields = [];
            fields.push(new FieldTV('constant', 'constant', new TermTV(constant)));
            fields.push(new FieldTV('variable', 'variable', new NewVarTV(new StringTV(variable), new PSTypeTV(undefined))));

            //fields.push(new FieldTV('variable', new VarTV(variable)));
            fields.push(new FieldTV('initValue', 'initValue', new TermTV(initValue)));
            fields.push(new FieldTV('bounds', 'bounds', new TermBoolTV(bounds)));

            _super.call(this, fields);
            this.tvName = "RTVInPostTV";
        }
        RTVInPostTV.mkEmptyInstance = function () {
            return new RTVInPostTV("", "", "", "");
        };
        RTVInPostTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return RTVInPostTV;
    })(ClassTV);
    exports.RTVInPostTV = RTVInPostTV;

    var RTVInPost2TV = (function (_super) {
        __extends(RTVInPost2TV, _super);
        function RTVInPost2TV(displayId, variable, initValue, bounds) {
            var fields = [];
            fields.push(new FieldTV('displayId', 'displayId', new IntegerTV(displayId)));
            fields.push(new FieldTV('variable', 'variable', new NewVarTV(new StringTV(variable), new PSTypeTV(undefined))));

            //fields.push(new FieldTV('variable', new VarTV(variable)));
            fields.push(new FieldTV('initValue', 'initValue', new TermTV(initValue)));
            fields.push(new FieldTV('bounds', 'bounds', new TermBoolTV(bounds)));

            _super.call(this, fields);
            this.tvName = "RTVInPost2TV";
        }
        RTVInPost2TV.mkEmptyInstance = function () {
            return new RTVInPost2TV(0, "", "", "");
        };
        RTVInPost2TV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return RTVInPost2TV;
    })(ClassTV);
    exports.RTVInPost2TV = RTVInPost2TV;

    var SplitoutBoundVariableTV = (function (_super) {
        __extends(SplitoutBoundVariableTV, _super);
        function SplitoutBoundVariableTV(displayId, boundVar) {
            var fields = [];
            fields.push(new FieldTV('displayId', 'displayId', new FDisplayIdTV(displayId)));
            fields.push(new FieldTV('boundVar', 'boundVar', new NewVarTV(new StringTV(boundVar), new PSTypeTV(undefined))));
            _super.call(this, fields);
            this.tvName = "SplitoutBoundVariableTV";
        }
        SplitoutBoundVariableTV.mkEmptyInstance = function () {
            return new SplitoutBoundVariableTV(0, "");
        };
        SplitoutBoundVariableTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return SplitoutBoundVariableTV;
    })(ClassTV);
    exports.SplitoutBoundVariableTV = SplitoutBoundVariableTV;

    ///////////////////////////////////////////////
    var DistributivityTV = (function (_super) {
        __extends(DistributivityTV, _super);
        function DistributivityTV(displayId) {
            var fields = [];
            fields.push(new FieldTV('displayId', 'displayId', new FDisplayIdTV(displayId)));
            _super.call(this, fields);
            this.tvName = "DistributivityTV";
        }
        DistributivityTV.mkEmptyInstance = function () {
            return new DistributivityTV(0);
        };
        DistributivityTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return DistributivityTV;
    })(ClassTV);
    exports.DistributivityTV = DistributivityTV;

    var EmptyRangeTV = (function (_super) {
        __extends(EmptyRangeTV, _super);
        function EmptyRangeTV(displayId) {
            var fields = [];
            fields.push(new FieldTV('displayId', 'displayId', new FDisplayIdTV(displayId)));
            _super.call(this, fields);
            this.tvName = "EmptyRangeTV";
        }
        EmptyRangeTV.mkEmptyInstance = function () {
            return new EmptyRangeTV(0);
        };
        EmptyRangeTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return EmptyRangeTV;
    })(ClassTV);
    exports.EmptyRangeTV = EmptyRangeTV;

    var OnePointTV = (function (_super) {
        __extends(OnePointTV, _super);
        function OnePointTV(displayId) {
            var fields = [];
            fields.push(new FieldTV('displayId', 'displayId', new FDisplayIdTV(displayId)));
            _super.call(this, fields);
            this.tvName = "OnePointTV";
        }
        OnePointTV.mkEmptyInstance = function () {
            return new OnePointTV(0);
        };
        OnePointTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return OnePointTV;
    })(ClassTV);
    exports.OnePointTV = OnePointTV;

    var QDistributivityTV = (function (_super) {
        __extends(QDistributivityTV, _super);
        function QDistributivityTV(displayId) {
            var fields = [];
            fields.push(new FieldTV('displayId', 'displayId', new FDisplayIdTV(displayId)));
            _super.call(this, fields);
            this.tvName = "QDistributivityTV";
        }
        QDistributivityTV.mkEmptyInstance = function () {
            return new QDistributivityTV(0);
        };
        QDistributivityTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return QDistributivityTV;
    })(ClassTV);
    exports.QDistributivityTV = QDistributivityTV;

    var RangeSplitTV = (function (_super) {
        __extends(RangeSplitTV, _super);
        function RangeSplitTV(displayId) {
            var fields = [];
            fields.push(new FieldTV('displayId', 'displayId', new FDisplayIdTV(displayId)));
            _super.call(this, fields);
            this.tvName = "RangeSplitTV";
        }
        RangeSplitTV.mkEmptyInstance = function () {
            return new RangeSplitTV(0);
        };

        RangeSplitTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return RangeSplitTV;
    })(ClassTV);
    exports.RangeSplitTV = RangeSplitTV;

    var ReplaceSubformulaTV = (function (_super) {
        __extends(ReplaceSubformulaTV, _super);
        function ReplaceSubformulaTV(oldSubFId, newSubF) {
            var fields = [];
            fields.push(new FieldTV('oldSubFId', 'oldSubFId', new FDisplayIdTV(oldSubFId)));
            fields.push(new FieldTV('newSubF', 'newSubF', new TermBoolTV(newSubF)));
            _super.call(this, fields);
            this.tvName = "ReplaceSubformulaTV";
        }
        ReplaceSubformulaTV.mkEmptyInstance = function () {
            return new ReplaceSubformulaTV(0, "");
        };
        ReplaceSubformulaTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return ReplaceSubformulaTV;
    })(ClassTV);
    exports.ReplaceSubformulaTV = ReplaceSubformulaTV;

    var ReplaceSubTermTV = (function (_super) {
        __extends(ReplaceSubTermTV, _super);
        function ReplaceSubTermTV(subTermId, newSubTerm) {
            var fields = [];
            fields.push(new FieldTV('subTermId', 'subTermId', new FDisplayIdTV(subTermId)));
            fields.push(new FieldTV('newSubTerm', 'newSubTerm', new TermTV(newSubTerm)));
            _super.call(this, fields);
            this.tvName = "ReplaceSubTermTV";
        }
        ReplaceSubTermTV.mkEmptyInstance = function () {
            return new ReplaceSubTermTV(0, "");
        };
        ReplaceSubTermTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return ReplaceSubTermTV;
    })(ClassTV);
    exports.ReplaceSubTermTV = ReplaceSubTermTV;

    var StartAsgnDerivationTV = (function (_super) {
        __extends(StartAsgnDerivationTV, _super);
        function StartAsgnDerivationTV(lhsVars) {
            var fields = [];
            var lhsVarsTVList = _.map(lhsVars, function (x) {
                new VarTV(x);
            });
            fields.push(new FieldTV('lhsVars', 'lhsVars', new ListTV(new VarTV(""), lhsVarsTVList)));
            _super.call(this, fields);
            this.tvName = "StartAsgnDerivationTV";
        }
        StartAsgnDerivationTV.mkEmptyInstance = function () {
            return new StartAsgnDerivationTV([]);
        };
        StartAsgnDerivationTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return StartAsgnDerivationTV;
    })(ClassTV);
    exports.StartAsgnDerivationTV = StartAsgnDerivationTV;

    var StepIntoPOTV = (function (_super) {
        __extends(StepIntoPOTV, _super);
        function StepIntoPOTV() {
            var fields = [];

            _super.call(this, fields);
            this.tvName = "StepIntoPOTV";
        }
        StepIntoPOTV.mkEmptyInstance = function () {
            return new StepIntoPOTV();
        };
        StepIntoPOTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return StepIntoPOTV;
    })(ClassTV);
    exports.StepIntoPOTV = StepIntoPOTV;

    var StepIntoProgIdTV = (function (_super) {
        __extends(StepIntoProgIdTV, _super);
        function StepIntoProgIdTV(id) {
            var fields = [];
            fields.push(new FieldTV('id', 'id', new IntegerTV(id)));
            _super.call(this, fields);
            this.tvName = "StepIntoProgIdTV";
        }
        StepIntoProgIdTV.mkEmptyInstance = function () {
            return new StepIntoProgIdTV(0);
        };
        StepIntoProgIdTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return StepIntoProgIdTV;
    })(ClassTV);
    exports.StepIntoProgIdTV = StepIntoProgIdTV;

    var StepIntoSubProgTV = (function (_super) {
        __extends(StepIntoSubProgTV, _super);
        function StepIntoSubProgTV(displayId) {
            var fields = [];
            fields.push(new FieldTV('displayId', 'displayId', new PDisplayIdTV(displayId)));
            _super.call(this, fields);
            this.tvName = "StepIntoSubProgTV";
        }
        StepIntoSubProgTV.mkEmptyInstance = function () {
            return new StepIntoSubProgTV(0);
        };
        StepIntoSubProgTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return StepIntoSubProgTV;
    })(ClassTV);
    exports.StepIntoSubProgTV = StepIntoSubProgTV;

    var StepIntoSubFormulaTV = (function (_super) {
        __extends(StepIntoSubFormulaTV, _super);
        function StepIntoSubFormulaTV(subId) {
            var fields = [];
            fields.push(new FieldTV('subId', 'subId', new FDisplayIdTV(subId)));
            _super.call(this, fields);
            this.tvName = "StepIntoSubFormulaTV";
        }
        StepIntoSubFormulaTV.mkEmptyInstance = function () {
            return new StepIntoSubFormulaTV(0);
        };
        StepIntoSubFormulaTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return StepIntoSubFormulaTV;
    })(ClassTV);
    exports.StepIntoSubFormulaTV = StepIntoSubFormulaTV;

    var StrengthenInvariantTV = (function (_super) {
        __extends(StrengthenInvariantTV, _super);
        function StrengthenInvariantTV(newInvs) {
            var fields = [];
            var newInvsTVList = _.map(newInvs, function (x) {
                new TermBoolTV(x);
            });
            fields.push(new FieldTV('newInvs', 'newInvs', new ListTV(new TermBoolTV(""), newInvsTVList)));
            _super.call(this, fields);
            this.tvName = "StrengthenInvariantTV";
        }
        StrengthenInvariantTV.mkEmptyInstance = function () {
            return new StrengthenInvariantTV([]);
        };
        StrengthenInvariantTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return StrengthenInvariantTV;
    })(ClassTV);
    exports.StrengthenInvariantTV = StrengthenInvariantTV;

    var TradingMoveToTermTV = (function (_super) {
        __extends(TradingMoveToTermTV, _super);
        function TradingMoveToTermTV(displayId, termToBeMovedId) {
            var fields = [];
            fields.push(new FieldTV('displayId', 'displayId', new FDisplayIdTV(displayId)));
            fields.push(new FieldTV('termToBeMovedId', 'termToBeMovedId', new FDisplayIdTV(termToBeMovedId)));
            _super.call(this, fields);
            this.tvName = "TradingMoveToTermTV";
        }
        TradingMoveToTermTV.mkEmptyInstance = function () {
            return new TradingMoveToTermTV(0, 0);
        };
        TradingMoveToTermTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return TradingMoveToTermTV;
    })(ClassTV);
    exports.TradingMoveToTermTV = TradingMoveToTermTV;

    var UseAssumptionsTV = (function (_super) {
        __extends(UseAssumptionsTV, _super);
        function UseAssumptionsTV(subFormulaId, newSubF) {
            var fields = [];
            fields.push(new FieldTV('subFormulaId', 'subFormulaId', new FDisplayIdTV(subFormulaId)));
            fields.push(new FieldTV('newSubF', 'newSubF', new TermBoolTV(newSubF)));
            _super.call(this, fields);
            this.tvName = "UseAssumptionsTV";
        }
        UseAssumptionsTV.mkEmptyInstance = function () {
            return new UseAssumptionsTV(0, "");
        };
        UseAssumptionsTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return UseAssumptionsTV;
    })(ClassTV);
    exports.UseAssumptionsTV = UseAssumptionsTV;

    var Init3TV = (function (_super) {
        __extends(Init3TV, _super);
        function Init3TV(name, params, retVar, preF, postF, globalInvs) {
            var fields = [];
            fields.push(new FieldTV('name', 'name', new StringTV(name)));
            var paramsTVList = _.map(params, function (x) {
                new NewVarTV(new StringTV(x), new PSTypeTV(undefined));
            });
            fields.push(new FieldTV('params', 'params', new ListTV(new NewVarTV(new StringTV(""), new PSTypeTV(undefined)), paramsTVList)));
            fields.push(new FieldTV('retVar', 'retVar', new NewVarTV(new StringTV(retVar), new PSTypeTV(undefined))));
            fields.push(new FieldTV('preF', 'preF', new TermBoolTV(preF)));
            fields.push(new FieldTV('postF', 'postF', new TermBoolTV(postF)));
            var globalInvsTVList = _.map(globalInvs, function (x) {
                new TermBoolTV(x);
            });
            fields.push(new FieldTV('globalInvs', 'globalInvs', new ListTV(new TermBoolTV(""), globalInvsTVList)));
            _super.call(this, fields);
            this.tvName = "Init3TV";
        }
        Init3TV.mkEmptyInstance = function () {
            return new Init3TV("", [], "", "", "", []);
        };
        Init3TV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return Init3TV;
    })(ClassTV);
    exports.Init3TV = Init3TV;

    var InsertVariableTV = (function (_super) {
        __extends(InsertVariableTV, _super);
        function InsertVariableTV(aVar, initVal) {
            var fields = [];
            fields.push(new FieldTV('aVar', 'aVar', new NewVarTV(new StringTV(aVar), new PSTypeTV(undefined))));
            fields.push(new FieldTV('initVal', 'initVal', new TermTV(initVal)));
            _super.call(this, fields);
            this.tvName = "InsertVariableTV";
        }
        InsertVariableTV.mkEmptyInstance = function () {
            return new InsertVariableTV("", "");
        };
        InsertVariableTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return InsertVariableTV;
    })(ClassTV);
    exports.InsertVariableTV = InsertVariableTV;

    /////////////////////////////////////////////////////////////////////////////////////
    //TODO: rename as TacticSelTV
    var TacticTV = (function (_super) {
        __extends(TacticTV, _super);
        function TacticTV() {
            var subTVNames = [
                "StepIntoUnknownProgIdxTV",
                "RTVInPost", "DeleteConjunct", "StepOut", "SimplifyAuto", "Simplify", "IntroAssignment", "IntroAssignmentEnd",
                "Init4", "InstantiateMeta", "ReplaceFormula", "GuessGuard", 'Distributivity',
                'EmptyRange', 'OnePoint', 'QDistributivity', 'RangeSplit', 'ReplaceSubformula', 'ReplaceSubTerm',
                'StepIntoPO', 'StepIntoSubProg', 'StepIntoSubFormula', 'StrengthenInvariant',
                'TradingMoveToTerm', 'InsertVariable', 'AssumePre',
                'RTVInPost2', 'SplitoutBoundVariable', 'StepIntoBA', 'StepIntoIFBA', 'IntroSwap', 'IntroIf', 'WhileStrInvSP',
                'AssumeToIf', 'PropagateAssumeUp', 'PropagateAssertionsDownSP', 'StrengthenPostSP', 'CollapseCompositions'].sort();

            // 'StartIfDerivation'
            // 'StartAsgnDerivation'
            // 'StartGCmdDerivation'
            _super.call(this, subTVNames);
            this.tvName = "TacticTV";
        }
        TacticTV.prototype.getView = function () {
            var tacticView = new typeViews.TacticView(this);
            tacticView.concreteView = typeReg.createView(this.concreteTV);
            return tacticView;
        };

        TacticTV.mkEmptyInstance = function () {
            return new TacticTV();
        };
        TacticTV.prototype.fill = function (obj) {
            _super.prototype.fill.call(this, obj);
            return this;
        };
        return TacticTV;
    })(AbstractTV);
    exports.TacticTV = TacticTV;
});
//# sourceMappingURL=TypeTVs.js.map
