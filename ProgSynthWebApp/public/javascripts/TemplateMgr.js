/// <reference path="typings/requirejs/require.d.ts" />
/// <reference path="typings/handlebars/handlebars.d.ts" />
/// <amd-dependency path="text!templates/_tacticInputDivs.tpl.html" />
// Exports the compiled handlebar templates
"use strict";
define(["require", "exports", "text!templates/_tacticInputDivs.tpl.html"], function(require, exports) {
    var templates = require("text!templates/_tacticInputDivs.tpl.html");

    var getTemplateSrc = function (name) {
        var retVal = $(templates).filter("div#" + name + ".template").html();
        if (!retVal) {
            throw new Error("template not found: " + name);
        } else {
            return retVal;
        }
    };

    var abstractValSrc = getTemplateSrc("abstractVal");
    console.assert(abstractValSrc != undefined);
    var abstractValTemplate = Handlebars.compile(abstractValSrc);
    console.assert(abstractValTemplate);

    var primitiveValSrc = getTemplateSrc("primitiveVal");
    console.assert(primitiveValSrc != undefined);
    var primitiveValTemplate = Handlebars.compile(primitiveValSrc);
    console.assert(primitiveValTemplate);

    var fieldValSrc = getTemplateSrc("fieldVal");
    console.assert(fieldValSrc != undefined);
    var fieldValTemplate = Handlebars.compile(fieldValSrc);
    console.assert(fieldValTemplate);

    //Handlebars.registerPartial("fieldValPartial", fieldValSrc);
    var compositeValSrc = getTemplateSrc("compositeVal");
    console.assert(compositeValSrc != undefined);
    var compositeValTemplate = Handlebars.compile(compositeValSrc);
    console.assert(compositeValTemplate);

    var listValSrc = getTemplateSrc("listVal");
    console.assert(listValSrc != undefined);
    var listValTemplate = Handlebars.compile(listValSrc);
    console.assert(listValTemplate);

    var tupleValSrc = getTemplateSrc("tupleVal");
    console.assert(tupleValSrc != undefined);
    var tupleValTemplate = Handlebars.compile(tupleValSrc);
    console.assert(tupleValTemplate);

    var inputFormSrc = getTemplateSrc("inputForm");
    console.assert(inputFormSrc != undefined);
    var inputFormTemplate = Handlebars.compile(inputFormSrc);
    console.assert(inputFormTemplate);

    var siblingsViewSrc = getTemplateSrc("siblingsView");
    console.assert(siblingsViewSrc != undefined);
    var siblingsViewTemplate = Handlebars.compile(siblingsViewSrc);
    console.assert(siblingsViewTemplate);

    var enumViewSrc = getTemplateSrc("enumView");
    console.assert(enumViewSrc != undefined);
    var enumViewTemplate = Handlebars.compile(enumViewSrc);
    console.assert(enumViewTemplate);

    var newVarViewSrc = getTemplateSrc("newVarView");
    console.assert(newVarViewSrc != undefined);
    var newVarViewTemplate = Handlebars.compile(newVarViewSrc);
    console.assert(newVarViewTemplate);

    function getTemplate(name) {
        switch (name) {
            case "abstractVal":
                return abstractValTemplate;
            case "primitiveVal":
                return primitiveValTemplate;
            case "compositeVal":
                return compositeValTemplate;
            case "fieldVal":
                return fieldValTemplate;
            case "listVal":
                return listValTemplate;
            case "tupleVal":
                return tupleValTemplate;
            case "inputForm":
                return inputFormTemplate;
            case "siblingsView":
                return siblingsViewTemplate;
            case "enumView":
                return enumViewTemplate;
            case "newVarView":
                return newVarViewTemplate;
        }
        throw new Error("template not found: " + name);
    }
    exports.getTemplate = getTemplate;
});
//# sourceMappingURL=TemplateMgr.js.map
