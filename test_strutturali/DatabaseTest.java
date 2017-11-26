

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class DatabaseTest {
	private static Database database;
	private static DatabaseCreator databaseCreator;
	private static final FileType fileType = FileType.TEST;
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("Running DatabaseTest.");
		String name = "DatabaseTest.sqlite";
		String path = "jdbc:sqlite:db" + File.separator + name;

		databaseCreator = new DatabaseCreator(name);
		databaseCreator.create();
		databaseCreator.fillDatabase();
		database = new Database(path);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		database.closeConnection();
		databaseCreator.delete();
		System.out.println("AnalyzerTest successfully done.");
	}

	@Test
	public final void testInsertRemoveRetrieveTable() throws Exception {
		/*
		 * Retrieves the current models from the database
		 */
		
		ArrayList<Model> model = database.retrieveFromTable(fileType);
		
		/*
		 * Inserts a new model into the database
		 */
		
		String name = "newTestSetName";
		String path = "newTestSetPath";
		
		database.insertIntoTable(name, path, fileType);
		
		/*
		 * Retrieves the current models from the database, again.
		 * Now the returned ArrayList will contain the new model,
		 * as verified later
		 */
		
		ArrayList<Model> newModel = database.retrieveFromTable(fileType);
		
		/*
		 * Asserts that the models number has increased
		 * by one after insertion
		 */
		
		assertEquals(newModel.size(), model.size() + 1);
		
		/*
		 * Compares all the models before the insertion with
		 * the new models, except for the last one, which must
		 * be the result of the last insertion
		 */
		
		for (int i = 0; i < model.size(); i++)
			assertEquals(newModel.get(i).getId(), model.get(i).getId());
		
		/*
		 * Checks the equality of the last element 
		 * of newModel -supposedly the last model 
		 * inserted - with the expected name
		 */
		
		Model m = newModel.get(newModel.size() - 1);
		assertTrue(m.getName().equals(name));
		assertTrue(m.getPath().equals(path));	
		
		/*
		 * Removes the inserted model from the table
		 */
		
		assertTrue(database.removeFromTable(name, fileType));
		
		/*
		 * After the removal, the models' array must be reset,
		 * i.e. decreased by one and totally equal to the 
		 * first array of model, "model"
		 */
		
		newModel = database.retrieveFromTable(fileType);
		assertEquals(model.size(), newModel.size());
		
		for (int i = 0; i < model.size(); i++)
			assertEquals(newModel.get(i).getId(), model.get(i).getId());
		
		/*
		 * Tries to remove a non existing configuration
		 */
		
		assertFalse(database.removeFromTable("nonExistingTest", fileType));
		
	}
	
	@Test
	public final void testGetTableName() {
		assertTrue(Database.getTableName(FileType.TEST).equals("TEST_SET_TABLE"));
	}

	@Test
	public final void testUpdateClicked() throws Exception {
		/*
		 * Tests whether updating the clicked state of 
		 * a non existing model has a perceivable,
		 * disruptive effect on the execution or not
		 */
		
		ArrayList<Model> model = database.retrieveFromTable(fileType);
		
		database.updateClicked("nonExistingConfiguration", 1, fileType);
		
		ArrayList<Model> newModel = database.retrieveFromTable(fileType);
		
		assertEquals(newModel.size(), model.size());
		for (int i = 0; i < model.size(); i++) {
			assertEquals(newModel.get(i).getName(), model.get(i).getName());
			assertEquals(newModel.get(i).getClicked(), model.get(i).getClicked());
		}
		
		/*
		 * Inserts a new model, clicks and unclicks 
		 * and checks the consistency of the operations
		 */
		
		String name = "testUpdateClickedName";
		String path = "testUpdateClickedPath";
		database.insertIntoTable(name, path, fileType);
		
		database.updateClicked(name, 1, fileType);
		ArrayList<Model> clickedModels = database.getClickedModels(fileType);
		for (Model m : clickedModels)
			if (m.getName().equals(name))
				assertEquals(m.getClicked(), true);
		
		database.updateClicked(name, 0, fileType);
		clickedModels = database.getClickedModels(fileType);
		for (Model m : clickedModels)
			assertTrue(!m.getName().equals(name));
		
		/*
		 * Unclicks all the models and verifies the
		 * consistency of the operation
		 */
		
		String name2 = "testUpdateClickedName2";
		String path2 = "testUpdateClickedPath2";
		database.insertIntoTable(name2, path2, fileType);
		
		database.updateClicked("*", 0, fileType);
		clickedModels = database.getClickedModels(fileType);
		assertEquals(clickedModels.size(), 0);
		
		database.removeFromTable(name, fileType);
		database.removeFromTable(name2, fileType);
	}
	
	@Test
	public final void testGetClickedModels() throws Exception {
		String name = "testGetClickedModelsConfiguration";
		
		/*
		 * Retrieves the current array of models
		 */
		
		ArrayList<Model> model = database.getClickedModels(fileType);
		
		/*
		 * Inserts a new model into the database and clicks it
		 */
		
		database.insertIntoTable(name, "", fileType);
		database.updateClicked(name, 1, fileType);
		
		/*
		 * Retrieves the new array of models, which 
		 * must contain the newly inserted element,
		 * which must be characterized by a consistent
		 * clicked state
		 */
		
		ArrayList<Model> newModel = database.getClickedModels(fileType);
		
		/*
		 * Checks that the size of the fileType array 
		 * of models has been increased by one
		 */
		
		assertEquals(newModel.size(), model.size() + 1);
		
		/*
		 * Checks the consistency of the clicked state 
		 * of the current models with the old ones
		 */
		
		for (int i = 0; i < model.size(); i++) {
			assertEquals(newModel.get(i).getName(), model.get(i).getName());
			assertEquals(newModel.get(i).getClicked(), model.get(i).getClicked());
		}
		
		assertEquals(newModel.get(newModel.size() - 1).getClicked(), true);	
	}
	
	
	@Test
	public final void testConnection() throws Exception {
		/*
		 * Closes the current connection
		 */
		
		database.closeConnection();
		
		/*
		 * If invalid, restores a proper connection to the database
		 */
		
		database.connectionValidator();
		
		/*
		 * If the following statement fails, the 
		 * connection has not been properly reset
		 */
		
		database.retrieveFromTable(fileType);
	}
}
