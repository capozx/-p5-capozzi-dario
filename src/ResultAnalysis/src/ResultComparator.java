/*
 * Author: Dario Capozzi
 * 
 * Date: 22/06/2017 
 * 
 * The project is intended to allow the optimization of automatic classifiers.
 * The software produces a file containing the value of the Performance 
 * Evaluation Parameters according to the different execution of the analyzed 
 * classifier. The aim of the program is to evaluate the best set of input 
 * parameters for a specified classifier.
 * 
 */



import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map.Entry;

/**
 * The class ResultComparator provides a method to compare the results
 * of the serialized execution. In particular it is the comparator for 
 * the Collections.sort() applied to the aforementioned results. 
 */

public class ResultComparator 
		implements Comparator<Entry<String,ArrayList<String>>> {

	private int parameterIndex;
	private int sizeOffset;
	
	public ResultComparator(int parameterIndex, int sizeOffset) {
		this.parameterIndex = parameterIndex;
		this.sizeOffset = sizeOffset;
	}
	
	@Override
	public int compare(Entry<String,ArrayList<String>> o1, 
			Entry<String,ArrayList<String>> o2) {
		String s1 = o1.getValue().get(o1.getValue().size() 
				- sizeOffset + parameterIndex);
		String s2 = o2.getValue().get(o1.getValue().size() 
				- sizeOffset + parameterIndex);		
		double val1 = Double.parseDouble(s1.substring(s1.indexOf('.')));
		double val2 = Double.parseDouble(s2.substring(s2.indexOf('.')));
				
		return Double.compare(val2, val1); 
	}
}