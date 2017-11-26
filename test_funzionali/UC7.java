

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class UC7 {
	private static DatabaseCreator testDB;
	private static MainLauncher ml;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testDB = new DatabaseCreator("testDB");
		testDB.create();
		testDB.fillDatabase();
		System.out.println("Running UC7 test.");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ml.closeDBConnection();
		testDB.delete();
		System.out.println("UC7 test successfully done.");
	}

	@Test
	public void test() throws Exception {
		// starting UC7 test - main scenario
		ml = new MainLauncher();
		ml.setNotReady();
		ml.start();
		// getting all TestSet(s) marked as clicked (ready to be used in serialized execution process). 
		ArrayList<Model> currentTestSet = ml.getClicked(FileType.TEST);
		/* starting procedure to mark as clicked a new TestSet (different from the one contained
		 * in currentTestSet). If this procedure will be successful, the number of TestSet
		 * marked as clicked MUST be greater than before testClickedTestSet() invocation.
		 */
		ml.testClickedTestSet();
		ArrayList<Model> clickedTestSet = ml.getClicked(FileType.TEST);
		// checking if the number of TestSet marked as clicked is greater than before
		assertTrue(clickedTestSet.size() > currentTestSet.size());
	}

}
