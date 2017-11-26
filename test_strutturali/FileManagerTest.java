

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class FileManagerTest {
	private static DatabaseCreator databaseCreator;
	private static Database database;
	private static FileManager fileManager;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("Running FileManagerTest.");
		String name = "FileManagerTest.sqlite";
		String path = "jdbc:sqlite:db" + File.separator + name;
		
		databaseCreator = new DatabaseCreator(name);
		databaseCreator.create();
		databaseCreator.fillDatabase();
		
		database = new Database(path);

		fileManager = new FileManager(database);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		database.closeConnection();
		databaseCreator.delete();
		System.out.println("FileManagerTest successfully done.");
	}
	
	/**
	 * 
	 */
	
	@Test
	public final void testGetIterator() {
		assertNotNull(fileManager.getIterator(FileType.TEST));
	}
	
	/**
	 * Test a function that returns the number of models in the fileType table
	 */

	@Test
	public final void testGetArraySize() {
		int size = fileManager.getArraySize(FileType.TEST);
		
		Iterator<Model> it = fileManager.getIterator(FileType.TEST);
		int count = 0;
		
		while (it.hasNext()) {
			count++;
			it.next();
		}
		
		assertEquals(count, size);
	}

	/**
	 * 
	 */

	@Test
	public final void testGetElement() {
		int size = fileManager.getArraySize(FileType.TEST);
		
		for (int i = 0; i < size; i++)
			assertNotNull(fileManager.getElement(FileType.TEST, i));
		
		assertNull(fileManager.getElement(FileType.TEST, size));
		assertNull(fileManager.getElement(FileType.TEST, -1));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	
	@Test
	public final void testInsert() throws Exception {
		String name = "testInsertRemoveConfigurationName";
		String path = "testInsertRemoveConfigurationPath";
		
		int size = fileManager.getArraySize(FileType.TEST);
		
		fileManager.insert(name, path, FileType.TEST);
		
		assertEquals(fileManager.getArraySize(FileType.TEST), size + 1);
		
		Model model = fileManager.getElement(FileType.TEST, size);
		
		assertNotNull(model);
		assertEquals(model.getName(), name);
		assertEquals(model.getPath(), path);
		
		size = fileManager.getArraySize(FileType.TEST);
		
		fileManager.remove(name, FileType.TEST);
		
		assertEquals(fileManager.getArraySize(FileType.TEST), size - 1);
	}

	/**
	 * 
	 */

	@Test
	public final void testGetModelArray() {
		ArrayList<Model> model = fileManager.getModelArray(FileType.TEST);
		Iterator<Model> it = fileManager.getIterator(FileType.TEST);
		
		int i;
		for (i = 0; i < model.size(); i++) {
			assertTrue(it.hasNext());
			assertEquals(model.get(i), it.next());
		}
		
		assertTrue(!it.hasNext());
		assertEquals(model.size(), i);
	}
	
	/**
	 * Testing a function that extract the values from the table 
	 * indicated by "fileType" and put them into the ArrayList 
	 * which is dedicated to that specified type of model
	 * 
	 * @throws Exception
	 */

	@Test
	public final void testUpdateModelData() throws Exception {
		ArrayList<Model> model = fileManager.getModelArray(FileType.TEST);
		
		fileManager.setClicked(FileType.TEST, 0, true);
		fileManager.updateModelData(FileType.TEST);
		
		ArrayList<Model> newModel = fileManager.getModelArray(FileType.TEST);

		assertEquals(newModel.size(), model.size());
		
		for (int i = 0; i < newModel.size(); i++) {
			assertTrue(newModel.get(i).getName().equals(model.get(i).getName()));
		}
		
		assertEquals(newModel.get(0).getClicked(), true);
	}
	
	/**
	 * 
	 * @throws Exception
	 */

	@Test
	public final void testClicked() throws Exception {
		/*
		 * Unclicks all the configurations
		 */

		int configurationSize = fileManager.getArraySize(FileType.TEST);
		for (int i = 0; i < configurationSize; i++)
			fileManager.setClicked(FileType.TEST, i, false);
		
		/*
		 * If none of the configurations is marked as clicked,
		 * the following call to getClicked() must return null
		 */
		
		assertNull(fileManager.getClicked(FileType.TEST));
		
		fileManager.setClicked(FileType.TEST, 0, true);
		
		/*
		 * A configuration has been clicked: the following call
		 * to getClicked must return a initialized ArrayList,
		 * and its first element must result as clicked
		 */
		
		ArrayList<Model> clickedConfigurations = 
				fileManager.getClicked(FileType.TEST);
		assertNotNull(clickedConfigurations);
		assertEquals(clickedConfigurations.get(0).getClicked(), true);
		
		/*
		 * Restores the previous state. After the following call,
		 * no configuration will result as clicked.
		 */
		
		fileManager.setClicked(FileType.TEST, 0, false);
		
		/*
		 * Tries to click two configurations denoted by an out-of-bounds index,
		 * with no other configuration clicked.
		 * The following getClicked call must fail
		 */
		
		fileManager.setClicked(FileType.TEST, configurationSize, true);
		fileManager.setClicked(FileType.TEST, -1, true);
		
		assertNull(fileManager.getClicked(FileType.TEST));
	}

	
	
	/**
	 * Extract the id of the configuration "name" from CONFIGURATION_TABLE
 	 *
 	 * The method getIdByName return -1 if the 
 	 * parameter name is missing in table
	 */
	
	@Test
	public final void testGetIdByName() throws Exception {
		String name = "testGetIdByNameConfiguration";
		fileManager.insert(name, "", FileType.TEST);
		
		ArrayList<Model> model = fileManager.getModelArray(FileType.TEST);
		
		int i;
		for (i = 0; i < model.size(); i++) {
			if (model.get(i).getName().equals(name)) {
				break;
			}
		}
		
		assertTrue(i < model.size());
		
		assertEquals(fileManager.getIdByName(name, FileType.TEST),
					model.get(i).getId());
		assertEquals(fileManager.getIdByName("nonExistingConfiguration", FileType.TEST),
					-1);
	}
}
