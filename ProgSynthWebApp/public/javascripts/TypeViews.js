var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "TemplateMgr", "SynthtreeModel", "TypeTVs", "TypeRegistry", "HtmlJsonUtils"], function(require, exports, templateMgr, stm, typeTvs, tpeReg, htmlJsonUtils) {
    templateMgr;
    stm;
    typeTvs;
    tpeReg;
    htmlJsonUtils;
    var mkTag = htmlJsonUtils.mkTag;
    var mkTag2 = htmlJsonUtils.mkTag2;

    var log4javascript = require('log4javascript');

    var logger = log4javascript.getLogger("webapp.TypeViews");

    
    var PSView = (function (_super) {
        __extends(PSView, _super);
        function PSView(options) {
            _super.call(this, options);
        }
        PSView.prototype.close = function () {
            throw new Error("Abstract method close not implemented in subclass of PSView");
        };
        PSView.prototype.updateVal = function () {
            throw new Error("Abstract method updateVal not implemented in subclass of PSView");
        };
        return PSView;
    })(Backbone.View);
    exports.PSView = PSView;

    var AbstractView = (function (_super) {
        __extends(AbstractView, _super);
        function AbstractView(tv, options) {
            _super.call(this, options);
            this.tv = tv;
            this.populateConcreteView();
        }
        AbstractView.prototype.populateConcreteView = function () {
            if (this.tv.concreteTV == undefined)
                this.concreteView = undefined;
            else {
                this.concreteView = tpeReg.createView(this.tv.concreteTV);
            }
        };

        AbstractView.prototype.render = function () {
            this.$el.empty();
            this.$el.append("<div>AbstractView</div>");
            if (this.concreteView)
                this.concreteView.render(); //TODO: test
            return this;
        };

        AbstractView.prototype.close = function () {
            if (this.concreteView) {
                this.concreteView.close();
            }
        };
        AbstractView.prototype.updateVal = function () {
            this.concreteView.updateVal();
        };
        return AbstractView;
    })(PSView);
    exports.AbstractView = AbstractView;

    var NewVarView = (function (_super) {
        __extends(NewVarView, _super);
        function NewVarView(tv, options) {
            _super.call(this, options);
            this.tv = tv;
            this.populateViews();
        }
        NewVarView.prototype.populateViews = function () {
            this.varNameView = tpeReg.createView(this.tv.varNameTV);
            this.varTypeView = tpeReg.createView(this.tv.varTypeTV);
        };

        NewVarView.prototype.render = function () {
            this.$el.empty();
            this.varNameView.render();
            this.varTypeView.render();
            var jsonEle = [
                '<div class = "NewVarTV"> </div>',
                [
                    this.varNameView.el,
                    '<div> : </div>',
                    this.varTypeView.el]];

            this.$el.append(htmlJsonUtils.json2JQuery(jsonEle));
            return this;
        };

        NewVarView.prototype.close = function () {
            this.varTypeView.close();
        };
        NewVarView.prototype.updateVal = function () {
            this.varNameView.updateVal();
            this.varTypeView.updateVal();
        };
        return NewVarView;
    })(PSView);
    exports.NewVarView = NewVarView;

    var EnumView = (function (_super) {
        __extends(EnumView, _super);
        function EnumView(tv, options) {
            _super.call(this, options);
            this.tv = tv;
        }
        EnumView.prototype.render = function () {
            this.$el.empty();

            var template = templateMgr.getTemplate("enumView");
            var comboBoxDiv = $(template({ 'elements': this.tv.elements }));
            comboBoxDiv.find('select').val(this.tv.selectedElem);
            this.$el.append(comboBoxDiv);
            return this;
        };

        EnumView.prototype.close = function () {
        };
        EnumView.prototype.updateVal = function () {
            this.tv.selectedElem = this.$el.find("option:selected").val();
        };
        return EnumView;
    })(PSView);
    exports.EnumView = EnumView;

    var PSTypeView = (function (_super) {
        __extends(PSTypeView, _super);
        function PSTypeView() {
            _super.apply(this, arguments);
        }
        return PSTypeView;
    })(EnumView);
    exports.PSTypeView = PSTypeView;

    var ClassView = (function (_super) {
        __extends(ClassView, _super);
        function ClassView(tv, options) {
            _super.call(this, options);
            this.tv = tv;
            this.fieldViews = [];
            this.populateFieldViews();
        }
        ClassView.prototype.populateFieldViews = function () {
            var _this = this;
            _.forEach(this.tv.fields, function (field) {
                var fv = tpeReg.createView(field);
                _this.fieldViews.push(fv);
            });
        };

        ClassView.prototype.render = function () {
            this.$el.empty();
            var elem = $('<table border = "1" class="tablestyle" ></table>');
            _.forEach(this.fieldViews, function (fv) {
                //children().first() strips the root .div node
                var fieldDiv = fv.render().$el;
                elem.append(fieldDiv.children().first());
            });
            this.$el.append(elem);
            return this;
        };

        ClassView.prototype.close = function () {
            //_.each(this.fieldViews, function (fv) {
            //    fv.close();
            //});
        };
        ClassView.prototype.updateVal = function () {
            this.fieldViews.forEach(function (fv) {
                fv.updateVal();
            });
        };
        return ClassView;
    })(PSView);
    exports.ClassView = ClassView;

    var ListView = (function (_super) {
        __extends(ListView, _super);
        function ListView(tv, options) {
            _super.call(this, options);
            this.tv = tv;
            this.template = [
                "<div class = 'listVal'> </div>",
                ["<span class='mathquill-editable'></span>"]
            ];
            this.itemViews = [];
            this.populateItemViews();
        }
        ListView.prototype.initialize = function () {
            this.events = {
                "click .addItemBtn": "addItemHandler",
                "click .removeItemBtn": "removeItemHandler"
            };
        };
        ListView.prototype.populateItemViews = function () {
            var _this = this;
            if (this.tv.items.length == 0) {
                //this.addEmptyItem();
                //this.addEmptyItem();
            } else {
                _.forEach(this.tv.items, function (item) {
                    var fv = tpeReg.createView(item);
                    _this.itemViews.push(fv);
                });
            }
        };

        ListView.prototype.render = function () {
            this.$el.empty();
            var elem = $('<table border = "1" ></table>');
            _.forEach(this.itemViews, function (iv) {
                var row = $("<tr></tr>");
                row.append($("<td></td>").append(iv.render().el));
                row.append($("<td class='removeItemBtn'><i class='minus square outline icon'></i></td>"));
                elem.append(row);
            });
            this.$el.append(elem);
            this.$el.append("<div class='addItemBtn'><i class='plus square outline icon'></i></div>");
            return this;
        };

        ListView.prototype.addEmptyItem = function () {
            //Why to clone: the updateval directly picks up data from the objects
            //associated with the views. Hence we need a new object.
            //jQuery extend does not work.
            //var clonedTpe = <TV>jQuery.extend(true, {}, this.tv.itv);
            var newItem = this.tv.metaTV.clone();
            this.tv.items.push(newItem);

            var fv = tpeReg.createView(newItem);
            this.itemViews.push(fv);
        };

        ListView.prototype.addItemHandler = function () {
            //First update the TVs as we are redering at the end.
            this.updateVal();

            this.addEmptyItem();

            this.render();
        };

        ListView.prototype.removeItemHandler = function (event) {
            //First update the TVs as we are redering at the end.
            this.updateVal();

            var tr = $(event.currentTarget).parent();
            var n = tr.index();

            //update items and itemViews
            this.tv.items = _.reject(this.tv.items, function (item, index) {
                return index == n;
            });
            this.itemViews = _.reject(this.itemViews, function (item, index) {
                return index == n;
            });

            this.render();
        };

        ListView.prototype.updateVal = function () {
            this.itemViews.forEach(function (fv) {
                fv.updateVal();
            });
        };
        return ListView;
    })(PSView);
    exports.ListView = ListView;

    var TupleView = (function (_super) {
        __extends(TupleView, _super);
        function TupleView(tv, options) {
            _super.call(this, options);
            this.tv = tv;
            this.template = [
                "<div class = 'listVal'> </div>",
                ["<span class='mathquill-editable'></span>"]
            ];
            this.view1 = tpeReg.createView(tv.item1);
            this.view2 = tpeReg.createView(tv.item2);
        }
        TupleView.prototype.render = function () {
            this.$el.empty();
            var elem = $("<div class='tupleview'></div>");
            elem.append(this.view1.render().el);
            elem.append($("<div class='TupleSep'/>"));
            elem.append(this.view2.render().el);
            this.$el.append(elem);
            return this;
        };

        TupleView.prototype.updateVal = function () {
            this.view1.updateVal();
            this.view2.updateVal();
        };
        return TupleView;
    })(PSView);
    exports.TupleView = TupleView;

    var FieldView = (function (_super) {
        __extends(FieldView, _super);
        function FieldView(tv, options) {
            _super.call(this, options);
            this.tv = tv;
            this.rhsView = tpeReg.createView(this.tv.ftv);
        }
        FieldView.prototype.renderWithTr = function () {
            //Add in constructor : this.tagName = "tr";
            this.$el.empty();
            this.$el.addClass("fieldDiv");
            this.$el.append("<td>" + this.tv.displayName + "</td>");
            this.$el.append("<td>" + "=" + "</td>");
            this.$el.append($("<td></td>").append(this.rhsView.render().el));
            return this;
        };

        FieldView.prototype.render = function () {
            this.$el.empty();
            this.$el.addClass("fieldDiv");
            var rowElem = $("<tr></tr>");
            rowElem.append("<td>" + this.tv.displayName + "</td>");
            rowElem.append("<td>" + "=" + "</td>");
            rowElem.append($("<td></td>").append(this.rhsView.render().el));
            this.$el.append(rowElem);
            return this;
        };

        //using template
        FieldView.prototype.render2 = function () {
            var fieldName = this.tv.tvName;
            var rhsTV = this.tv.ftv;
            var fv = tpeReg.createView(rhsTV);

            var fieldValTpl = templateMgr.getTemplate("fieldVal");
            var fieldValElem = $(fieldValTpl({ ftv: rhsTV, fname: fieldName }));
            fieldValElem.find(".placeholder.fieldTVDiv").replaceWith(fv.render().el);

            this.$el.append(fieldValElem);
            return this;
        };

        FieldView.prototype.updateVal = function () {
            this.rhsView.updateVal();
        };
        return FieldView;
    })(PSView);
    exports.FieldView = FieldView;

    var PrimitiveView = (function (_super) {
        __extends(PrimitiveView, _super);
        function PrimitiveView(tv, options) {
            _super.call(this, options);
            this.tv = tv;
        }
        return PrimitiveView;
    })(PSView);
    exports.PrimitiveView = PrimitiveView;

    //User Views
    var IntegerView = (function (_super) {
        __extends(IntegerView, _super);
        function IntegerView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
            this.template = [
                "<div class = 'primitiveVal'> </div>",
                ["<input name = 'data' />"]
            ];
        }
        IntegerView.prototype.render = function () {
            this.$el.empty();

            var template = mkTag('div', { class: 'primitiveVal' }, mkTag('input', { name: 'data' }, ''));

            //Set the value
            template.find('input').val(this.tv.value);

            //this.$el.append(htmlJsonUtils.json2JQuery(this.template));
            this.$el.append(template);
            return this;
        };
        IntegerView.prototype.updateVal = function () {
            this.tv.value = this.$("input").val();
        };
        return IntegerView;
    })(PrimitiveView);
    exports.IntegerView = IntegerView;

    var FDisplayIdView = (function (_super) {
        __extends(FDisplayIdView, _super);
        function FDisplayIdView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
            this.template = [
                "<div></div>",
                [
                    [
                        "<div class = 'primitiveVal'> </div>",
                        ["<input name = 'data' />"]],
                    "<div class = 'FSelect'>Select</div>"
                ]
            ];
            this.events = {
                "click .FSelect": "onFSelect",
                "click .PSelect": "onPSelect"
            };
            this.listenTo(stm.gPSState, "model:DisplayIdSelected", $.proxy(this.onDisplayIdSelected, this));
        }
        FDisplayIdView.prototype.render = function () {
            logger.trace("Render FDisplayIdView claled");
            this.$el.empty();
            this.$el.append(htmlJsonUtils.json2JQuery(this.template));

            this.$el.find('input').val(this.tv.value);

            this.delegateEvents(this.events);
            return this;
        };
        FDisplayIdView.prototype.updateVal = function () {
            this.tv.value = this.$el.find("input").val();
            logger.trace("displayId value" + this.tv.value);
        };

        FDisplayIdView.prototype.onFSelect = function () {
            logger.trace("FSelect clicked");
            this.$el.find("input").addClass("FSelectOn");
            stm.gPSState.onFSelectClick();
        };

        FDisplayIdView.prototype.onPSelect = function () {
            logger.trace("PSelect clicked");
        };
        FDisplayIdView.prototype.onDisplayIdSelected = function () {
            this.$el.find("input.FSelectOn").val(stm.gPSState.displayId);
            this.$el.find("input").removeClass("FSelectOn");
        };
        return FDisplayIdView;
    })(IntegerView);
    exports.FDisplayIdView = FDisplayIdView;

    var PDisplayIdView = (function (_super) {
        __extends(PDisplayIdView, _super);
        function PDisplayIdView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
            this.template = [
                "<div></div>",
                [
                    [
                        "<div class = 'primitiveVal'> </div>",
                        ["<input name = 'data' />"]],
                    "<div class = 'PSelect'>Select</div>"
                ]
            ];
            this.events = {
                "click .PSelect": "onPSelect"
            };
            this.listenTo(stm.gPSState, "model:PDisplayIdSelected", $.proxy(this.onPDisplayIdSelected, this));
        }
        PDisplayIdView.prototype.render = function () {
            this.$el.empty();
            logger.trace("Render PDisplayIdView called");
            this.$el.append(htmlJsonUtils.json2JQuery(this.template));
            this.$el.find('input').val(this.tv.value);
            this.delegateEvents(this.events);
            return this;
        };
        PDisplayIdView.prototype.updateVal = function () {
            this.tv.value = this.$el.find("input").val();
            logger.trace("displayId value" + this.tv.value);
        };

        PDisplayIdView.prototype.onPSelect = function () {
            logger.trace("PSelect clicked");
            this.$el.find("input").addClass("PSelectOn");
            stm.gPSState.onPSelectClick();
        };

        PDisplayIdView.prototype.onPDisplayIdSelected = function () {
            this.$el.find("input.PSelectOn").val(stm.gPSState.pdisplayId);
            this.$el.find("input").removeClass("PSelectOn");
        };
        return PDisplayIdView;
    })(IntegerView);
    exports.PDisplayIdView = PDisplayIdView;

    var StringView = (function (_super) {
        __extends(StringView, _super);
        function StringView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
            this.template = [
                "<div class = 'primitiveVal'> </div>",
                ["<input name = 'data' />"]
            ];
        }
        StringView.prototype.render = function () {
            this.$el.empty();
            this.$el.append(htmlJsonUtils.json2JQuery(this.template));
            this.$el.find('input').val(this.tv.value);
            return this;
        };
        StringView.prototype.updateVal = function () {
            this.tv.value = this.$("input").val();
        };
        return StringView;
    })(PrimitiveView);
    exports.StringView = StringView;

    var TermView = (function (_super) {
        __extends(TermView, _super);
        function TermView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
            this.template = [
                "<div class = 'primitiveVal'> </div>",
                ["<span class='mathquill-editable'></span>"]
            ];
        }
        TermView.prototype.render = function () {
            this.$el.empty();
            this.$el.append(htmlJsonUtils.json2JQuery(this.template));
            this.$el.find('.mathquill-editable').html(this.tv.value);
            this.$('.mathquill-editable').mathquill('editable');
            return this;
        };
        TermView.prototype.updateVal = function () {
            this.tv.value = this.$(".mathquill-editable").mathquill('latex');
            ;
        };
        return TermView;
    })(PrimitiveView);
    exports.TermView = TermView;

    var VarView = (function (_super) {
        __extends(VarView, _super);
        function VarView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
            this.template = [
                "<div class = 'primitiveVal'> </div>",
                ["<span class='mathquill-editable'></span>"]
            ];
        }
        VarView.prototype.render = function () {
            this.$el.empty();
            this.$el.append(htmlJsonUtils.json2JQuery(this.template));
            this.$el.find('.mathquill-editable').html(this.tv.value);
            this.$('.mathquill-editable').mathquill('editable');
            return this;
        };
        VarView.prototype.updateVal = function () {
            this.tv.value = this.$(".mathquill-editable").mathquill('latex');
            ;
        };
        return VarView;
    })(PrimitiveView);
    exports.VarView = VarView;

    var FOLFormulaView = (function (_super) {
        __extends(FOLFormulaView, _super);
        function FOLFormulaView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
            this.template = [
                "<div class = 'primitiveVal'> </div>",
                ["<span class='mathquill-editable'></span>"]
            ];
        }
        FOLFormulaView.prototype.render = function () {
            this.$el.empty();
            this.$el.append(htmlJsonUtils.json2JQuery(this.template));
            this.$el.find('.mathquill-editable').html(this.tv.value);
            this.$('.mathquill-editable').mathquill('editable');
            return this;
        };
        FOLFormulaView.prototype.updateVal = function () {
            this.tv.value = this.$(".mathquill-editable").mathquill('latex');
            ;
        };
        return FOLFormulaView;
    })(PrimitiveView);
    exports.FOLFormulaView = FOLFormulaView;

    var TermBoolView = (function (_super) {
        __extends(TermBoolView, _super);
        function TermBoolView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
            this.template = [
                "<div class = 'primitiveVal'> </div>",
                ["<span class='mathquill-editable'></span>"]
            ];
        }
        TermBoolView.prototype.render = function () {
            this.$el.empty();
            this.$el.append(htmlJsonUtils.json2JQuery(this.template));
            this.$el.find('.mathquill-editable').html(this.tv.value);
            this.$('.mathquill-editable').mathquill('editable');
            return this;
        };
        TermBoolView.prototype.updateVal = function () {
            this.tv.value = this.$(".mathquill-editable").mathquill('latex');
            ;
        };
        return TermBoolView;
    })(PrimitiveView);
    exports.TermBoolView = TermBoolView;

    var StepIntoUnknownProgIdxView = (function (_super) {
        __extends(StepIntoUnknownProgIdxView, _super);
        function StepIntoUnknownProgIdxView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        StepIntoUnknownProgIdxView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            this.tv.idx = this.tv.fields[0].ftv.value;
        };
        return StepIntoUnknownProgIdxView;
    })(ClassView);
    exports.StepIntoUnknownProgIdxView = StepIntoUnknownProgIdxView;

    var FocusIntoUnknownProgIdxView = (function (_super) {
        __extends(FocusIntoUnknownProgIdxView, _super);
        function FocusIntoUnknownProgIdxView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        FocusIntoUnknownProgIdxView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            this.tv.idx = this.tv.fields[0].ftv.value;
        };
        return FocusIntoUnknownProgIdxView;
    })(ClassView);
    exports.FocusIntoUnknownProgIdxView = FocusIntoUnknownProgIdxView;

    var RTVInPost2View = (function (_super) {
        __extends(RTVInPost2View, _super);
        function RTVInPost2View(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        RTVInPost2View.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return RTVInPost2View;
    })(ClassView);
    exports.RTVInPost2View = RTVInPost2View;

    var SplitoutBoundVariableView = (function (_super) {
        __extends(SplitoutBoundVariableView, _super);
        function SplitoutBoundVariableView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        SplitoutBoundVariableView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return SplitoutBoundVariableView;
    })(ClassView);
    exports.SplitoutBoundVariableView = SplitoutBoundVariableView;

    var RTVInPostView = (function (_super) {
        __extends(RTVInPostView, _super);
        function RTVInPostView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        RTVInPostView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return RTVInPostView;
    })(ClassView);
    exports.RTVInPostView = RTVInPostView;

    var RetValView = (function (_super) {
        __extends(RetValView, _super);
        function RetValView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        RetValView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return RetValView;
    })(ClassView);
    exports.RetValView = RetValView;

    var InitView = (function (_super) {
        __extends(InitView, _super);
        function InitView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        InitView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return InitView;
    })(ClassView);
    exports.InitView = InitView;

    var Init4View = (function (_super) {
        __extends(Init4View, _super);
        function Init4View(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        Init4View.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return Init4View;
    })(ClassView);
    exports.Init4View = Init4View;

    var StartIfDerivationView = (function (_super) {
        __extends(StartIfDerivationView, _super);
        function StartIfDerivationView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        StartIfDerivationView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return StartIfDerivationView;
    })(ClassView);
    exports.StartIfDerivationView = StartIfDerivationView;

    var StepIntoBAView = (function (_super) {
        __extends(StepIntoBAView, _super);
        function StepIntoBAView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        StepIntoBAView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return StepIntoBAView;
    })(ClassView);
    exports.StepIntoBAView = StepIntoBAView;

    var StepIntoIFBAView = (function (_super) {
        __extends(StepIntoIFBAView, _super);
        function StepIntoIFBAView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        StepIntoIFBAView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return StepIntoIFBAView;
    })(ClassView);
    exports.StepIntoIFBAView = StepIntoIFBAView;

    var IntroSwapView = (function (_super) {
        __extends(IntroSwapView, _super);
        function IntroSwapView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        IntroSwapView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return IntroSwapView;
    })(ClassView);
    exports.IntroSwapView = IntroSwapView;

    var IntroIfView = (function (_super) {
        __extends(IntroIfView, _super);
        function IntroIfView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        IntroIfView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return IntroIfView;
    })(ClassView);
    exports.IntroIfView = IntroIfView;

    var InstantiateMetaView = (function (_super) {
        __extends(InstantiateMetaView, _super);
        function InstantiateMetaView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        InstantiateMetaView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return InstantiateMetaView;
    })(ClassView);
    exports.InstantiateMetaView = InstantiateMetaView;

    var ReplaceFormulaView = (function (_super) {
        __extends(ReplaceFormulaView, _super);
        function ReplaceFormulaView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        ReplaceFormulaView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return ReplaceFormulaView;
    })(ClassView);
    exports.ReplaceFormulaView = ReplaceFormulaView;

    var GuessGuardView = (function (_super) {
        __extends(GuessGuardView, _super);
        function GuessGuardView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        GuessGuardView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return GuessGuardView;
    })(ClassView);
    exports.GuessGuardView = GuessGuardView;

    var StartGCmdDerivationView = (function (_super) {
        __extends(StartGCmdDerivationView, _super);
        function StartGCmdDerivationView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        StartGCmdDerivationView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return StartGCmdDerivationView;
    })(ClassView);
    exports.StartGCmdDerivationView = StartGCmdDerivationView;

    var MagicView = (function (_super) {
        __extends(MagicView, _super);
        function MagicView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        MagicView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return MagicView;
    })(ClassView);
    exports.MagicView = MagicView;

    var AssumePreView = (function (_super) {
        __extends(AssumePreView, _super);
        function AssumePreView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        AssumePreView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return AssumePreView;
    })(ClassView);
    exports.AssumePreView = AssumePreView;

    var DeleteConjunctView = (function (_super) {
        __extends(DeleteConjunctView, _super);
        function DeleteConjunctView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        DeleteConjunctView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return DeleteConjunctView;
    })(ClassView);
    exports.DeleteConjunctView = DeleteConjunctView;

    var IntroAssignmentView = (function (_super) {
        __extends(IntroAssignmentView, _super);
        function IntroAssignmentView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        IntroAssignmentView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return IntroAssignmentView;
    })(ClassView);
    exports.IntroAssignmentView = IntroAssignmentView;

    var IntroAssignmentEndView = (function (_super) {
        __extends(IntroAssignmentEndView, _super);
        function IntroAssignmentEndView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        IntroAssignmentEndView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return IntroAssignmentEndView;
    })(ClassView);
    exports.IntroAssignmentEndView = IntroAssignmentEndView;

    var StepOutView = (function (_super) {
        __extends(StepOutView, _super);
        function StepOutView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        StepOutView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return StepOutView;
    })(ClassView);
    exports.StepOutView = StepOutView;

    var FocusOutView = (function (_super) {
        __extends(FocusOutView, _super);
        function FocusOutView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        FocusOutView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return FocusOutView;
    })(ClassView);
    exports.FocusOutView = FocusOutView;

    var WhileStrInvSPView = (function (_super) {
        __extends(WhileStrInvSPView, _super);
        function WhileStrInvSPView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        WhileStrInvSPView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return WhileStrInvSPView;
    })(ClassView);
    exports.WhileStrInvSPView = WhileStrInvSPView;

    var CollapseCompositionsView = (function (_super) {
        __extends(CollapseCompositionsView, _super);
        function CollapseCompositionsView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        CollapseCompositionsView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return CollapseCompositionsView;
    })(ClassView);
    exports.CollapseCompositionsView = CollapseCompositionsView;

    var AssumeToIfView = (function (_super) {
        __extends(AssumeToIfView, _super);
        function AssumeToIfView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        AssumeToIfView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return AssumeToIfView;
    })(ClassView);
    exports.AssumeToIfView = AssumeToIfView;

    var PropagateAssumeUpView = (function (_super) {
        __extends(PropagateAssumeUpView, _super);
        function PropagateAssumeUpView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        PropagateAssumeUpView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return PropagateAssumeUpView;
    })(ClassView);
    exports.PropagateAssumeUpView = PropagateAssumeUpView;

    var StrengthenPostSPView = (function (_super) {
        __extends(StrengthenPostSPView, _super);
        function StrengthenPostSPView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        StrengthenPostSPView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return StrengthenPostSPView;
    })(ClassView);
    exports.StrengthenPostSPView = StrengthenPostSPView;

    var PropagateAssertionsDownSPView = (function (_super) {
        __extends(PropagateAssertionsDownSPView, _super);
        function PropagateAssertionsDownSPView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        PropagateAssertionsDownSPView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return PropagateAssertionsDownSPView;
    })(ClassView);
    exports.PropagateAssertionsDownSPView = PropagateAssertionsDownSPView;

    var SimplifyAutoView = (function (_super) {
        __extends(SimplifyAutoView, _super);
        function SimplifyAutoView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        SimplifyAutoView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return SimplifyAutoView;
    })(ClassView);
    exports.SimplifyAutoView = SimplifyAutoView;

    var SimplifyView = (function (_super) {
        __extends(SimplifyView, _super);
        function SimplifyView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        SimplifyView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
        };
        return SimplifyView;
    })(ClassView);
    exports.SimplifyView = SimplifyView;

    ///////////////////////////
    var DistributivityView = (function (_super) {
        __extends(DistributivityView, _super);
        function DistributivityView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        DistributivityView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return DistributivityView;
    })(ClassView);
    exports.DistributivityView = DistributivityView;

    var EmptyRangeView = (function (_super) {
        __extends(EmptyRangeView, _super);
        function EmptyRangeView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        EmptyRangeView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return EmptyRangeView;
    })(ClassView);
    exports.EmptyRangeView = EmptyRangeView;

    var OnePointView = (function (_super) {
        __extends(OnePointView, _super);
        function OnePointView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        OnePointView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return OnePointView;
    })(ClassView);
    exports.OnePointView = OnePointView;

    var QDistributivityView = (function (_super) {
        __extends(QDistributivityView, _super);
        function QDistributivityView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        QDistributivityView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return QDistributivityView;
    })(ClassView);
    exports.QDistributivityView = QDistributivityView;

    var RangeSplitView = (function (_super) {
        __extends(RangeSplitView, _super);
        function RangeSplitView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        RangeSplitView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return RangeSplitView;
    })(ClassView);
    exports.RangeSplitView = RangeSplitView;

    var ReplaceSubformulaView = (function (_super) {
        __extends(ReplaceSubformulaView, _super);
        function ReplaceSubformulaView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        ReplaceSubformulaView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return ReplaceSubformulaView;
    })(ClassView);
    exports.ReplaceSubformulaView = ReplaceSubformulaView;

    var ReplaceSubTermView = (function (_super) {
        __extends(ReplaceSubTermView, _super);
        function ReplaceSubTermView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        ReplaceSubTermView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return ReplaceSubTermView;
    })(ClassView);
    exports.ReplaceSubTermView = ReplaceSubTermView;

    var StartAsgnDerivationView = (function (_super) {
        __extends(StartAsgnDerivationView, _super);
        function StartAsgnDerivationView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        StartAsgnDerivationView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return StartAsgnDerivationView;
    })(ClassView);
    exports.StartAsgnDerivationView = StartAsgnDerivationView;

    //
    var StepIntoPOView = (function (_super) {
        __extends(StepIntoPOView, _super);
        function StepIntoPOView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        StepIntoPOView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return StepIntoPOView;
    })(ClassView);
    exports.StepIntoPOView = StepIntoPOView;

    var StepIntoProgIdView = (function (_super) {
        __extends(StepIntoProgIdView, _super);
        function StepIntoProgIdView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        StepIntoProgIdView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return StepIntoProgIdView;
    })(ClassView);
    exports.StepIntoProgIdView = StepIntoProgIdView;

    var StepIntoSubProgView = (function (_super) {
        __extends(StepIntoSubProgView, _super);
        function StepIntoSubProgView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        StepIntoSubProgView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return StepIntoSubProgView;
    })(ClassView);
    exports.StepIntoSubProgView = StepIntoSubProgView;

    var FocusIntoSubProgView = (function (_super) {
        __extends(FocusIntoSubProgView, _super);
        function FocusIntoSubProgView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        FocusIntoSubProgView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return FocusIntoSubProgView;
    })(ClassView);
    exports.FocusIntoSubProgView = FocusIntoSubProgView;

    var StepIntoSubFormulaView = (function (_super) {
        __extends(StepIntoSubFormulaView, _super);
        function StepIntoSubFormulaView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        StepIntoSubFormulaView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };

        StepIntoSubFormulaView.prototype.onDisplayIdSelect = function () {
            var displayId = 10;
            //this.$el.find('table ')
            /*
            this.$el.empty();
            var elem = $('<table border = "1" ></table>');
            _.forEach(this.fieldViews, (fv: FieldView) => {
            //children().first() strips the root .div node
            var fieldDiv = fv.render().$el;
            elem.append(fieldDiv.children().first());
            });
            this.$el.append(elem);
            return this;
            */
        };
        return StepIntoSubFormulaView;
    })(ClassView);
    exports.StepIntoSubFormulaView = StepIntoSubFormulaView;

    var StrengthenInvariantView = (function (_super) {
        __extends(StrengthenInvariantView, _super);
        function StrengthenInvariantView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        StrengthenInvariantView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return StrengthenInvariantView;
    })(ClassView);
    exports.StrengthenInvariantView = StrengthenInvariantView;

    var TradingMoveToTermView = (function (_super) {
        __extends(TradingMoveToTermView, _super);
        function TradingMoveToTermView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        TradingMoveToTermView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return TradingMoveToTermView;
    })(ClassView);
    exports.TradingMoveToTermView = TradingMoveToTermView;

    var UseAssumptionsView = (function (_super) {
        __extends(UseAssumptionsView, _super);
        function UseAssumptionsView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        UseAssumptionsView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return UseAssumptionsView;
    })(ClassView);
    exports.UseAssumptionsView = UseAssumptionsView;

    var Init3View = (function (_super) {
        __extends(Init3View, _super);
        function Init3View(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        Init3View.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return Init3View;
    })(ClassView);
    exports.Init3View = Init3View;

    var InsertVariableView = (function (_super) {
        __extends(InsertVariableView, _super);
        function InsertVariableView(tv, options) {
            _super.call(this, tv, options);
            this.tv = tv;
        }
        InsertVariableView.prototype.updateVal = function () {
            _super.prototype.updateVal.call(this);
            //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
        };
        return InsertVariableView;
    })(ClassView);
    exports.InsertVariableView = InsertVariableView;

    ///////////////////////////
    var TacticView = (function (_super) {
        __extends(TacticView, _super);
        function TacticView(tv, options) {
            _super.call(this, tv, options);
            /**/ this.logger = log4javascript.getLogger("webapp.TacticView");
            this.tv = tv;
        }
        TacticView.prototype.initialize = function () {
            if (this.jel) {
                this.el = this.jel.get();
            }

            /**/ //this.logger.trace("TacticView: registering handler for change .inputPanelTVCombo");
            this.events = {
                "change .inputPanelTVCombo": "onComboSelect"
            };
        };

        TacticView.prototype.render_old = function () {
            /**/ this.logger.trace("Rendering TacticView ...");
            this.$el.empty();

            var tacticTVs = _.map(this.tv.subTVNames, function (st) {
                return st.replace(/TV$/, "");
            });

            var subViewDiv = this.concreteView ? this.concreteView.render().el : '';

            var template = templateMgr.getTemplate("abstractVal");

            var comboBoxDiv = template({
                'subTVs': tacticTVs,
                'absTVName': this.tv.tvName.replace(/TV$/, ""),
                'concreteDiv': subViewDiv
            });

            this.$el.append(comboBoxDiv);
            return this;
        };

        TacticView.prototype.template = function (absTVName, subTVs, $subView) {
            var subTVOptions = _.map(subTVs, function (st) {
                return mkTag('option', { 'value': st }, st);
            });

            var chooseOptionDiv = mkTag('option', { 'value': '', 'disabled': undefined, 'selected': undefined }, 'Choose ...');

            var comboOptionsDiv = [chooseOptionDiv].concat(subTVOptions);

            var x = mkTag('div', { 'class': 'template', 'id': 'abstractVal' }, mkTag('div', { 'class': "AbstractVal" }, mkTag('div', { 'class': 'absValueName' }, absTVName), mkTag('div', {}, mkTag2('select', { 'class': "inputPanelTVCombo" }, comboOptionsDiv)), mkTag('div', { 'class': 'selectedSubTV' }, $subView)));
            return x;
        };

        TacticView.prototype.render = function () {
            this.$el.empty();

            var absTVName = this.tv.tvName.replace(/TV$/, "");
            var subTVs = _.map(this.tv.subTVNames, function (st) {
                return st.replace(/TV$/, "");
            });
            var $subView = this.concreteView ? this.concreteView.render().$el : $('<div></div>');

            this.$el.append(this.template(absTVName, subTVs, $subView));

            //select the option
            if (this.concreteView) {
                var tacticName = this.concreteView.tv.tvName.replace(/TV$/, "");
                this.$el.find('select.inputPanelTVCombo').val(tacticName);
            }

            return this;
        };

        TacticView.prototype.onComboSelect = function (event) {
            /**/ this.logger.trace("TacticView.onComboSelect called");
            logger.trace("Tactic::onComboSelect");
            this.$('.selectedSubTV').empty();
            var tacticName = $(event.target).find("option:selected").val();
            this.concreteView = tpeReg.createViewFromTVName(tacticName + 'TV');
            ;
            this.tv.concreteTV = this.concreteView.tv;
            this.$('.selectedSubTV').append(this.concreteView.render().el);
            this.$('.mathquill-editable').mathquill('redraw');

            //Clear Errors
            $(".ApplyTacticError").html("");
        };
        return TacticView;
    })(AbstractView);
    exports.TacticView = TacticView;
});
//# sourceMappingURL=TypeViews.js.map
