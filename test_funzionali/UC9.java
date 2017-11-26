

import static org.junit.Assert.*;

import javax.swing.JOptionPane;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class UC9 {
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
		// starting UC9 test - main scenario
		System.out.println("Running UC9 test.");
		ml = new MainLauncher();
		ml.setNotReady();
		ml.start();
		// getting all TestSet(s) available in ACO's running environment.
		int loadedTestSet = ml.getModelQuantity(FileType.TEST);
		/* starting procedure to remove a TestSet in ACO's running environment.
		 * When ACO asks for confirmation, a positive answer is given. 
		 */
		ml.testRemoveTestSet(JOptionPane.YES_OPTION);
		/* if the previous procedure was successfully executed, the quantity of 
		 * all TestSet(s) available in ACO's running environment MUST be lower than before 
		 * that testAddTestSet() was executed.    
		 */
		assertTrue(ml.getModelQuantity(FileType.TEST) < loadedTestSet);
		// starting UC9 test - alternative scenario 2a 
		loadedTestSet = ml.getModelQuantity(FileType.TEST);
		/* starting procedure to remove a TestSet in ACO's running environment.
		 * When ACO asks for confirmation, a negative answer is given. 
		 */
		ml.testRemoveTestSet(JOptionPane.NO_OPTION);
		/* checking if available TestSet quantity is not changed after
		 * testRemoveTestSet(JOptionPane.NO_OPTION) execution.
		 */
		assertTrue(ml.getModelQuantity(FileType.TEST) == loadedTestSet);
		System.out.println("UC9 test successfully done.");
	}

}
