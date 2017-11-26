

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class UC17 {
	private static DatabaseCreator testDB;
	private static MainLauncher ml;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testDB = new DatabaseCreator("testDB");
		testDB.create();
		testDB.fillDatabase();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ml.closeDBConnection();
		testDB.delete();
	}

	@Test
	public void test() throws Exception {
		// starting UC17 test - alternative scenario 2a
		System.out.println("Running UC17 test.");
		ml = new MainLauncher();
		ml.setNotReady();
		ml.start();
		/*
		 * no-write-dir is a directory in which nobody has the write right.
		 * Then an IOException is expected to be launched, so the analysis
		 * process must fail.
		 * If this is true, no analysis file must exist.
		 */
		File analysisOutput = new File("data/output/no-write-dir/analysis.txt");	
		ml.testClickedOutputReport("data/output/no-write-dir/output.txt");
		assertFalse(analysisOutput.exists());
		
		Thread.sleep(5000);
		
		// starting UC17 test - main scenario
		/*
		 * Correct input and output paths. 
		 * Analysis process must succeed.
		 * If this is true, an analysis file must exist.
		 */
		analysisOutput = new File("data/output/analysis.txt");
		ml.testClickedOutputReport("data/output/output.txt");
		Thread.sleep(10000);
		assertTrue(analysisOutput.exists());
		/*
		 * Cleaning up the analysis file of the previous test.
		 * If this fails, the test must fails accordingly, because
		 * further testing check for existence of analysis file,
		 * and if a file exists before the test starts, the final result 
		 * can't be reliable.
		 */
		if(!analysisOutput.delete())
			fail("Cleanup failed. Test can't go further due to "
					+ "inconsistencies");
		
		// starting UC17 test - alternative scenario 3a
		/*
		 * The given file to be analyzed doesn't contains any PEP value.
		 * This must force the ACO to write into the analysis file a warning
		 * about the fact that no PEP value was found.
		 */
		analysisOutput = new File("data/output/analysis.txt");
		ml.testClickedOutputReport("data/output/no-pep-output.txt");
		Thread.sleep(10000);
		/*
		 *  Checking for the existence of analysis output file.
		 *  Then looking if it contains the warning message.
		 */
		assertTrue(analysisOutput.exists());
		List<String> lines = Files.readAllLines(analysisOutput.toPath());
		String warningMessage = "Warning! No PEP detected in provided file to be analyzed.";
		boolean containsWarningMessage = false;
		for(String line : lines){
			if(line.contains(warningMessage)){
				containsWarningMessage = true;
				break;
			}
		}
		assertTrue(containsWarningMessage);
		analysisOutput.delete();
		System.out.println("UC17 test successfully done.");
	}
	

}
