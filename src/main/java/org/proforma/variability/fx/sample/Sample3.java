package org.proforma.variability.fx.sample;

import org.proforma.variability.transfer.CV;
import org.proforma.variability.transfer.VarSpecRoot;
import org.proforma.variability.transfer.Vp;
import org.proforma.variability.util.SpecRandomHelper;

import javafx.application.Application;
import javafx.stage.Stage;

public class Sample3 extends Application {

	private static String JS_SRC_PIECE_PART_SLD;
	static {
		JS_SRC_PIECE_PART_SLD= Sample.readJavascriptResource(Sample2.class, "piece_part_sld.js");
	}
	
	@Override
	public void start(Stage primaryStage) {
		
	    VarSpecRoot cvSpec
        = VarSpecRoot.build(Vp.s("c"), Vp.s("tld"), Vp.s("sld"), Vp.s("n"), Vp.s("m"), Vp.s("ms"), Vp.s("nval"), Vp.i("mval"), Vp.s("b1"), Vp.s("b2"), Vp.s("e"))
        
            .collectGroup()
          
                .combine("Customer", "com", "company", "customer name", "customer id", "customer ids", "Johnson", 77865, "+", "+", "Error")
              
                .combineGroup()
                    .collect("Piece", "Part")
                    .collect("co", "com", "company")
                    .deriveVal(JS_SRC_PIECE_PART_SLD)
                    .deriveVal("function apply(obj) { return obj.c.toLowerCase() + ' name'; }")
                    .deriveVal("function apply(obj) { return obj.c.toLowerCase() + ' number'; }")
                    .deriveVal("function apply(obj) { return obj.c.toLowerCase() + ' numbers'; }")
                    .combine("thing", 332, "{", "}", "Throwable")
                .endCombineGroup()
          
          
                // Group of "c" through "mval"
                // there are three domains: Student, State and Prod
                // all with specific subdata
                .define("student", "c", "tld", "sld", "n", "m", "ms", "nval", "mval")
                    .combineGroup()           
                        .val("Student")
                        .collect("edu", "org") 
                        .collect("domain", "university")
                        .val("name")
                          // .collect() without parameters reuses the parent node's key order "m", "ms"
                        .collectGroup()
                            .combineGroup("ms", "m")   // optionally switch the key order in any subset definition
                                               // The framework will reorder these according to the parent
                                               // node's order.
                                .val("matriculation numbers")  
                                .val("matriculation number")
                            .endCombineGroup()
                            .combineGroup() // here we reuse the parent node's key order "m", "ms"
                                .val("student id")
                                .val("student ids")
                            .endCombineGroup()
                        .endCollectGroup()
                        .collect("Smith", "Doe", "Baldwin")
                        .val(1000000)  
                    .endCombineGroup()           
                .endDefine()
             
//                .define("state", "c", "tld", "sld", "n", "m", "ms", "nval", "mval")
//                    .combineGroup()
//                        .val("State")
//                        .collectGroup()
//                            .combine("net", "geo", "state name", "number of residents", 
//                            		 "numbers of residents", "State", 1234567)  
//                            .combine("org", "geo", "state name", "number of residents", 
//                            		 "numbers of residents", "State", 1234567) 
//                            .combine("de", "land", "state name", "number of residents", 
//                            		 "numbers of residents", "Bayern", 12860010)  
//                            .combine("de", "staat", "state name", "number of residents", 
//                            		 "numbers of residents", "Bayern", 12860010)  
//                            .combine("ch", "kanton", "state name", "number of residents", 
//                            		 "numbers of residents", "Waadt", 784681)    
//                            .combine("uk", "region", "state name", "number of residents", 
//                            		 "numbers of residents", "Wales", 3092000)  
//                        .endCollectGroup()
//                    .endCombineGroup()
//                .endDefine()
       
                
               
                .define("state", "c", "n", "m", "ms", "tld", "sld", "nval", "mval")
               
                    // .combine() without parameters reuses the parent node's key order
                    .combineGroup()
                        .val("State")
                        .val("state name")
                        .val("number of residents")
                        .val("numbers of residents")
                        .collectGroup("tld", "sld", "mval", "nval")
                            .combineGroup()
                                .collect("net", "org")
                                .combine("geo", 1234567, "State")  
                            .endCombineGroup()
                            .combineGroup("sld", "tld", "nval", "mval")
                                .collect("land", "staat")
                                .combine("de", "Bayern", 12860010)  
                            .endCombineGroup()
                            .combine("ch", "kanton", 784681, "Waadt")    
                            .combine("uk", "region", 3092000, "Wales")  
                        .endCollectGroup()
                    .endCombineGroup()
                .endDefine()
   

             
             
             
             

             
                .define("prod", "c", "tld", "sld", "n", "m", "ms", "nval", "mval")
        
                    // we could deliberately reorder key specifications in subsets.
                    // The framework will reorder them according to the parent node's order.
                    // Here we specify the <c>-key at third position instead of first.
                    .combineGroup("tld", "sld", "c", "n", "m", "ms", "nval", "mval")
                
                        .val("com")
                        .val("mymart")
                        .val("Prod")
                        .val("product name")
                        .val("article number")
                        .val("article numbers")
                        .collect("Ketchup", "Butter", "Bread")
                        .val(78266)
        //                .infer(vt -> new Random().nextInt(900000)+100000).noVariant("prod_articlenumber")  
                                                                                               // noVariant bedeutet: bei der Variantenzählung wird hier ein Faktor 1 generiert. 
                                                                                               // außerdem muss der bei der Variantengenerierung konkret gewählte Wert 
                                                                                               // neben der Variantenummer gespeichert werden.
                    
                    .endCombineGroup()
                .endDefine()
             
                .define("()", "b1", "b2")
                    .combine("(", ")")
                .endDefine()
                .define("[] and <>", "b1", "b2")
                    .collectGroup()
                        .combine("[", "]")
                        .combineGroup().val("<").val(">").endCombineGroup()
                    .endCollectGroup()
                .endDefine()

             
                .combineGroup()
                 
                    .collectGroup()
                        .ref("student")
                        .ref("prod")
                    .endCollectGroup()
        
                    .collectGroup()
                        .ref("()")
                        .ref("[] and <>")
                    .endCollectGroup()
                    .val("RuntimeException")
                .endCombineGroup()
        
                .combineGroup()
             
                    .collectGroup()
                        .ref("student")
                        .ref("state")
                    .endCollectGroup()
        
                    .ref("()")

                    .val("IllegalArgumentException")
                .endCombineGroup()

                .combineGroup()
             
                    .collectGroup()
                        .ref("prod")
                        .ref("state")
                    .endCollectGroup()
        
                    .ref("[] and <>")
                 
                    .collect("Exception", "IllegalArgumentException")
                .endCombineGroup()
    
            .endCollectGroup()
        .endBuild();

		CV defaultValue= SpecRandomHelper.random(cvSpec).getCV();
		
		Sample.start("Sample 3", cvSpec, defaultValue, primaryStage);
	}
	
    
    public static void main(String[] args) {
		launch(args);
	}
}