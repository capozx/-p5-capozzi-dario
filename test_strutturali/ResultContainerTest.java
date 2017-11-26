

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class ResultContainerTest {
	private static ResultContainer rc;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("Running ResultContainerTest.");
		rc = new ResultContainer(null,null);	
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("ResultContainerTest successfully done.");
	}
	
	@Test
	public final void testGetRs() throws SQLException {
		assertNull(rc.getRs());
	}
	
}
