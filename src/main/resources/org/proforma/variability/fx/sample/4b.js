/**
 * Calculates an array of new variation point values from other variation point values.
 * @param {Object} obj - an object with variation point values
 * @param {Number} obj.p??? - numbers
 * @returns {Integer[]} q - The set of numbers greater than or equal to all numbers in obj and less than %MAX%
 */
function apply(obj) {
    "use strict"
	var i= 1;
	var q= []
	var min= 0;
	while (obj['p'+pad(i, 3)] !== undefined) {
		min= Math.max(min, obj['p'+pad(i, 3)]);
		i++;
	}
	for (var k= min; k<=%MAX%; k++) {
		q.push(k);
	}
    return q;
};

function pad(num, size) {
    var s = num+"";
    while (s.length < size) s = "0" + s;
    return s;
}

