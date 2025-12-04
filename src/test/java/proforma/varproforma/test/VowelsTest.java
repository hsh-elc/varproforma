package proforma.varproforma.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import proforma.util.div.XmlUtils;
import proforma.util.div.XmlUtils.MarshalOption;
import proforma.varproforma.CV;
import proforma.varproforma.CVListVp;
import proforma.varproforma.CVVp;
import proforma.varproforma.V;
import proforma.varproforma.VarSpecRoot;
import proforma.varproforma.Vp;
import proforma.varproforma.util.JavascriptString;
import proforma.varproforma.util.SpecContainsHelper;
import proforma.varproforma.util.SpecValueConverter;





public class VowelsTest {
    
    static final String DEFAULT_USER_INPUT= "The quick brown fox jumps over the lazy dog.";

	static Charset encoding= StandardCharsets.UTF_8;

	
    private static String fixtureFolder; // here we find source files
    
	@BeforeClass
	public static void setupClass() {
		// source folder
		fixtureFolder= VowelsTest.class.getPackage().getName();
		fixtureFolder += ".for"+VowelsTest.class.getSimpleName();
		fixtureFolder= fixtureFolder.replace(".", File.separator);		
	}
	

    @Test
    public void shouldCombineCV() {
    	Vp[] vps= { Vp.s("c"), Vp.c("v") };
    	//dim[1].setAccuracy(VpCharacter.create((char)0));
        VarSpecRoot cvs
        = VarSpecRoot.build(vps)
            .combineGroup()
                .collect("Program", "Counter", "CountVowels")    // valid values for <c>
                .collect('a','e','i','o','u') // valid values for <v>
            .endCombineGroup()
        .endBuild();

        VarSpecRoot cvs2= marshalAndUnmarshal(cvs);
        
        int i=0;
        for (String c : new String[]{ "Program", "Counter", "CountVowels" }) {
            for (char v : new char[]{ 'a','e','i','o','u' }) {
                i++;
            	CVVp cvvp= CVVp.create(V.fromValue(c, vps[0]), V.fromValue(v, vps[1]));
                assertTrue(SpecContainsHelper.contains(cvs, cvvp));
                assertTrue(SpecContainsHelper.contains(cvs2, cvvp));
            }
        }
        
        assertEquals(i, cvs.sizeLowerBound());
        assertEquals(i, cvs2.sizeLowerBound());
    }
    
    @Test
    public void shouldCombineCVT() {
    	Vp[] vps= { Vp.s("c"), Vp.c("v"), Vp.s("t") };

        VarSpecRoot cvs
        = VarSpecRoot.build(vps)
            .combineGroup()
                .collect("Program", "Counter", "CountVowels")
                .collectGroup()
                    .combineGroup().collect('a', 'e').collect("Great to see you again!", "Another great place").endCombineGroup()
                    .combine(               'i',              "Have you imagined this?")
                    .combine(               'o',              "Why should we go around?")
                    .combine(               'u',              "Wounderful tube")
                .endCollectGroup()
            .endCombineGroup()
        .endBuild();
        VarSpecRoot cvs2= marshalAndUnmarshal(cvs);

        int i=0;
        for (String c : new String[]{ "Program", "Counter", "CountVowels" }) {
            for (char v : new char[]{ 'a','e','i','o','u' }) {
                for (String t : new String[]{ "Great to see you again!", "Another great place", "Have you imagined this?", "Why should we go around?", "Wounderful tube" }) {
                    if (count(t, v) == 3) {
                        i++;
                        CVVp cvvp= CVVp.create(
                                V.fromValue(c, vps[0]), 
                                V.fromValue(v, vps[1]),
                                V.fromValue(t, vps[2]));
                        assertTrue(SpecContainsHelper.contains(cvs, cvvp));
                        assertTrue(SpecContainsHelper.contains(cvs2, cvvp));
                    }
                }
            }
        }
        assertEquals(i, cvs.sizeLowerBound());
        assertEquals(i, cvs2.sizeLowerBound());
    }
    
    
    @Test
    public void shouldDefineAndRef() {
    	Vp[] vps= { Vp.s("a"), Vp.s("b") };

        VarSpecRoot cvs= VarSpecRoot.build(vps)
            .define("id1", "a")
                .collect("a1", "a2")
            .endDefine()
            .combineGroup()
                .ref("id1")
                .collect("b1", "b2")
            .endCombineGroup()
        .endBuild();
        VarSpecRoot cvs2= marshalAndUnmarshal(cvs);

        int i=0;
        for (String a : new String[]{ "a1", "a2" }) {
            for (String b : new String[]{ "b1", "b2" }) {
            	i++;
                CVVp cvvp= CVVp.create(V.fromValue(a, vps[0]), V.fromValue(b, vps[1]));
                assertTrue(SpecContainsHelper.contains(cvs, cvvp));
                assertTrue(SpecContainsHelper.contains(cvs2, cvvp));
            }
        }

        assertEquals(i, cvs.sizeLowerBound());
        assertEquals(i, cvs2.sizeLowerBound());

        try {
            VarSpecRoot.build(vps)
                .define("id1", "a")
                    .collect("a1", "a2")
                .endDefine()
                .combineGroup()
                    .ref("idunknown")
                    .collect("b1", "b2")
                .endCombineGroup()
            .endBuild();
            Assert.fail("ref to unkown id should throw exception");
        } catch (IllegalArgumentException ex) {
            // ok
        }

    }
    
    
    
    @Test
    public void shouldCombineCVTnonEquivalent() {
    	Vp[] vps= { Vp.s("c"), Vp.c("v"), Vp.s("t") };
        VarSpecRoot cvs= VarSpecRoot.build(vps)
            .combineGroup()
                .collect("Program", "Counter", "CountVowels")
                .collectGroup()
                    .combineGroup()
                        .collect('a', 'e')
                        .collect("Great to see you again, boy!", "Another great place")
                    .endCombineGroup()
                    .combine('i', "Have you imagined this?")
                    .combineGroup().val('o').collect("Great to see you again, boy!", "Why should we go around?").endCombineGroup()
                    .combine('u', "Wounderful tube")
                .endCollectGroup()
            .endCombineGroup()
        .endBuild();
        VarSpecRoot cvs2= marshalAndUnmarshal(cvs);
        
        VarSpecRoot cvs3= VarSpecRoot.build(vps)
                .combineGroup()
                    .collect("Program", "Counter", "CountVowels")
                    .collectGroup()
                        .combineGroup().collect('a','e')    .val("Another great place")         .endCombineGroup()
                        .combineGroup().val('i')            .val("Have you imagined this?")     .endCombineGroup()
                        .combineGroup().collect('a','e','o').val("Great to see you again, boy!").endCombineGroup()
                        .combineGroup().val('o')            .val("Why should we go around?")    .endCombineGroup()
                        .combineGroup().val('u')            .val("Wounderful tube")             .endCombineGroup()
                    .endCollectGroup()
                .endCombineGroup()
            .endBuild();

        VarSpecRoot cvs4= marshalAndUnmarshal(cvs3);
        
        VarSpecRoot cvs5= VarSpecRoot.build(vps)
                .combineGroup()
                    .collect("Program", "Counter", "CountVowels")
                    .collectGroup()
                        .define("id1", "t")
                            .val("Great to see you again, boy!")
                        .endDefine()
                        .combineGroup()
                            .collect('a', 'e')
                            .collectGroup()
                                .val("Another great place")
                                .ref("id1")
                            .endCollectGroup()
                        .endCombineGroup()
                        .combine('i', "Have you imagined this?")
                        .combineGroup()
                            .val('o')
                            .collectGroup()
                                .ref("id1")
                                .val("Why should we go around?")
                            .endCollectGroup()
                        .endCombineGroup()
                        .combine('u', "Wounderful tube")
                    .endCollectGroup()
                .endCombineGroup()
            .endBuild();

        VarSpecRoot cvs6= marshalAndUnmarshal(cvs5);

        int i=0;
        for (String c : new String[]{ "Program", "Counter", "CountVowels" }) {
            for (char v : new char[]{ 'a','e','i','o','u' }) {
                for (String t : new String[]{ "Great to see you again, boy!", "Another great place", "Have you imagined this?", "Why should we go around?", "Wounderful tube" }) {
                    if (count(t, v) == 3) {
                    	i++;
                        CVVp cvvp= CVVp.create(
                                V.fromValue(c, vps[0]), 
                                V.fromValue(v, vps[1]), 
                                V.fromValue(t, vps[2]));
                        assertTrue(SpecContainsHelper.contains(cvs, cvvp));
                        assertTrue(SpecContainsHelper.contains(cvs2, cvvp));
                        assertTrue(SpecContainsHelper.contains(cvs3, cvvp));
                        assertTrue(SpecContainsHelper.contains(cvs4, cvvp));
                        assertTrue(SpecContainsHelper.contains(cvs5, cvvp));
                        assertTrue(SpecContainsHelper.contains(cvs6, cvvp));
                    }
                }
            }
        }

        assertEquals(i, cvs.sizeLowerBound());
        assertEquals(i, cvs2.sizeLowerBound());
        assertEquals(i, cvs3.sizeLowerBound());
        assertEquals(i, cvs4.sizeLowerBound());
        assertEquals(i, cvs5.sizeLowerBound());
        assertEquals(i, cvs6.sizeLowerBound());

    }
    
    

    @Test
    public void shouldCombineCVN() throws IOException {
    	Vp[] vps= { Vp.s("c"), Vp.c("v"), Vp.i("n") };
    	
        VarSpecRoot cvs= VarSpecRoot.build(vps)
            .combineGroup()
                .collect("Program", "Counter", "CountVowels")
                .collectGroup()
                    .combineGroup().collect('a', 'i').val(1).endCombineGroup()
                    .combineGroup().val('e')         .val(3).endCombineGroup()
                    .combineGroup().val('o')         .val(4).endCombineGroup()
                    .combineGroup().val('u')         .val(2).endCombineGroup()
                .endCollectGroup()
            .endCombineGroup()
        .endBuild();
        VarSpecRoot cvs2= marshalAndUnmarshal(cvs);

        File jsFile= new File(Defaults.RESOURCE_FOLDER+File.separator+fixtureFolder+File.separator+"number_v.js");
		String jsSrc= Files.readString(jsFile.toPath().toAbsolutePath(), encoding);
		jsSrc= jsSrc.replace("%TEXT%", JavascriptString.encodeURIComponent(DEFAULT_USER_INPUT));
		System.out.println(jsSrc);
		
    	VarSpecRoot cvs3= VarSpecRoot.build(vps)
                .combineGroup()
                    .collect("Program", "Counter", "CountVowels")
                    .collect('a','e','i','o','u')
                    .deriveVal(jsSrc)
                .endCombineGroup()
            .endBuild();
        VarSpecRoot cvs4= marshalAndUnmarshal(cvs3);

        int i=0;
        for (String c : new String[]{ "Program", "Counter", "CountVowels" }) {
            for (char v : new char[]{ 'a','e','i','o','u' }) {
            	i++;
                int n= count(DEFAULT_USER_INPUT, v);
                CVVp cvvp= CVVp.create(
                        V.fromValue(c, vps[0]), 
                        V.fromValue(v, vps[1]), 
                        V.fromValue(n, vps[2]));
                assertTrue(SpecContainsHelper.contains(cvs, cvvp));
                assertTrue(SpecContainsHelper.contains(cvs2, cvvp));
                assertTrue(SpecContainsHelper.contains(cvs3, cvvp));
                assertTrue(SpecContainsHelper.contains(cvs4, cvvp));
            }
        }
        assertEquals(i, cvs.sizeLowerBound());
        assertEquals(i, cvs2.sizeLowerBound());
        assertEquals(i, cvs3.sizeLowerBound());
        assertEquals(i, cvs4.sizeLowerBound());

    }    
    
    
    @Test
    public void shouldCombineCVWN() {
    	Vp[] vps= { Vp.s("c"), Vp.c("v"), Vp.c("w"), Vp.i("n") };
    	
        VarSpecRoot cvs= VarSpecRoot.build(vps)
            .combineGroup()
                .collect("Program", "Counter", "CountVowels")
                .collectGroup()
                    .combine('a', 'a', 1)
                    .combine('a', 'e', 4)
                    .combine('a', 'i', 2)
                    .combine('a', 'o', 5)
                    .combine('a', 'u', 3)
                    .combine('e', 'a', 4)
                    .combine('e', 'e', 3)
                    .combine('e', 'i', 4)
                    .combine('e', 'o', 7)
                    .combine('e', 'u', 5)
                    .combine('i', 'a', 2)
                    .combine('i', 'e', 4)
                    .combine('i', 'i', 1)
                    .combine('i', 'o', 5)
                    .combine('i', 'u', 3)
                    .combine('o', 'a', 5)
                    .combine('o', 'e', 7)
                    .combine('o', 'i', 5)
                    .combine('o', 'o', 4)
                    .combine('o', 'u', 6)
                    .combine('u', 'a', 3)
                    .combine('u', 'e', 5)
                    .combine('u', 'i', 3)
                    .combine('u', 'o', 6)
                    .combine('u', 'u', 2)
                .endCollectGroup()
            .endCombineGroup()
        .endBuild();
        VarSpecRoot cvs2= marshalAndUnmarshal(cvs);
        
        int i=0;
        for (String c : new String[]{ "Program", "Counter", "CountVowels" }) {
            for (char v : new char[]{ 'a','e','i','o','u' }) {
                for (char w : new char[]{ 'a','e','i','o','u' }) {
                	i++;
                    int n= count(DEFAULT_USER_INPUT, v, w);
                    CVVp cvvp= CVVp.create(
                            V.fromValue(c, vps[0]), 
                            V.fromValue(v, vps[1]), 
                            V.fromValue(w, vps[2]), 
                            V.fromValue(n, vps[3]));
                    assertTrue(SpecContainsHelper.contains(cvs, cvvp));
                    assertTrue(SpecContainsHelper.contains(cvs2, cvvp));
                }
            }
        }
        assertEquals(i, cvs.sizeLowerBound());
        assertEquals(i, cvs2.sizeLowerBound());
        
    }
    
    
    
    @Test
    public void shouldCombineCVWwithDerivedN() throws IOException {
    	Vp[] vps= { Vp.s("c"), Vp.c("v"), Vp.c("w"), Vp.i("n") };
    	
        

        File jsFile= new File(Defaults.RESOURCE_FOLDER+File.separator+fixtureFolder+File.separator+"number_vw.js");
		String jsSrc= Files.readString(jsFile.toPath().toAbsolutePath(), encoding);
		jsSrc= jsSrc.replace("%TEXT%", JavascriptString.encodeURIComponent(DEFAULT_USER_INPUT));
		
    	VarSpecRoot cvs= VarSpecRoot.build(vps)
                .combineGroup()
                    .collect("Program", "Counter", "CountVowels")
                    .collect('a','e','i','o','u')
                    .collect('a','e','i','o','u')
                    .deriveVal(jsSrc)
                .endCombineGroup()
            .endBuild();
        VarSpecRoot cvs2= marshalAndUnmarshal(cvs);


        
        int i=0;
        for (String c : new String[]{ "Program", "Counter", "CountVowels" }) {
            for (char v : new char[]{ 'a','e','i','o','u' }) {
                for (char w : new char[]{ 'a','e','i','o','u' }) {
                	i++;
                    int n= count(DEFAULT_USER_INPUT, v, w);
                    CVVp cvvp= CVVp.create(
                            V.fromValue(c, vps[0]), 
                            V.fromValue(v, vps[1]), 
                            V.fromValue(w, vps[2]), 
                            V.fromValue(n, vps[3]));
                    assertTrue(SpecContainsHelper.contains(cvs, cvvp));
                    assertTrue(SpecContainsHelper.contains(cvs2, cvvp));
                }
            }
        }
        assertEquals(i, cvs.sizeLowerBound());
        assertEquals(i, cvs2.sizeLowerBound());
        
    }
    
    @Test
    public void shouldCombineCNVWithDerivedW() throws IOException {
    	Vp[] vpsCNVW= { Vp.s("c"), Vp.i("n"), Vp.c("v"), Vp.c("w") };
    	Vp[] vpsCVWN= { Vp.s("c"), Vp.c("v"), Vp.c("w"), Vp.i("n") };
    	
    	
        File jsFile= new File(Defaults.RESOURCE_FOLDER+File.separator+fixtureFolder+File.separator+"vowelw_nv.js");
		String jsSrcWAfromNV= Files.readString(jsFile.toPath().toAbsolutePath(), encoding);
		jsSrcWAfromNV= jsSrcWAfromNV.replace("%TEXT%", JavascriptString.encodeURIComponent(DEFAULT_USER_INPUT));

        VarSpecRoot cvs1= VarSpecRoot.build(vpsCNVW) 
            .combineGroup()   
                .collect("Program", "Counter", "CountVowels") 
                .range(1,7)
                .collect('a','e','i','o','u')
                .deriveCollect(jsSrcWAfromNV)
            .endCombineGroup()
        .endBuild();
		VarSpecRoot cvs2= marshalAndUnmarshal(cvs1);
    	        
        VarSpecRoot cvs3= VarSpecRoot.build(vpsCVWN) // original order
            .combineGroup()    // no reordering means: inherit from surrounding set
                .collect("Program", "Counter", "CountVowels") 
                .combineGroup("n", "v", "w")     // explicitly reordered keys for the generation of a subset
                    .range(1,7)
                    .collect('a','e','i','o','u')
                    .deriveCollect(jsSrcWAfromNV)
                .endCombineGroup()
            .endCombineGroup()
        .endBuild();
		VarSpecRoot cvs4= marshalAndUnmarshal(cvs3);
	        
		int i=0;
        for (String c : new String[]{ "Program", "Counter", "CountVowels" }) {
            for (char v : new char[]{ 'a','e','i','o','u' }) {
                for (char w : new char[]{ 'a','e','i','o','u' }) {
                	i++;
                    int n= count(DEFAULT_USER_INPUT, v, w);
                    CVVp cvvp= CVVp.create(
                            V.fromValue(c, vpsCNVW[0]), 
                            V.fromValue(n, vpsCNVW[1]), 
                            V.fromValue(v, vpsCNVW[2]), 
                            V.fromValue(w, vpsCNVW[3]));
                    assertTrue(SpecContainsHelper.contains(cvs1, cvvp));
                    assertTrue(SpecContainsHelper.contains(cvs2, cvvp));
                    assertTrue(SpecContainsHelper.contains(cvs3, cvvp));
                    assertTrue(SpecContainsHelper.contains(cvs4, cvvp));
                }
            }
        }
        assertTrue(i >= cvs1.sizeLowerBound());
        assertTrue(i >= cvs2.sizeLowerBound());
        assertTrue(i >= cvs3.sizeLowerBound());
        assertTrue(i >= cvs4.sizeLowerBound());
        
        List<VarSpecRoot> cvsList= new ArrayList<>();
		
        jsFile= new File(Defaults.RESOURCE_FOLDER+File.separator+fixtureFolder+File.separator+"vowel_wg_nv.js");
		String jsSrcWGfromNV= Files.readString(jsFile.toPath().toAbsolutePath(), encoding);
		jsSrcWGfromNV= jsSrcWGfromNV.replace("%TEXT%", JavascriptString.encodeURIComponent(DEFAULT_USER_INPUT));

        jsFile= new File(Defaults.RESOURCE_FOLDER+File.separator+fixtureFolder+File.separator+"vowelv_n.js");
		String jsSrcVfromN= Files.readString(jsFile.toPath().toAbsolutePath(), encoding);
		jsSrcVfromN= jsSrcVfromN.replace("%TEXT%", JavascriptString.encodeURIComponent(DEFAULT_USER_INPUT));
		
		cvsList.add(VarSpecRoot.build(vpsCVWN) // original order
			    .combineGroup()   // no reordering means: inherit from surrounding set
			        .collect("Program", "Counter", "CountVowels") 
			        .combineGroup("n", "v", "w")     // explicitly reordered keys for the generation of a subset
			            .collect(1,2,3,4,5,6,7)
			            .collect('a','e','i','o','u')
			            .deriveCollect(jsSrcWGfromNV)
			        .endCombineGroup()
			    .endCombineGroup()
			.endBuild());
		cvsList.add(marshalAndUnmarshal(cvsList.get(cvsList.size()-1)));
	        

		cvsList.add(VarSpecRoot.build(vpsCVWN) // original order
			    .combineGroup()   // no reordering means: inherit from surrounding set
			        .collect("Program", "Counter", "CountVowels") 
			        .combineGroup("n", "v", "w")     // explicitly reordered keys for the generation of a subset
			            .range(1,7)
			            .collect('a','e','i','o','u')
			            .deriveCollect(jsSrcWGfromNV)
			        .endCombineGroup()
			    .endCombineGroup()
			.endBuild());
		cvsList.add(marshalAndUnmarshal(cvsList.get(cvsList.size()-1)));
	        
		cvsList.add(VarSpecRoot.build(vpsCVWN) // original order
			    .combineGroup()   // no reordering means: inherit from surrounding set
			        .collect("Program", "Counter", "CountVowels") 
			        .combineGroup("n", "v", "w")     // explicitly reordered keys for the generation of a subset
			            .range(1,7)
			            .deriveCollect(jsSrcVfromN)
			            .deriveCollect(jsSrcWGfromNV)
			        .endCombineGroup()
			    .endCombineGroup()
			.endBuild());
		cvsList.add(marshalAndUnmarshal(cvsList.get(cvsList.size()-1)));
	        

		List<CV> expected= new ArrayList<>();
        for (String c : new String[]{ "Program", "Counter", "CountVowels" }) {
            for (char v : new char[]{ 'a','e','i','o','u' }) {
                for (char w : new char[]{ 'a','e','i','o','u' }) {
                	if (w > v) {
	                    int n= count(DEFAULT_USER_INPUT, v, w);
	                    CV cv= new CV(
	                            V.fromValue(c, vpsCVWN[0]), 
                                V.fromValue(v, vpsCVWN[1]), 
                                V.fromValue(w, vpsCVWN[2]), 
                                V.fromValue(n, vpsCVWN[3]));
	                    CVVp cvvp= CVVp.create(cv);
	                	for (VarSpecRoot cvs : cvsList) {
		                    assertTrue("Should contain cvvp="+cvvp+" in "+cvs.prettyPrintToString(), SpecContainsHelper.contains(cvs, cvvp));
	                	}
	                    
	                    expected.add(cv);
                	}
                }
            }
        }

        for (VarSpecRoot cvs : cvsList) {
	        CVListVp vgs= SpecValueConverter.expandNode(cvs);
        	System.out.println("--------------------------------------------------------");
        	cvs.prettyPrint();
        	vgs.prettyPrint();
        	System.out.println("--------------------------------------------------------");
        	
	        assertEquals(Arrays.asList(vpsCVWN), vgs.getCVp().getVariationPoints());
	        for (CV cv : vgs.getList()){
	        	assertTrue(expected.contains(cv));
	        }
        }
    }
    
    @Test
    public void shouldCombineCVNL() throws IOException {
    	Vp[] vps= { Vp.s("c"), Vp.c("v"), Vp.i("n"), Vp.s("l") };
    	
    	File jsFile= new File(Defaults.RESOURCE_FOLDER+File.separator+fixtureFolder+File.separator+"number_v.js");
		String jsSrcN= Files.readString(jsFile.toPath().toAbsolutePath(), encoding);
		jsSrcN= jsSrcN.replace("%TEXT%", JavascriptString.encodeURIComponent(DEFAULT_USER_INPUT));
		
		
    	jsFile= new File(Defaults.RESOURCE_FOLDER+File.separator+fixtureFolder+File.separator+"vowel_l_from_n.js");
		String jsSrcL= Files.readString(jsFile.toPath().toAbsolutePath(), encoding);

        VarSpecRoot cvs1= VarSpecRoot.build(vps) 
            .combineGroup()   
                .collect("Program", "Counter", "CountVowels") 
                .collect('a','e','i','o','u')
                .deriveVal(jsSrcN)
                .deriveVal(jsSrcL)
            .endCombineGroup()
        .endBuild();
		VarSpecRoot cvs2= marshalAndUnmarshal(cvs1);
	        
		int i=0;
        for (String c : new String[]{ "Program", "Counter", "CountVowels" }) {
            for (char v : new char[]{ 'a','e','i','o','u' }) {
            	i++;
                int n= count(DEFAULT_USER_INPUT, v);
                String ell= n==1 ? "" : "s"; 
            	
                CVVp cvvp= CVVp.create(
                        V.fromValue(c, vps[0]), 
                        V.fromValue(v, vps[1]), 
                        V.fromValue(n, vps[2]), 
                        V.fromValue(ell, vps[3]));

                assertTrue(SpecContainsHelper.contains(cvs1, cvvp));
                assertTrue(SpecContainsHelper.contains(cvs2, cvvp));
            }
        }
        assertTrue(i >= cvs1.sizeLowerBound());
        assertTrue(i >= cvs2.sizeLowerBound());
    }
    
    
//    
//    
//    
//    @Test
//    public void shouldCombineCVWithInferredWN() {
//        VtsGen vtsd= VtsGen.build("c", "v", "w", "n")
//            .combineGroup()
//                .collect("Program", "Counter", "CountVowels") 
//                .combineGroup()
//                    .collect('a','e','i','o','u')
//                    .infer(this::inferWfromV)
//                    .infer(this::inferNfromVW0)
//                .endCombineGroup()
//            .endCombineGroup()
//        .endBuild();
//        Vts observed= vtsd.expand();
//            
//        Vts expected= new Vts();
//        expected.setDimensions(Arrays.asList("c", "v", "w", "n"));
//        expected.setTuples(new ArrayList<>());
//        char[] vowels= {'a','e','i','o','u'};
//        for (String c : new String[]{ "Program", "Counter", "CountVowels" }) {
//            for (int i=0; i<vowels.length; i++) { 
//                char v= vowels[i];
//                for (int k=i+1; k<vowels.length; k++) {
//                    char w= vowels[k];
//                    int n= count(DEFAULT_USER_INPUT, v, w);
//                    Vt vt= Vt.create(c, v, w, n);
//                    expected.getTuples().add(vt);
//                }
//            }
//        }
//        
//        sortAndAssertEqual("Simple vowels assignment (c,v,w,n) with w,n inferred", expected, observed);
//        assertMarshalling(vtsd);
//        
//    }
//    
//    /**
//     * @param v first vowel 
//     * @return a set with all vowels &lt;w&gt; greater than &lt;v&gt;
//     */
//    private Set<Character> inferWfromV(Character v) {
//        Set<Character> result= new HashSet<Character>(); // a set with all vowels <w> greater than <v>:
//        for (char w : new char[]{'a','e','i','o','u'})
//            if (w>v) result.add(w);
//        return result;
//    }
//    
//
//    
//    @Test
//    public void shouldCombineCVWNQlQr() {
//        Vts expected= new Vts();
//        expected.setDimensions(Arrays.asList("c", "v", "w", "n", "ql", "qr"));
//        expected.setTuples(new ArrayList<>());
//        char[] vowels= {'a','e','i','o','u'};
//        for (String c : new String[]{ "Program", "Counter", "CountVowels" }) {
//            for (int i=0; i<vowels.length; i++) { 
//                char v= vowels[i];
//                for (int k=i+1; k<vowels.length; k++) {
//                    char w= vowels[k];
//                    int n= count(DEFAULT_USER_INPUT, v, w);
//                    for (char[] qlr : new char[][]{ {'[',']'}, {'\u00AB','\u00BB'} }) {
//                        Vt vt= Vt.create(c, v, w, n, qlr[0], qlr[1]);
//                        expected.getTuples().add(vt);
//                    }
//                }
//            }
//        }
//
//        int i=0;
//        VtsGen vtsd= VtsGen.build("c", "v", "w", "n", "ql", "qr")
//            .combineGroup()
//                .collect("Program", "Counter", "CountVowels") 
//                .combineGroup()
//                    .collect('a','e','i','o','u')
//                    .infer(this::inferWfromV)
//                    .infer(this::inferNfromVW0)
//                .endCombineGroup()
//                .combineGroup()
//                    .collect('\u00AB','[')  // valid values for <ql>
//                    .infer(this::inferQRfromQL)
//                .endCombineGroup()
//            .endCombineGroup()
//        .endBuild();
//        Vts observed= vtsd.expand();
//        
//        sortAndAssertEqual("Simple vowels assignment (c,v,w,n,ql,qr) with w,n,qr inferred (variant "+i+")", expected, observed);
//        assertMarshalling(vtsd);
//        i++;
//
//
//        vtsd= VtsGen.build("c", "v", "w", "n", "ql", "qr")
//            .combineGroup()
//                .collect("Program", "Counter", "CountVowels") 
//                .combineGroup()
//                    .collect('a','e','i','o','u')
//                    .infer(this::inferWfromV)
//                    .infer(this::inferNfromVW0)
//                .endCombineGroup()
//                .collectGroup()   // valid value pairs for <ql> and <qr>
//                    .combine('\u00AB', '\u00BB')
//                    .combine('[', ']') 
//                .endCollectGroup()
//            .endCombineGroup()
//        .endBuild();
//        observed= vtsd.expand();
//
//        sortAndAssertEqual("Simple vowels assignment (c,v,w,n,ql,qr) with w,n,qr inferred (variant "+i+")", expected, observed);
//        assertMarshalling(vtsd);
//        i++;
//    }
//    
//    
//    /**
//     * @param ql left quote character
//     * @return appropriate right quote character depending on the left quote.
//     */
//    private char inferQRfromQL(Character ql) {
//        if (ql == '[') return ']';
//        if (ql == '\u00AB') return '\u00BB';
//        throw new IllegalArgumentException("Cannot infer <qr> from unexpected <ql>='"+ql+"'");
//    }
//    
//    
//
//    
//    
//    private void sortAndAssertEqual(String message, Vts expected, Vts observed) {
//        System.out.println(message);
//        System.out.println(message.replaceAll(".", "-"));
//        observed.sort();
//        expected.sort();
//        System.out.println("Expected:");
//        expected.prettyPrint();
//        System.out.println();
//        System.out.println("Observed:");
//        observed.prettyPrint();
//        System.out.println();
//        for (int i=0; i<expected.getTuples().size(); i++) {
//            Vt vtExpected= expected.getTuples().get(i);
//            Vt vtObserved= observed.getTuples().get(i);
//            Assert.assertEquals(message + " - vts mismatch at index "+i, vtExpected, vtObserved);
//        }
//        
//        Assert.assertEquals(message+" - vts not equal", expected, observed);
//        
//    }
    

    @SuppressWarnings("unused")
	private CVListVp marshalAndUnmarshal(CVListVp set) {
        System.out.println("Set:");
        set.prettyPrint(System.out);
        System.out.println();

        String s = null;
        try {
            s = XmlUtils.marshalToXml(set, new MarshalOption[] { MarshalOption.CDATA });
        } catch (Throwable e) {
            Assert.fail("Cannot marshal "+set.getClass()+". "+e);
        }
        System.out.println("Set as XML:");
        System.out.println(s);
        System.out.println();
        CVListVp setUnmarshalled= null;
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        try {
            setUnmarshalled = XmlUtils.unmarshalToObject(bytes, CVListVp.class);
        } catch (Throwable e) {
            Assert.fail("Cannot unmarshal "+set.getClass()+". "+e);
        }
        System.out.println();
        System.out.println("Set after unmarshalling:");
        setUnmarshalled.prettyPrint();
        System.out.println();
        return setUnmarshalled;

    }
    private VarSpecRoot marshalAndUnmarshal(VarSpecRoot SpecRoot) {
        System.out.println("SpecRoot:");
        SpecRoot.prettyPrint(System.out);
        System.out.println();

        String s = null;
        try {
            s = XmlUtils.marshalToXml(SpecRoot, new MarshalOption[] { MarshalOption.CDATA }, VarSpecRoot.class);
        } catch (Throwable e) {
            Assert.fail("Cannot marshal "+SpecRoot.getClass()+". "+e);
        }
        System.out.println("SpecRoot as XML:");
        System.out.println(s);
        System.out.println();
        VarSpecRoot cvsUnmarshalled= null;
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        try {
            cvsUnmarshalled = XmlUtil.unmarshalToObject(bytes, VarSpecRoot.class);
        } catch (Throwable e) {
            Assert.fail("Cannot unmarshal "+SpecRoot.getClass()+". "+e);
        }
        System.out.println();
        System.out.println("SpecRoot after unmarshalling:");
        cvsUnmarshalled.prettyPrint();
        System.out.println();
        return cvsUnmarshalled;
    }
    
//
//    
//    private void assertMarshalling(VtsGen vtsd) {
//        System.out.println("Descriptor:");
//        vtsd.prettyPrint(System.out);
//        System.out.println();
//
//        Vts vts0= vtsd.expand();
//        vts0.sort();
//        
//        ByteArrayOutputStream baos= new ByteArrayOutputStream();
//        try {
//            JaxbUtil.write(vtsd, baos);
//        } catch (Throwable e) {
//            Assert.fail("Cannot marshal "+vtsd.getClass()+". "+e);
//        }
//        byte[] bytes= baos.toByteArray();
//        String s= new String(bytes);
//        System.out.println("Descriptor as XML:");
//        System.out.println(s);
//        System.out.println();
//        ByteArrayInputStream bais= new ByteArrayInputStream(bytes);
//        VtsGen vtsdUnmarshalled= null;
//        try {
//            vtsdUnmarshalled = JaxbUtil.read(bais, VtsGen.class);
//        } catch (Throwable e) {
//            Assert.fail("Cannot unmarshal "+vtsd.getClass()+". "+e);
//        }
//        Vts vts1= vtsdUnmarshalled.expand();
//        vts1.sort();
//        System.out.println("Before marshalling:");
//        vts0.prettyPrint();
//        System.out.println();
//        System.out.println("After unmarshalling:");
//        vts1.prettyPrint();
//        System.out.println();
//        System.out.println("Descriptor after unmarshalling:");
//        vtsdUnmarshalled.prettyPrint();
//        System.out.println();
//        Assert.assertEquals("VariationTupleSetDescriptorTO should expand to equal set after marshal/unmarshal",  vts0, vts1);
//    }
    
    
    private static int count(String text, char v) {
        return (int)text.chars().filter( it -> Character.toLowerCase((char)it) == v ).count();
    }
    private static int count(String text, char v, char w) {
        return (int)text.chars().filter( it -> {
            char fromStream= Character.toLowerCase((char)it);
            return fromStream == v || fromStream == w; 
        }).count();
        
    }
}
