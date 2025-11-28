package org.proforma.variability.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.proforma.variability.transfer.CVListVp;
import org.proforma.variability.transfer.VarSpecRoot;
import org.proforma.variability.util.SpecValueConverter;

import proforma.util.div.XmlUtils;
import proforma.util.div.XmlUtils.MarshalOption;




public class CVSpecTest {
	
    private static String fixtureFolder; // here we find source files
    private static Path tmpDir;
    
	@BeforeClass
	public static void setupClass() {
		// source folder
		fixtureFolder= CVSpecTest.class.getPackage().getName();
		fixtureFolder += ".for"+CVSpecTest.class.getSimpleName();
		fixtureFolder= fixtureFolder.replace(".", File.separator);		

		// working folder
		try {
			tmpDir= Files.createTempDirectory(CVSpecTest.class.getSimpleName());
		} catch (IOException e) {
			Assert.fail("Preparation of "+CVSpecTest.class+": cannot create temp dir: "+e);
		}

	}
	
	private void shouldUnmarshalAndExpandToSet(String descriptorFilename, String expectedOutputFilename) throws Exception {
		Charset encoding= StandardCharsets.UTF_8;
		File fixtureFile= new File(Defaults.RESOURCE_FOLDER+File.separator+fixtureFolder+File.separator+descriptorFilename);

		VarSpecRoot cvs= XmlUtil.unmarshalToObject(new BufferedInputStream(new FileInputStream(fixtureFile.getAbsolutePath())), VarSpecRoot.class);
		System.out.println("CVSpec:");
		cvs.prettyPrint(System.out);
		System.out.println();
		CVListVp set= SpecValueConverter.expandNode(cvs);
		set.sort();
		marshalAndUnmarshal(set);
		
		ByteArrayOutputStream baos= new ByteArrayOutputStream();
		PrintStream out= new PrintStream(baos, true, encoding);
		set.prettyPrint(out);
		String observed= baos.toString(encoding);
		
		File expectedOutputFile= new File(Defaults.RESOURCE_FOLDER+File.separator+fixtureFolder+File.separator+expectedOutputFilename);
		String expected= Files.readString(expectedOutputFile.toPath().toAbsolutePath(), encoding);

		if (!expected.equals(observed)) {
			Path observedOutputFile= tmpDir.resolve("observed_"+expectedOutputFilename);
			Files.writeString(observedOutputFile.toAbsolutePath(), observed, encoding);
//		System.out.println("diff \""+expectedOutputFile.getAbsolutePath()+"\" \""+observedOutputFile+"\"");
			Assert.assertEquals("unexpected pretty print output (expected is in file '"+expectedOutputFile.getAbsolutePath()+"', observed in '"+observedOutputFile+"')", expected, observed);
		}
	}
	
	@Test
	public void shouldUnmarshalAndExpandToSet1() throws Exception {
		shouldUnmarshalAndExpandToSet("setDescriptor1.xml", "pretty1.txt");
	}
	@Test
	public void shouldUnmarshalAndExpandToSet2() throws Exception {
		shouldUnmarshalAndExpandToSet("setDescriptor2.xml", "pretty2.txt");
	}
	
	

    @SuppressWarnings("unused")
	private CVListVp marshalAndUnmarshal(CVListVp set) {
        System.out.println("Set:");
        set.prettyPrint(System.out);
        System.out.println();

        String s = null;
        try {
            s = XmlUtils.marshalToXml(set, new MarshalOption[] { MarshalOption.CDATA }, CVListVp.class);
        } catch (Throwable e) {
            Assert.fail("Cannot marshal "+set.getClass()+". "+e);
        }
        System.out.println("Set as XML:");
        System.out.println(s);
        System.out.println();
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        CVListVp setUnmarshalled= null;
        try {
            setUnmarshalled = XmlUtil.unmarshalToObject(b, CVListVp.class);
        } catch (Throwable e) {
            Assert.fail("Cannot unmarshal "+set.getClass()+". "+e);
        }
        System.out.println();
        System.out.println("Set after unmarshalling:");
        setUnmarshalled.prettyPrint();
        System.out.println();
        return setUnmarshalled;

    }
	
}
