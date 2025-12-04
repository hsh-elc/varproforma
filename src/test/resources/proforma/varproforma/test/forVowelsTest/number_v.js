/**
 * Calculates a new variation point value from other variation point values.
 * @param {Object} obj - an object with variation point values
 * @param {String} obj.v - lower case vowel character
 * @returns {Number} number of vowels v in the text %TEXT%
 */
function apply(obj) {
    "use strict"
    var text= decodeURIComponent("%TEXT%").toLowerCase();
    var cnt= 0;
    for (var i = 0, len = text.length; i < len; i++) {
    	if (text.charAt(i) === obj.v) cnt++;
    }
    return cnt;
};
