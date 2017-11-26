

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class UC8 {
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
		// starting UC8 test - main scenario
		System.out.println("Running UC8 test.");
		ml = new MainLauncher();
		ml.setNotReady();
		ml.start();
		// getting all TestSet(s) available in ACO's running environment.
		int loadedTestSet = ml.getModelQuantity(FileType.TEST);
		// starting procedure to add a new TestSet in ACO's running environment.
		ml.testAddTestSet();
		/* if the previous procedure was successfully executed, the quantity of 
		 * all TestSet(s) available in ACO's running environment MUST be greater than before 
		 * that testAddTestSet() was executed.    
		 */
		assertTrue(ml.getModelQuantity(FileType.TEST) > loadedTestSet);
		// starting UC8 test - alternative scenario 2a
		loadedTestSet = ml.getModelQuantity(FileType.TEST);
		/* starting procedure to add the same TestSet added before. This operation MUST fail,
		 * because ACO should not permit to insert the same TestSet more than once. 
		 */
		ml.testAddTestSet();
		// checking if available TestSet quantity is not changed after testAddTestSet() execution.
		assertTrue(ml.getModelQuantity(FileType.TEST) == loadedTestSet);
		System.out.println("UC8 test successfully done.");
	}
	

}
