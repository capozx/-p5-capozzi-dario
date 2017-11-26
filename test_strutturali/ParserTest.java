

import static org.junit.Assert.*;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ParserTest {

	private static Parser parser;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("Running ParserTest.");
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("ParserTest successfully done.");
	}
	
	@Test
	public void testParseFile() {
		
		String fakeName = "nonExistingOutput.txt";
		parser = new Parser(fakeName);
		assertNull(parser.parseFile());
		
		String name = "data" + File.separator + "output" + File.separator + "output.txt";
		parser = new Parser(name);
		assertNotNull(parser.parseFile());
		
	}

}
