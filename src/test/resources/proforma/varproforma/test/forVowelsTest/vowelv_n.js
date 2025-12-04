/**
 * Calculates an array of new variation point values from other variation point values.
 * @param {Object} obj - an object with variation point values
 * @param {Number} obj.n - number of vowels in %TEXT%
 * @returns {String[]} v - all vowels from {a,e,i,o} for which there exists some greater vowel w such that the 
 *                         number of vowels v or w in the text %TEXT% is
 *                         exactly obj.n
 */
function apply(obj) {
    "use strict"
    var text= decodeURIComponent("%TEXT%").toLowerCase();
	var vs= "aeio";
	var result= [];
	for (var i=0; i<vs.length; i++) {
		var v= vs.charAt(i);
		var ws= "eiou";
		for (var j=0; j<ws.length; j++) {
			var w= ws.charAt(j);
			if (w > v) {
			    var cnt= 0;
			    for (var k = 0, len = text.length; k < len; k++) {
			    	var c= text.charAt(k);
			    	if (c === v || c === w) cnt++;
			    }
			    if (cnt === obj.n) {
			    	result.push(v);
			    	break;
			    }
			}
		}
	}
    return result;
};

