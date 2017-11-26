/*
 * Author: Dario Capozzi
 * 
 * Date: 24/06/2017  
 *
 * This software has been developed in order to allow to the user to optimize
 * an automatic classifier. The application gives the possibility to set a
 * configuration of input parameter and to decide what train and test set
 * must be used. The serialized execution of the classifier produces a
 * file which contains the output of the classifier for each execution.
 */



import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;



/**
 * Main class that launches the thread associated to the graphic interface
 * and that instantiates the objects that are shared by all the objects that
 * manage the logic of the software.
 */

public class Main {
	private static Database database;
	private static FileManager fileManager;
	private static Window window;
	private static AtomicBoolean isReady = new AtomicBoolean(false);


	public static void main(String[] args) {
		isReady.set(false);
		
		String dbName = "database.sqlite";
		if(args.length > 0 && args[0].equals("debug"))
			dbName = "testDB";
		// Try to instantiate connection to DB
		String path = "jdbc:sqlite:db" + File.separator + dbName ;
		try {
			database = new Database(path);
			fileManager = new FileManager(database);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex);
			return;
		}
	
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main.window = new Window(fileManager);
					Main.window.initialize();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		isReady.set(true);
	}
	
	public static ArrayList<Model> getClicked(FileType fileType) throws InterruptedException {
		while (!isReady.get()){
			Thread.sleep(1000);
		}
		return fileManager.getClicked(fileType);
	}
	
	public static int getModelQuantity(FileType fileType) throws Exception{
		while (!isReady.get()){
			Thread.sleep(1000);
		}
		return fileManager.getArraySize(fileType);
	}
	
	public static Database getDatabase(){
		return database;
	}
	
	public static void testClickedTestSet() throws Exception{
		while (!isReady.get()){
			Thread.sleep(1000);
		}
		window.testClickedTestSet();
		Thread.sleep(5000);
	}
	
	public static void testAddTestSet() throws Exception{
		while (!isReady.get()){
			Thread.sleep(1000);
		}
		window.testAddTestSet("data/test/newTest.txt");
	}
	
	public static void testRemoveTestSet(int answer) throws Exception{
		while (!isReady.get()){
			Thread.sleep(1000);
		}
		window.testRemoveTestSet(answer);
	}
	
	public static void testClickedOutputReport(String path) throws Exception{
		while (!isReady.get()){
			Thread.sleep(1000);
		}
		window.testClickedOutputReport(path);
	}
	
	public static void setNotReady() {
		isReady.set(false);
	}
	
	public static void closeDBConnection() throws Exception{
		while (!isReady.get()){
			Thread.sleep(1000);
		}
		database.closeConnection();
	}
}
