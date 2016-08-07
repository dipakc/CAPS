define(["require", "exports"], function(require, exports) {
    function json2JqueryI(json) {
        var node = json[0];
        var $node = json2JqueryL(node);
        var childs = json[1];
        _.forEach(childs, function (child) {
            var $child = exports.json2JQuery(child);
            $node.append($child);
        });
        return $node;
    }

    function json2JqueryL(obj) {
        if (obj instanceof jQuery) {
            return obj;
        } else {
            return $(obj);
        }
    }

    function json2JQuery(obj) {
        if ($.isArray(obj)) {
            return json2JqueryI(obj);
        } else {
            return json2JqueryL(obj);
        }
    }
    exports.json2JQuery = json2JQuery;

    ////////////////////////////////
    function stag(arg) {
        return '<' + arg + '>';
    }
    exports.stag = stag;

    //contentArr is an arrary of String | jquery node
    function mkTag(tag, attribDict) {
        var contentArr = [];
        for (var _i = 0; _i < (arguments.length - 2); _i++) {
            contentArr[_i] = arguments[_i + 2];
        }
        return exports.mkTag2(tag, attribDict, contentArr);
    }
    exports.mkTag = mkTag;

    function mkTag2(tag, attribDict, contentArr) {
        var attribStrArr = [];
        for (var key in attribDict) {
            if (attribDict.hasOwnProperty(key)) {
                var value = attribDict[key];
                if (value == undefined)
                    attribStrArr.push(key);
                else
                    attribStrArr.push(key + "=" + '"' + value + '"');
            }
        }
        var attribStr = attribStrArr.join(' ');

        var $retNode = $(exports.stag(tag + ' ' + attribStr) + exports.stag('/' + tag));

        for (var c in contentArr) {
            $retNode.append(contentArr[c]);
        }
        return $retNode;
    }
    exports.mkTag2 = mkTag2;
});
//# sourceMappingURL=HtmlJsonUtils.js.map
