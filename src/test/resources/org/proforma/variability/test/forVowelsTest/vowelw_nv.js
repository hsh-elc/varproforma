/**
 * Calculates an array of new variation point values from other variation point values.
 * @param {Object} obj - an object with variation point values
 * @param {Number} obj.n - number of vowels in %TEXT%
 * @param {String} obj.v - lower case vowel character
 * @returns {String[]} w - The vowels w such that the number of vowels v or w 
 *                         in the text %TEXT% is exactly obj.n
 */
function apply(obj) {
    "use strict"
    var text= decodeURIComponent("%TEXT%").toLowerCase();
    var vowels= "aeiou";
    var w= [];
    for (var i=0; i<vowels.length; i++) {
        var vowel= vowels.charAt(i);
        var cnt= 0;
        for (var k = 0, len = text.length; k < len; k++) {
            var c= text.charAt(k);
            if (c === obj.v || c === vowel) cnt++;
        }
        if (cnt === obj.n) {
            w.push(vowel);
        }
    }
    return w;
};
