
function json2JqueryI(json: any[]): JQuery {
    var node = json[0];
    var $node = json2JqueryL(node);
    var childs = json[1];
    _.forEach(childs, (child) => {
        var $child = json2JQuery(child);
        $node.append($child);
    });
    return $node;
}

function json2JqueryL(obj: any): JQuery {
    if (obj instanceof jQuery) {
        return obj;
    } else {
        return $(obj);
    }
}

export function json2JQuery(obj): JQuery {
    if ($.isArray(obj)) {
        return json2JqueryI(obj);
    } else {
        return json2JqueryL(obj);
    }
}

////////////////////////////////

export function stag(arg: string) {
    return '<' + arg + '>';
}

//contentArr is an arrary of String | jquery node
export function mkTag(tag: string, attribDict: any, ...contentArr: any[]): JQuery {
    return mkTag2(tag, attribDict, contentArr)
}


export function mkTag2(tag: string, attribDict: any, contentArr: any[]): JQuery {
    var attribStrArr = [];
    for (var key in attribDict) {
        if (attribDict.hasOwnProperty(key)) {
            var value = attribDict[key];
            if(value == undefined)
                attribStrArr.push(key);
            else
                attribStrArr.push(key + "=" + '"' + value + '"');
        }
    }
    var attribStr: string = attribStrArr.join(' ');

    var $retNode = $(stag(tag + ' ' + attribStr) + stag('/' + tag));

    for (var c in contentArr) {
        $retNode.append(contentArr[c]);
    }
    return $retNode;
}
