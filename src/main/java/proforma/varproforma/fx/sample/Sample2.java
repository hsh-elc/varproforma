package proforma.varproforma.fx.sample;

import javafx.application.Application;
import javafx.stage.Stage;
import proforma.varproforma.CV;
import proforma.varproforma.CVp;
import proforma.varproforma.VarSpecRoot;
import proforma.varproforma.Vc;
import proforma.varproforma.Vp;
import proforma.varproforma.Vs;
import proforma.varproforma.util.Derive;
import proforma.varproforma.util.JavascriptString;

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