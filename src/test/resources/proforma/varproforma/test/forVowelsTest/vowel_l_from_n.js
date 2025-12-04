/**
 * Calculates a new variation point value from other variation point values.
 * @param {Object} obj - an object with variation point values
 * @param {Number} obj.n - number of vowels in %TEXT%
 * @returns {String} l - if n==1, return "" else return the plural "s".
 */
function apply(obj) {
    "use strict"
    if (obj.n === 1) return "";
    return "s";
};
