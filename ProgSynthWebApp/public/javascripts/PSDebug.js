"use strict";
define(["require", "exports"], function(require, exports) {
    //Recepe for hiding the dimmed elements in console message ( output of dir(object) command)
    //(eg. to hide the the array length property and the __proto__ object attribute.
    //1. Open the google chrome developer tools (by pressing ctrl + shift + I)
    //2. Undock the google chrome developer tools. This will launch in separate window
    //3. Again press ctrl + shift + I on this new window
    //4. This will open chrome_developer_tools in another window.
    //5. open console and paste the following code
    //6. run setHideDimmed() to hide the dimmed elements
    //7. run unsetHideDimmed() to revert.
    //8. close the chrome_developer_tools window
    exports._dummyExport = undefined;

    //http://toddmotto.com/creating-jquery-style-functions-in-javascript-hasclass-addclass-removeclass-toggleclass/
    function hasClass(elem, className) {
        return new RegExp(' ' + className + ' ').test(' ' + elem.className + ' ');
    }

    function addClass(elem, className) {
        if (!hasClass(elem, className)) {
            elem.className += ' ' + className;
        }
    }

    function removeClass(elem, className) {
        var newClass = ' ' + elem.className.replace(/[\t\r\n]/g, ' ') + ' ';
        if (hasClass(elem, className)) {
            while (newClass.indexOf(' ' + className + ' ') >= 0) {
                newClass = newClass.replace(' ' + className + ' ', ' ');
            }
            elem.className = newClass.replace(/^\s+|\s+$/g, '');
        }
    }

    function toggleClass(elem, className) {
        var newClass = ' ' + elem.className.replace(/[\t\r\n]/g, ' ') + ' ';
        if (hasClass(elem, className)) {
            while (newClass.indexOf(' ' + className + ' ') >= 0) {
                newClass = newClass.replace(' ' + className + ' ', ' ');
            }
            elem.className = newClass.replace(/^\s+|\s+$/g, '');
        } else {
            elem.className += ' ' + className;
        }
    }
    function hideDimmed() {
        var dimSelector = ".console-message .section .properties .name.dimmed";
        var dimElements = window.top.document.querySelectorAll(dimSelector);
        for (var i = 0; i < dimElements.length; i++) {
            addClass(dimElements[i].parentNode, "hidden");
            addClass(dimElements[i].parentNode, "diphidden");
        }
    }

    function unhideDimmed() {
        var hiddenSelector = ".console-message .section .properties .diphidden.hidden";
        var hiddenElements = window.top.document.querySelectorAll(hiddenSelector);
        for (var i = 0; i < hiddenElements.length; i++) {
            removeClass(hiddenElements[i], "hidden");
            removeClass(hiddenElements[i], "diphidden");
        }
    }

    function setHideDimmed() {
        document.addEventListener("DOMNodeInserted", hideDimmed, false);
    }

    function unsetHideDimmed() {
        document.removeEventListener("DOMNodeInserted", hideDimmed);
    }
});
//# sourceMappingURL=PSDebug.js.map
