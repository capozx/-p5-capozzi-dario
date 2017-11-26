/*
 * Author: Dario Capozzi, Giulio Tavella
 * 
 * Date: 24/06/2017  
 *
 * This software has been developed in order to allow to the user to optimize
 * an automatic classifier. The application gives the possibility to set a
 * configuration of input parameter and to decide what train and test set
 * must be used. The serialized execution of the classifier produces a
 * file which contains the output of the classifier for each execution.
 */



import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The class OpenFile manages the graphic element JFileChooser
 * instantiating and displaying it to the user. Moreover, it 
 * stores the needed information about the user selection.
 */

public class OpenFile {
	private JFileChooser fileChooser;
	private StringBuilder path;
	private StringBuilder name;

	public OpenFile(FileType fileType) {
		fileChooser = new JFileChooser();
		path = new StringBuilder();
		name = new StringBuilder();
		if(fileType == FileType.EC){
			FileNameExtensionFilter filter = new FileNameExtensionFilter(".jar", "jar");
			fileChooser.setFileFilter(filter);
		}
	}
	
	/**
	 * Open a file dialog which allows the user 
	 * to select a file from local storage
	 * 
	 * @throws FileNotFoundException
	 */
	
	public void pickMe() throws FileNotFoundException {
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();

			path.append(file.getAbsolutePath());
			name.append(file.getName());
			if (name.indexOf(".") > 0)
				name = new StringBuilder(name.substring(0, name.lastIndexOf(".")));
		}
	}
	
	/**
	 * @return the selected file path
	 */
	
	public String getPath() {
		return path.toString();
	}
	
	/**
	 * @return the selected file name
	 */
	
	public String getName() {
		return name.toString();
	}

	@Override
	public String toString() {
		return "OpenFile [fileChooser=" + fileChooser + ", path=" + path + ", name=" + name + "]";
	}
}
