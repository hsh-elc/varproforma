/**
 * Calculates a new variation point value from other variation point values.
 * @param {Object} obj - an object with variation point values
 * @param {String} obj.c - class name in { "Piece", "Part" }
 * @param {String} obj.tld - top level domain in { "co", "com", "company" }
 * @returns {String} sld - The sub level domain derived from the former data
 */
function apply(obj) {
    "use strict"
	var suffix;
	if (obj.c === "Piece") {
		suffix= obj.tld.length;
	} else {
		suffix= 0;
	}
	return obj.c.toLowerCase() + suffix;
}
