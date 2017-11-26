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




import java.io.File;

/**
 * The class Model provide a representation for every file type used in 
 * the software (those specified in the source file "FileType.java")
 */

public class Model {
	
	private int id;
	private String name, path;
	private boolean clicked;
	private FileType fileType;

	public Model(int id, String name, String path, boolean clicked, FileType fileType) {
		this.id = id;
		this.name = name;
		this.path = path;
		this.clicked = clicked;
		this.fileType = fileType;
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public boolean getClicked() {
		return clicked;
	}
	
	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}

	public FileType getFileType() {
		return fileType;
	}
	
	public boolean exist(){
		return new File(path).isFile();
	}

	@Override
	public String toString() {
		return fileType.name() + " [id = " + id + ", name =" + name + ", path = "
							   + path + ", clicked = " + clicked + "]";
	}
}
