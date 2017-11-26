/*
 * Author: Dario Capozzi
 * 
 * Date: 22/06/2017 
 * 
 * The project is intended to allow the optimisation of automatic classifiers.
 * The software produces a file containing the value of the Performance 
 * Evaluation Parameters according to the different execution of the analysed 
 * classifier. The aim of the program is to evaluate the best set of input 
 * parameters for a specified classifier.
 * 
 */



import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * The Output class contains the total output data produced
 * by an EC (External Classifier) serial execution
 */

public class Output {
	private String outputFileFolder;
	private ArrayList<ArrayList<String>> analysisResult;
	
	public ArrayList<ArrayList<String>> getAnalysisResult() {
		return analysisResult;
	}

	public void setAnalysisResult(ArrayList<ArrayList<String>> analysisResult) {
		this.analysisResult = analysisResult;
	}

	public Output() {
		this.analysisResult = null;
		this.outputFileFolder = null;
	}
	

	public String getOutputFileFolder() {
		return this.outputFileFolder;
	}
	
	public void setOutputFileFolder(String outputFileFolder) {
		this.outputFileFolder = outputFileFolder;
	}

	/**
	 * This method allows to write the results of the serialized execution 
	 * on a well structured file specified by "outputFilePath" 
	 * 
	 * @param val
	 * @param sortedEntries
	 * @throws IOException 
	 */
	
	
	public void writeToFile() throws IOException{
		String path = "";
		if(outputFileFolder.isEmpty())
			path = "analysis.txt";
		else
			path = outputFileFolder + File.separator + "analysis.txt";
		
		File f = new File(path);
		if(!outputFileFolder.isEmpty())
			f.getParentFile().mkdirs();
		f.createNewFile();
		
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		String introduction = new String("In the following lines are shown the"
				+ " input parameter(s) that generate the minimum and maximum "
				+ "value for each available PEP.");
		writer.println(introduction);
		writer.println();
		int counter = 0;
		for(ArrayList<String> valueOfInterest : analysisResult){
			writer.println(valueOfInterest);
			counter++;
			if(counter != 0 && counter % 2 == 0){
				writer.println();
				counter = 0;
			}
		}
		if(analysisResult.isEmpty()){
			writer.print("Warning! No PEP detected in provided file to be analyzed.");
		}
		writer.close();
	}
}
