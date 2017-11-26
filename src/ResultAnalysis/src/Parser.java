

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This class does the parsing of an input file to make available to other
 * classes an HashMap used in the analysis process.
 * @author Dario Capozzi, Giulio Tavella
 *
 */

public class Parser {
	
	private String inputFilePath;
	private File inputFile;

	public Parser(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}
	
	/**
	 * Returns an HashMap that has the input command as a key and the result of
	 * that command as a value. This HashMap is filled parsing the inputFile 
	 * given in class' constructor.
	 * @return
	 */
	
	public HashMap<String, ArrayList<String>> parseFile(){
		try{
			HashMap<String, ArrayList<String>> result = 
					new HashMap<String, ArrayList<String>>();
			inputFile = new File(inputFilePath);
			ArrayList<String> executionResult = new ArrayList<String>();
			String executionString = new String("");
			Scanner scanner = new Scanner(inputFile, "UTF-8");
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				if(!line.isEmpty()){
					if(isNumeric(line.substring(0,1)) || containPEP(line)){
						executionResult.add(line);
					} else {
						if(!executionString.isEmpty()){
							result.put(executionString, executionResult);
							executionString = new String(line);
							executionResult = new ArrayList<String>();
						} else {
							executionString = new String(line);
						}
					}
				}
			}
			scanner.close();
			return result;
		} catch (Exception e){
			return null;
		}
	}
	
	/**
	 * Checks if the input String s is a number.
	 * @param s
	 * @return
	 */
	
	private boolean isNumeric(String s) {  
	    return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
	}  
	
	/**
	 * Checks if the input String line contains a PEP. 
	 * @param line
	 * @return
	 */
	
	private boolean containPEP(String line){
		for(int i = 0; i < PEP.values().length; i++){
			if(line.toUpperCase().contains(
				PEP.values()[i].toString())){
				return true;
			}
		}
		return false;
	}
}


