package org.proforma.variability.fx.sample;

import org.proforma.variability.transfer.CV;
import org.proforma.variability.transfer.VarSpecRoot;
import org.proforma.variability.transfer.Vc;
import org.proforma.variability.transfer.CVp;
import org.proforma.variability.transfer.Vp;
import org.proforma.variability.transfer.Vs;
import org.proforma.variability.util.Derive;
import org.proforma.variability.util.JavascriptString;

import javafx.application.Application;
import javafx.stage.Stage;

public class Sample2 extends Application {

    private static final String DEFAULT_USER_INPUT= "The quick brown fox jumps over the lazy dog.";
	private static String JS_SRC_NUMBER;
	private static String JS_SRC_VOWELW;
	static {
		JS_SRC_NUMBER= Sample.readJavascriptResource(Sample2.class, "number.js").replace("%TEXT%", JavascriptString.encodeURIComponent(DEFAULT_USER_INPUT));
		JS_SRC_VOWELW= Sample.readJavascriptResource(Sample2.class, "vowelw.js");
	}
	
	@Override
	public void start(Stage primaryStage) {
	    Vp c= Vp.s("c");
        Vp v= Vp.c("v");
        Vp w= Vp.c("w");
        Vp n= Vp.i("n");
        Vp ql= Vp.c("ql");
        Vp qr= Vp.c("qr");

        VarSpecRoot cvSpec= VarSpecRoot.build(c, v, w, n, ql, qr)
	            .combineGroup()
	                .collect("Program", "Counter", "CountVowels") 
	                .combineGroup()
                        .collect('a','e','i','o')
                        .deriveCollect(JS_SRC_VOWELW)
//                        .collect('a','e','i','o','u')
	                    .deriveVal(JS_SRC_NUMBER)
	                .endCombineGroup()
	                .collectGroup()   // valid value pairs for <ql> and <qr>
	                    .combine('\u00AB', '\u00BB')
	                    .combine('[', ']') 
	                .endCollectGroup()
	            .endCombineGroup()
	        .endBuild();
		
		char defaultV= 'a', defaultW= 'e';
		CV defaultValue= CV.fromValues(
				new Vs(c,"Program"), 
				new Vc(v, defaultV),
				new Vc(w, defaultW), 
				Derive.deriveValueFrom(
				        Derive.getAsKeyOrderedMap(
				            CVp.of(v, w),
				            CV.fromValues(defaultV, defaultW)), 
				        n, JS_SRC_NUMBER), 
				new Vc(ql, '['), 
				new Vc(qr,']'));

		
		Sample.start("Sample 2", cvSpec, defaultValue, primaryStage);
	}
	
    
    public static void main(String[] args) {
		launch(args);
	}
}