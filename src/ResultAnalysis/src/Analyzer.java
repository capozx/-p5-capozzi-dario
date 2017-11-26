/*
 * 
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;


/**
 * The class Analyzer provides methods for analyze an output file that resume
 * a previous execution of the ACO. It can extract available PEP and, for each
 * of it, can check what combination of input parameter was able to give the 
 * lower and higher value of that PEP, among all other input parameter 
 * combinations. 
 * 
 * @author Dario Capozzi, Giulio Tavella
 *
 */
public class Analyzer implements Runnable {
	
	private HashMap<String, ArrayList<String>> mapOfValues;
	private ArrayList<Double> PEPValues;
	private int availablePEP; 
	private Output output;
	private String outputFilepath;
	private Object stateFlag;
	public Boolean state; 
	
	/**
	 * 
	 * @param mapOfValues
	 * @param outputFilePath
	 * @param stateFlag
	 */
	public Analyzer(HashMap<String, ArrayList<String>> mapOfValues,
			String outputFilePath, Object stateFlag){
		PEPValues = new ArrayList<Double>();
		for(int i = 0; i < PEP.values().length; i++){
			PEPValues.add(null);
		}
		this.mapOfValues = mapOfValues;
		assignAvailablePEP();
		availablePEP = 0;
		for(int i = 0; i < PEPValues.size(); i++){
			if(PEPValues.get(i) != null)
				availablePEP++;
		}
		output = new Output();
		this.outputFilepath = outputFilePath;
		this.stateFlag = stateFlag;
	}
	
	/**
	 * Returns, for each available PEP, the input command that generated the
	 * minimum and maximum of that PEP, among all other command in the analyzed
	 * file.
	 * 
	 * @return
	 */
	
	private ArrayList<ArrayList<String>> getValuesOfInterest(){
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		for(int i = 0 ; i < PEP.values().length; i++){
			if(PEPValues.get(i) != null){
				ArrayList<Entry<String,ArrayList<String>>> values = getValues(i);
				ArrayList<String> VOIRow = new ArrayList<String>();
				VOIRow.add(values.get(0).getKey());
				for(String runResult : values.get(0).getValue()){
					if(runResult.toUpperCase().contains(PEP.values()[i].toString()))
						VOIRow.add(runResult);
				}
				result.add(VOIRow);
				VOIRow = new ArrayList<String>();
				VOIRow.add(values.get(values.size()-1).getKey());
				for(String runResult : values.get(values.size()-1).getValue()){
					if(runResult.toUpperCase().contains(PEP.values()[i].toString()))
						VOIRow.add(runResult);
				}
				result.add(Integer.max(result.size()- 1, 0), VOIRow);
			}		
		}
		return result;
		
	}
	
	
	/**
	 * Returns output file results according to the PEPchoice specified,
	 * sorted by PEP.
	 * 
	 * @param PEPchoice
	 * @return
	 */
	
	private ArrayList<Entry<String,ArrayList<String>>> getValues(int PEPchoice){
		ArrayList<Entry<String,ArrayList<String>>> values = 
				new ArrayList<Entry<String,ArrayList<String>>>();
		for(Entry<String,ArrayList<String>> entry : this.mapOfValues.entrySet())
			values.add(entry);
		Collections.sort(values, new ResultComparator(PEPchoice,availablePEP));
		return values;
	}
	
	/**
	 * Retrieves the available PEP in the specified output to be 
	 * analyzed, then insert it in PEPValues ArrayList, where the position
	 * of the insertion is chosen based on what PEPValue is retrieved.  
	 * 
	 */
	
	private void assignAvailablePEP(){
		ArrayList<String> firstExecution = mapOfValues.entrySet().iterator()
				.next().getValue();
		for(String result : firstExecution){
			for(PEP pep : PEP.values()){
				if(result.toUpperCase().contains(pep.toString())){
					PEPValues.set(pep.ordinal(), 
							Double.parseDouble(result.
									substring(result.indexOf('.'))));
				}
			}
		}
	}
	
	/**
	 * Write the analysis done to a file which has 
	 * a path specified by outputFilepath
	 * @throws IOException
	 */
	
	private void writeToFile() throws IOException{
		output.setAnalysisResult(this.getValuesOfInterest());
		output.setOutputFileFolder(this.outputFilepath);
		output.writeToFile();
	}

	@Override
	public void run() {
		try {
			Thread.sleep(5000);
			writeToFile();
			Thread.sleep(5000);
			state = true;
			synchronized (stateFlag) {
				stateFlag.notifyAll();
			}
		} catch (Exception e) {
			state = false;
			synchronized (stateFlag) {
				stateFlag.notifyAll();
			}
		}
	}
	
	
}
