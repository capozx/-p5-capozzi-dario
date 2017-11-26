

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class ResultComparatorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("Running ResultComparatorTest.");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("ResultComparatorTest "
				+ "successfully done.");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		/*
		 * Building an HashMap that emulate the one obtained by a serialized
		 * execution.
		 */
		HashMap<String,ArrayList<String>> analysisMap = 
				new HashMap<String,ArrayList<String>>();
		String input1 = "java -jar data/ec/EC1.jar 9.0 data/train/train1.txt data/test/test1.txt";
		String input2 = "java -jar data/ec/EC1.jar 21.0 data/train/train1.txt data/test/test1.txt";
		ArrayList<String> results1 = new ArrayList<String>();
		ArrayList<String> results2 = new ArrayList<String>();
		results1.add("0 no");
		results1.add("1 yes");
		results1.add("Precision 0.7297297297297297");
		results1.add("Accuracy 0.9");
		analysisMap.put(input1, results1);
		results2.add("1 yes");
		results2.add("1 yes");
		results2.add("Precision 0.7674418604651163");
		results2.add("Accuracy 0.5238095238095238");
		analysisMap.put(input2, results2);
		/*
		 *  instantiating ResultComparator with respect to the first 
		 *  parameter
		 */
		ResultComparator rc = new ResultComparator(0,2);
		/*
		 *  getting the entrySet from analysisMap, then an array that contains
		 *  each entry. The order inside the array is the same of insertion in
		 *  the original map (i.e. entries[0] contains (input1,results1))
		 */		
		Set<Entry<String, ArrayList<String>>> set = analysisMap.entrySet();
		Object[] entries = new Object[2];  
		entries = set.toArray(entries);
		/*
		 * Precision in results1 is greater than in results2. Then a 1 is 
		 * expected to be returned form rc.compare()
		 */
		assertTrue(rc.compare((Entry<String, ArrayList<String>>) entries[0],
				(Entry<String, ArrayList<String>>) entries[1]) == 1);
		/*
		 *  instantiating ResultComparator with respect to the second 
		 *  parameter
		 */
		rc = new ResultComparator(1,2);
		/*
		 * Accuracy in results1 is lower than in results2. Then a -1 is 
		 * expected to be returned form rc.compare()
		 */
		assertTrue(rc.compare((Entry<String, ArrayList<String>>) entries[0],
				(Entry<String, ArrayList<String>>) entries[1]) == -1);
	}

}
