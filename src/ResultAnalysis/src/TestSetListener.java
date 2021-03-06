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



public class TestSetListener extends Listener {
	public TestSetListener(FileManager fileManager) throws Exception {
		super(fileManager, FileType.TEST);
	}
}
