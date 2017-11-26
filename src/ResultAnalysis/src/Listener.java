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



import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * The class Listener is a superclass of listeners. 
 * It implements table model listeners and provide methods 
 * to manage the events of the graphic elements of the table model
 * which the listener is attached to.
 */

public class Listener implements TableModelListener {
	protected boolean externalModify;
	protected FileManager fileManager;
	protected String selectedElement, table;
	protected TableModel model;
	protected FileType fileType;
	protected int row, column;
	
	public Listener(FileManager fileManager, FileType fileType) throws Exception {
		this.fileManager = fileManager;
		this.fileType = fileType;
		
		externalModify = true;
		selectedElement = null;
		this.table = Database.getTableName(fileType);
	}
	
	public String getSelectedElement() {
		return selectedElement;
	}
	
	/**
	 * The following method simply overrides the tableChanged() 
	 * callback method.
	 * 
	 * @param e
	 */
	
	@Override
	public void tableChanged(TableModelEvent e) {
		try {
			tableChangedHandler(e);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	/**
	 * The aim of this method is to retrieve the element just checked
	 * and to update the fileManager and the database.
	 * 
	 * @param e
	 * @return boolean
	 * @throws Exception
	 */
	public boolean tableChangedHandler(TableModelEvent e) throws Exception {
		if (!externalModify)
			return false;
		
		row = e.getFirstRow();
		column = e.getColumn();

		model = (TableModel) e.getSource();
		selectedElement = (String) model.getValueAt(row, 1);
		
		boolean checked = (boolean) model.getValueAt(row, column);
		
		// Sets the checked fileType element in the fileManager
		fileManager.setClicked(fileType, row, checked);
		
		return true;
	}
	
	/**
	 * The following methods are used to check or modify the 
	 * state of the lock "internalModify".
	 */
	
	public void setLock() {
		externalModify = false;
	}

	public void unsetLock() {
		externalModify = true;
	}

	public boolean isLocked() {
		return externalModify;
	}
}
