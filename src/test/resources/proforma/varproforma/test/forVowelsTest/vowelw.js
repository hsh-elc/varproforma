/**
 * Calculates an array of new variation point values from other variation point values.
 * @param {Object} obj - an object with variation point values
 * @param {String} obj.v - lower case vowel character
 * @returns {String[]} w - The vowels greater than obj.v
 */
function apply(obj) {
    "use strict"
	var w= [];
	var vowels= "aeiou";
	for (var i=0; i<vowels.length; i++) {
		var vowel= vowels.charAt(i);
		if (vowel > obj.v) w.push(vowel);
	}
    return w;
};
