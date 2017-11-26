

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class AnalyzerTest {
	private static Analyzer analyzer;
	private static HashMap<String, ArrayList<String>> mapOfValues;
	private String outputFilepath;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("Running AnalyzerTest.");
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("AnalyzerTest successfully done.");
	}
	
	@Test
	public void testRun() {
		String name = "data" + File.separator + "output" + File.separator + "output.txt";
		Parser parser = new Parser(name);

		/*
		 * getting illegal path to write to, in order to guarantee that 
		 * writeToFile() throws an exception.
		 */
		
		outputFilepath = getIllegalPath(); 
		mapOfValues = parser.parseFile();
		Object flag = new Object();
		
		analyzer = new Analyzer(mapOfValues, outputFilepath,flag);
		analyzer.run();
		
		/*
		 *	 setting a legal path to write to.
		 */
		
		outputFilepath = "data" + File.separator + "analysis" + File.separator;
		mapOfValues = parser.parseFile();
		
		analyzer = new Analyzer(mapOfValues, outputFilepath,flag);
		analyzer.run();
		
	}
	
	
	private String getIllegalPath(){
		if(System.getProperty("os.name").toLowerCase().contains("windows"))
			return "C:"+ File.separator +"Windows" + File.separator + "System32";
		else
			return File.separator;
	}
}
