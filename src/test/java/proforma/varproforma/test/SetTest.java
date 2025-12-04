package proforma.varproforma.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import proforma.varproforma.CVListVp;

public class SetTest {
	
    private static String fixtureFolder; // here we find source files

	@BeforeClass
	public static void setupClass() {
		// source folder
		fixtureFolder= SetTest.class.getPackage().getName();
		fixtureFolder += ".for"+SetTest.class.getSimpleName();
		fixtureFolder= fixtureFolder.replace(".", File.separator);		
	}
	
	
	private void shouldMarshal(String setFilename, String expectedOutputFilename) throws Exception {
		Charset encoding= StandardCharsets.UTF_8;
		File fixtureFile= new File(Defaults.RESOURCE_FOLDER+File.separator+fixtureFolder+File.separator+setFilename);

		
        CVListVp set= XmlUtil.unmarshalToObject(new BufferedInputStream(new FileInputStream(fixtureFile.getAbsolutePath())), CVListVp.class);
		
		ByteArrayOutputStream baos= new ByteArrayOutputStream();
		PrintStream out= new PrintStream(baos, true, encoding);
		set.prettyPrint(out);
		String observed= baos.toString(encoding);
		
		File expectedOutputFile= new File(Defaults.RESOURCE_FOLDER+File.separator+fixtureFolder+File.separator+expectedOutputFilename);
		String expected= Files.readString(expectedOutputFile.toPath().toAbsolutePath(), encoding);
		
		Assert.assertEquals("", expected, observed);
	}
	
	@Test
	public void shouldMarshal1() throws Exception {
		shouldMarshal("set1.xml", "pretty1.txt");
	}
	

}
