/**
 * highlight.js needs a window object to be able to register with AMD.
 * @type {{}}
 */
var window = {};


/**
 * Most simple implementation of the AMD define function where highlight.js will register.
 * Dependencies are ignored at the moment.
 *
 * @param moduleName {String} the name of the module that is registered.
 * @param dependencies {[String]} the module names of the modules the defined module depends on
 * @param factory the module factory function
 */
function define(moduleName, dependencies, factory) {
    define.modules[moduleName] = {
        module: factory() // TODO invoke with dependencies
    };
}

/**
 * To signal, it is AMD, we have to give the modules a hint.
 * @type {boolean}
 */
define.amd = true;

/**
 * The modules registered through the define function.
 * @type {{}}
 */
define.modules = {};

/**
 * Returns the module with the given moduleName.
 *
 * @param moduleName the name of the module to return
 * @returns {*} the module returned by the module factory when registered using define
 */
function require(moduleName) {
    return define.modules[moduleName].module;
}

/**
 * Formats the given source code assuming it represents a source of the given language.
 *
 * @param language  the name of the language. Must be one of the property keys in aliases.properties
 * @param sourceCode a raw source code string
 * @returns {*} a html string containing a lot of <span> tags with highlight classes or the given
 *      source code on error
 */
function highlight(language, sourceCode) {
    var result = require("hljs").highlight(language, sourceCode);
    if (result && result.value) {
        return result.value;
    }
    else {
        return sourceCode;
    }
}

