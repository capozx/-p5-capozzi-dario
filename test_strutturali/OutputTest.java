

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;



public class OutputTest {
	
	private static Output output;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("Running OutputTest.");
		output = new Output();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("OutputTest successfully done.");
		new File("test-dir" + File.separator + "analysis.txt").delete();
		new File("test-dir").delete();
		new File("analysis.txt").delete();
	}
	
	@Test
	public void testSetGetAnalysisResult() {
		ArrayList<ArrayList<String>> analysisResult = 
				new ArrayList<ArrayList<String>>();
		output.setAnalysisResult(analysisResult);
		boolean result = output.getAnalysisResult() != null &&
				output.getAnalysisResult() == analysisResult;
		assertTrue(result);
	}

	@Test
	public void testSetGetOutputFileFolder() {
		String path = "a" + File.separator + "path";
		output.setOutputFileFolder(path);
		boolean result = output.getOutputFileFolder() != null &&
				output.getOutputFileFolder() == path;
		assertTrue(result);
	}

	@Test
	public void testWriteToFile() {
		/*
		 * Setting an appropriate argument for setAnalysisResult
		 * and a void path, in order to get the resulting analysis
		 * file in the same directory of ACO runnable file.
		 */
		ArrayList<ArrayList<String>> analysisResult = 
				new ArrayList<ArrayList<String>>();
		output.setAnalysisResult(analysisResult);
		String path = "";
		output.setOutputFileFolder(path);
		try {
			output.writeToFile();
		} catch (IOException e) {
			fail("Problem when writing to specified path.");
		}
		assertTrue(new File(path + "analysis.txt").exists());
		/*
		 *  setting as outputFileFolder a non-existing directory that has to be
		 *  created by writeToFile().
		 */
		path = "test-dir";
		output.setOutputFileFolder(path);
		try {
			output.writeToFile();
		} catch (IOException e) {
			fail("Problem when writing to specified path.");
		}
		assertTrue(new File(path + File.separator + "analysis.txt").exists());
		/*
		 * Adding data to repeat the test with a non-empty-set of data to be
		 * written.
		 */
		ArrayList<String> randomData = new ArrayList<String>();
		randomData.add("21");
		analysisResult.add(randomData);
		randomData.add("118");
		analysisResult.add(randomData);
		randomData.add("345212121");
		analysisResult.add(randomData);
		output.setAnalysisResult(analysisResult); 
		path = "";
		output.setOutputFileFolder(path);		
		try {
			output.writeToFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Problem when writing to specified path.");
		}
		assertTrue(new File(path + "analysis.txt").exists());
	}

}
