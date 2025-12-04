package proforma.varproforma.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import proforma.util.div.XmlUtils;
import proforma.util.div.XmlUtils.MarshalOption;
import proforma.varproforma.CVListVp;
import proforma.varproforma.VarSpecRoot;
import proforma.varproforma.Vp;
import proforma.varproforma.util.SpecValueConverter;





public class LargerDemoTest {
    

    private static String fixtureFolder; // here we find source files
    private static Path tmpDir;

    @BeforeClass
    public static void setupClass() {
        
                // source folder
        fixtureFolder= LargerDemoTest.class.getPackage().getName();
        fixtureFolder += ".for"+LargerDemoTest.class.getSimpleName();
        fixtureFolder= fixtureFolder.replace(".", File.separator);

        // working folder
        try {
            tmpDir= Files.createTempDirectory(LargerDemoTest.class.getSimpleName());
        } catch (IOException e) {
            Assert.fail("Preparation of "+LargerDemoTest.class+": cannot create temp dir: "+e);
        }
    }

    private VarSpecRoot build() {
        VarSpecRoot cvs
        = VarSpecRoot.build(Vp.s("c"), Vp.s("tld"), Vp.s("sld"), Vp.s("n"), Vp.s("m"), Vp.s("ms"), Vp.s("nval"), Vp.i("mval"), Vp.s("b1"), Vp.s("b2"), Vp.s("e"))
        
            .collectGroup()
          
                .combine("Customer", "com", "company", "customer name", "customer id", "customer ids", "Johnson", 77865, "+", "+", "Error")
              
                .combineGroup()
                    .collect("Piece", "Part")
                    .collect("co", "com", "company")
                    .deriveVal( 
                		  "/**                                                                            \n"
        				+ " * Calculates a new variation point value from other variation point values.   \n"
        				+ " * @param {Object} obj - an object with variation point values                 \n"
        				+ " * @param {String} obj.c - class name in { 'Piece', 'Part' }                   \n"
        				+ " * @param {String} obj.tld - top level domain in { 'co', 'com', 'company' }    \n"
        				+ " * @returns {String} sld - The sub level domain derived from the former data   \n"
        				+ " */                                                                            \n"
                    	+ "function apply(obj) {                                                          \n"
                        + "    'use strict'                                                               \n"
                    	+ "    var suffix;                                                                \n"
                    	+ "    if (obj.c === 'Piece') {                                                   \n"
                    	+ "        suffix= obj.tld.length;                                                \n"
                    	+ "    } else {                                                                   \n"
                    	+ "        suffix= 0;                                                             \n"
                    	+ "    }                                                                          \n"
                    	+ "    return obj.c.toLowerCase() + suffix;                                       \n"
                        + "}                                                                              \n"
                    )
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
                                                                                               // noVariant bedeutet: bei der Variantenz�hlung wird hier ein Faktor 1 generiert. 
                                                                                               // au�erdem muss der bei der Variantengenerierung konkret gew�hlte Wert 
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
        return cvs;
    }

    
    

    @Test
    public void shouldPrettyPrint() throws IOException {
        
        VarSpecRoot cvs= build();
        cvs.prettyPrint();
        
        CVListVp set= SpecValueConverter.expandNode(cvs);

        set.sort();
        set.prettyPrint();
        
        String observedFilename= "observedPrettyPrint1.txt";
        try (PrintStream observedOutput= new PrintStream(tmpDir.resolve(observedFilename).toFile(), "UTF-8")) {
            set.prettyPrint(observedOutput);
        }
        String expectedFilename= "expectedPrettyPrint1.txt";
        File fixtureFile= new File(Defaults.RESOURCE_FOLDER+File.separator+fixtureFolder+File.separator+expectedFilename);
        String expected = new String(Files.readAllBytes(fixtureFile.toPath()), StandardCharsets.UTF_8);
        String observed = new String(Files.readAllBytes(tmpDir.resolve(observedFilename)), StandardCharsets.UTF_8);
        Assert.assertEquals("Unexpected pretty print output",  expected, observed);
    }
    
    
    @Test
    public void shouldMarshal() {
        VarSpecRoot cvs= build();
        System.out.println("CVSpec:");
        cvs.prettyPrint(System.out);
        System.out.println();

        CVListVp cvs0= SpecValueConverter.expandNode(cvs);
        cvs0.sort();
        
        String s = null;
        try {
            s = XmlUtils.marshalToXml(cvs, new MarshalOption[] { MarshalOption.CDATA }, VarSpecRoot.class);
        } catch (Throwable e) {
            Assert.fail("Cannot marshal "+cvs.getClass()+". "+e);
        }
        System.out.println("CVSpec as XML:");
        System.out.println(s);
        System.out.println();
        VarSpecRoot cvsUnmarshalled= null;
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        try {
            cvsUnmarshalled = XmlUtil.unmarshalToObject(bytes, VarSpecRoot.class);
        } catch (Throwable e) {
            Assert.fail("Cannot unmarshal "+cvs.getClass()+". "+e);
        }
        CVListVp cvs1= SpecValueConverter.expandNode(cvsUnmarshalled);
        cvs1.sort();
        System.out.println("Before marshalling:");
        cvs0.prettyPrint();
        System.out.println();
        System.out.println("After unmarshalling:");
        cvs1.prettyPrint();
        System.out.println();
        System.out.println("CVSpec after unmarshalling:");
        cvsUnmarshalled.prettyPrint();
        System.out.println();
        Assert.assertEquals("CVSpec should expand to equal set after marshal/unmarshal",  cvs0, cvs1);
    }
    

//    @Test
//    public void shouldMarshalSet() throws IOException {
//        
//        VariationTupleSetBuilder vtsb= build();
//        
//        VariationTupleSet vts= vtsb.toSet();
//        vts.sort();
//        
//        String observedFilenameXml= "observedSet1.xml";
//        VariationTupleSetTO vtsto= Transformer.marshalVariationTupleSet(vts);
//        VariationTupleSet vtsUnmarshalled= Transformer.unmarshalVariationTupleSet(vtsto);
//        Assert.assertEquals("Variation tuple set should be the same after marshalling and unmarshalling", vts, vtsUnmarshalled); 
//
//        try (FileOutputStream output= new FileOutputStream(tmpDir.resolve(observedFilenameXml).toFile())) {
//            JaxbUtil.write(vtsto, output, VariationTupleSetTO.class);
//        } catch (JAXBException e) {
//            Assert.fail("Cannot serialize variation tupe set to '"+observedFilenameXml+"'\n" + e.toString());
//        }
//    }
//
//    @Test
//    public void shouldMarshalBuilder() throws IOException {
//        
//        VariationTupleSetBuilder vtsb= build();
//
//        vtsb.prettyPrint();
//
//        String observedFilenameXml= "observedBuilder1.xml";
//        VariationTupleSetDescriptorTO vtsdto= Transformer.marshalBuilderToVariationTupleSetDescriptor(vtsb);
//
//        System.out.println("Size: "+vtsdto.size());
//        VariationTupleSetBuilder vtsbUnmarshalled= Transformer.unmarshalVariationTupleSetDescriptorToBuilder(vtsdto);
//
//        vtsbUnmarshalled.prettyPrint();
//        
//        try (FileOutputStream output= new FileOutputStream(tmpDir.resolve(observedFilenameXml).toFile())) {
//            JaxbUtil.write(vtsdto, output);
//        } catch (Throwable e) {
//            Assert.fail("Cannot serialize variation tupe set to '"+observedFilenameXml+"'\n" + e.toString());
//        }
//        
//        VariationTupleSet vtsExpected= vtsb.toSet();
//        VariationTupleSet vtsObserved= vtsbUnmarshalled.toSet();
//        Assert.assertEquals("Variation tuple set should be the same after marshalling and unmarshalling the builder", vtsExpected, vtsObserved); 
//
//        vtsObserved.prettyPrint();
//        
//    }
    
    }
