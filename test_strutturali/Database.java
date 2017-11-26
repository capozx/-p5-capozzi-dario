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



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Executors;

/**
 * The class Database is used to permit the communication between the
 * software and the SQLite database. It makes available all the 
 * necessary methods to insert, delete, update or select the
 * elements which are contained in the database.
 */

public class Database {
	private Connection connection;	// connection to the database
	private String path;

	public Database(String path) throws Exception {
		Class.forName("org.sqlite.JDBC");
		this.connection = DriverManager.getConnection(path);
		this.path = path;
	}

	/**
	 * The aim of this method is to return all the models in 
	 * the table that contains the elements of FileType 
	 * "fileType".
	 * 
	 * @param fileType
	 * @return ArrayList<Model>
	 * @throws Exception
	 */
	
	public ArrayList<Model> retrieveFromTable(FileType fileType)
			throws Exception {
		ArrayList<Model> data = new ArrayList<Model>();

		String query = "SELECT * FROM " + Database.getTableName(fileType);
		
		ResultContainer rw = this.executeQuery(query);

		ResultSet rs = rw.getRs();
		/*
		 * Now we extract the values of the attributes 
		 * from the database.
		 */
		while (rs.next()) {
			
			Integer id = rs.getInt("ID");
			String name = rs.getString("NAME");
			Integer checked = rs.getInt("CHECKED");
			String path = "";
			path = rs.getString("PATH");

			
			Model model = new Model(id, name, path, checked == 1, fileType);
			
			data.add(model);
		}

		rw.close();
		
		// data contains all the models of FileType "fileType".
		return data;
	}

	
	/**
	 * The aim of this method is to insert an element named "name", 
	 * located at path "path" in the table relative to the FileType
	 * "fileType".
	 * 
	 * @param name
	 * @param path
	 * @param fileType
	 * @throws Exception
	 */
	
	public void insertIntoTable(String name, String path, FileType fileType)
			throws Exception {
		String query = "INSERT INTO " + Database.getTableName(fileType);

		/*
		 * "path" represents the file path if fileType is different
		 * from FileType.CONFIGURATION; differently, "path" is 
		 * replaced with the selected EC id.
		 */
		
		query = query + " VALUES(null,'" + name + "','" + path + "',0)";

		this.executeUpdate(query);
	}

	
	/**
	 * The aim of this method is to remove an element named "name"
	 * from the table relative to "fileType".
	 * 
	 * @param name
     * @param fileType
	 * @throws Exception
	 */
	
	public boolean removeFromTable(String name, FileType fileType)
			throws Exception {
		
		String countQuery = "SELECT COUNT(*) AS c FROM "
				+ Database.getTableName(fileType) + " WHERE NAME = '" + name + "'";
		
		ResultContainer rc = this.executeQuery(countQuery);

		ResultSet rs = rc.getRs();
		
		int count = -1;
		while (rs.next()) {
			count = rs.getInt("c");
		}
		
		rc.close();
		
		if (count <= 0)
			return false;
		
		String deletionQuery = "DELETE FROM " + Database.getTableName(fileType)
				+ " WHERE NAME ='" + name + "'";
		this.executeUpdate(deletionQuery);
		
		return true;
	}

	/**
	 * The aim of this method is to execute the query 
	 * indicated by the string named "query".
	 * 
	 * @param query
	 * @return ResultSet
	 * @throws SQLException
	 */
	
	private ResultContainer executeQuery(String query) throws SQLException {
		PreparedStatement pst = this.connection.prepareStatement(query);
		ResultSet rs = pst.executeQuery();
		return new ResultContainer(pst, rs);
	}
	
	/**
	 * The aim of this method is to execute the update 
	 * query indicated by the string named "query".
	 * 
	 * @param query
	 * @throws SQLException
	 */

	private void executeUpdate(String query) throws SQLException {
		PreparedStatement pst = this.connection.prepareStatement(query);
		pst.executeUpdate();
		pst.close();
	}

	/**
	 * The aim of the following method is to get the table
	 * name connected to a specific "fileType".
	 * 
	 * @param fileType
	 * @return String
	 * @throws Exception
	 */
	
	public static String getTableName(FileType fileType) {
		switch (fileType) {
		case TEST:
			return "TEST_SET_TABLE";
		default:
			return "";
		}
	}
	
	/**
	 * The aim of the following method is to set the value 
	 * of the database field "CHECKED" to the value contained 
	 * in clicked executing an update query in the appropriate
	 * table according to "fileType".
	 * 
	 * @param selectedName
	 * @param clicked
	 * @param fileType
	 * @throws Exception
	 */
	
	public void updateClicked(String selectedName, int clicked,
			FileType fileType) throws Exception {
		String whereClause = "";
		if (!selectedName.equals("*"))
			whereClause = " WHERE NAME = '" + selectedName + "'";
		String q = "UPDATE " + getTableName(fileType) + " SET CHECKED = "
				+ clicked + whereClause;
		this.executeUpdate(q);
	}

	
	/**
	 * The aim of this method is to retrieve only those models 
	 * that have the database field "CHECKED" set to "1"
	 * from the table associated to FileType "fileType".
	 * 
	 * @param fileType
	 * @return ArrayList<Model>
	 * @throws Exception
	 */
	
	public ArrayList<Model> getClickedModels(FileType fileType)
			throws Exception {
		ArrayList<Model> data = new ArrayList<Model>();

		String query = "SELECT * FROM " + Database.getTableName(fileType)
				+ " WHERE CHECKED = 1 ";
		ResultContainer rw = this.executeQuery(query);

		ResultSet rs = rw.getRs();
		
		while (rs.next()) {
			
			/*
			 *  Extraction of attributes from
			 *  the database
			 */
			
			Integer id = rs.getInt("ID");
			String name = rs.getString("NAME");
			String path = "";
			path = rs.getString("PATH");
		
			
			/*
			 *  Create and add the new fileType model
			 */
			
			Model model = new Model(id, name, path, true, fileType);
			data.add(model);
		}

		rw.close();
		return data;
	}
	
	/**
	 * The aim of this method is simply to close
	 * the connection to the database.
	 * 
	 * @throws SQLException
	 */
	
	public void closeConnection() throws SQLException {
		this.connection.abort(Executors.newSingleThreadExecutor());
		this.connection.close();
	}
	
	public void connectionValidator() throws SQLException {
		if(!this.connection.isValid(1000))
			this.connection = DriverManager.getConnection(path);	
	}
}
