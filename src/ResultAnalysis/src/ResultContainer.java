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



import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class provides an interface to close PreparedStatement
 * and ResultSet objects after they have been used to query
 * the database.
 */

public class ResultContainer {

	private PreparedStatement pst;
	private ResultSet rs;
	
	public ResultContainer(PreparedStatement pst, ResultSet rs){
		this.pst = pst;
		this.rs = rs;
	}
	
	public ResultSet getRs() {
		return rs;
	}

	public void close() throws SQLException{
		pst.close();
		rs.close();
	}
}
