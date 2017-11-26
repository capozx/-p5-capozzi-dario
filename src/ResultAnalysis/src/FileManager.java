/*
 * Author: Dario Capozzi, Alessandro Mantovani, Roberto Ronco, Giulio Tavella
 * 
 * Date: 24/06/2017  
 * 
 * This software has been developed in order to allow to the user to optimize
 * an automatic classifier. The application gives the possibility to set a
 * configuration of input parameter and to decide what train and test set
 * must be used. The serialized execution of the classifier produces a 
 * file which contains the output of the classifier for each execution.
 */


import java.util.ArrayList;
import java.util.Iterator;


/**
 * The class FileManager is used to implement the communication between
 * the database and the logic of the software. It mantains some useful
 * information about the models present in the system.
 */

public class FileManager {
	private Database database;
	private ArrayList<ArrayList<Model>> array;

	public FileManager(Database database) throws Exception {
		this.database = database;
		array = new ArrayList<ArrayList<Model>>();
		
		/*
		 * Four ArrayList<Model> are created here, one for 
		 * each fileType.
		 */
		
		for (FileType fileType : FileType.values()) {
			array.add(fileType.ordinal(), new ArrayList<Model>());
		}
		updateModelData(FileType.TEST);
	}
	
	
	/**
	 * The aim of this method is to return an iterator
	 * for the ArrayList<Model> corresponding to
	 * the FileType "fileType".
	 * 
	 * @param fileType
	 * @return Iterator<Model>
	 */

	public Iterator<Model> getIterator(FileType fileType) {
		return array.get(fileType.ordinal()).iterator();
	}
	
	public ArrayList<Model> getModelArray(FileType fileType) {
		ArrayList<Model> model = new ArrayList<Model>();
		Iterator<Model> it = getIterator(fileType);
		while (it.hasNext())
			model.add(it.next());
		return model;
	}
	
	/**
	 * The aim of this method is to return the size 
	 * of the ArrayList<Model> containing the 
	 * models of fileType "fileType".
	 * 
	 * @param fileType
	 * @return Iterator<Model>
	 */

	public int getArraySize(FileType fileType) {
		return array.get(fileType.ordinal()).size();
	}

	/**
	 * The aim of this method is to return the model of 
	 * file type "fileType" at index "index".
	 * 
	 * @param fileType
	 * @param index
	 * @return null if no model at index "index" is available,
	 * the found model otherwise
	 */

	public Model getElement(FileType fileType, int index) {
		if (index < 0 || index >= array.get(fileType.ordinal()).size())
			return null;
		return array.get(fileType.ordinal()).get(index);
	}
	
	/**
	 * The aim of this method is to modify the clicked
	 * state of the model of file type "fileType"
	 * to the boolean value "val"
	 * 
	 * @param fileType
	 * @param index
	 * @param val
	 * @throws Exception 
	 */
	
	public void setClicked(FileType fileType, int index, boolean val) 
			throws Exception {
		if (index < 0 || index >= array.get(fileType.ordinal()).size())
			return;
		
		Model m = array.get(fileType.ordinal()).get(index);
		
		m.setClicked(val);
		database.updateClicked(m.getName(), 1, fileType);
	}
	
	
	/**
	 * The aim of this method is to return an ArrayList containing 
	 * all the models of the type indicated by "fileType"
	 * that have been clicked by the user.
	 * 
	 * @param fileType
	 * @return ArrayList<Model>
	 */
	
	public ArrayList<Model> getClicked(FileType fileType) {
		ArrayList<Model> clickedModel = new ArrayList<Model>();
		ArrayList<Model> allModels = array.get(fileType.ordinal());
		
		for (Model m : allModels) {
			if (m.getClicked()) {
				clickedModel.add(m);
			}
		}
		
		if (clickedModel.size() == 0)
			return null;
		
		return clickedModel;
	}
	

	/**
	 * The aim of the following method is to extract the values  
	 * from the table indicated by "fileType" and put them 
	 * into the ArrayList which associated to the FileType 
	 * "fileType".
	 * 
	 * @param fileType
	 * @return ArrayList<Model>
	 * @throws Exception
	 */
	
	public ArrayList<Model> updateModelData(FileType fileType) throws Exception {
		ArrayList<Model> result = database.retrieveFromTable(fileType);
			
		array.set(fileType.ordinal(), result);
		
		return result;
	}
	
	
	/**
	 * The aim of this method is to wrap the Database method 
	 * to insert a model named "name", located at path "path",  
	 * of file type "fileType".
	 * 
	 * @param name
	 * @param path
	 * @param fileType
	 * @throws Exception
	 */
	
	public void insert(String name, String path, FileType fileType) throws Exception {
		database.insertIntoTable(name, path, fileType);
		this.updateModelData(fileType);
	}
	
	/**
	 * The aim of this method is to wrap the Database method 
	 * to delete a model named "name" of file type "fileType".
	 * 
	 * @param name
	 * @param fileType
	 * @throws Exception
	 */
	
	public void remove(String name, FileType fileType) throws Exception {
		database.removeFromTable(name, fileType);
		this.updateModelData(fileType);
	}
	
	
	
	/**
	 * The aim of this method is to obtain the id of
	 * an element with name "name" and of file Type 
	 * "fileType".
	 * 
	 * @param name 
	 * @param fileType
	 * @return int
	 * @throws Exception
	 */
	
	public int getIdByName(String name, FileType fileType) throws Exception {
		for (Model m : array.get(fileType.ordinal()))
			if (m.getName().equals(name))
				return m.getId();
		
		return -1;
	}
}
