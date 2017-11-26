

import java.util.ArrayList;


public class MainLauncher extends Thread {

	public void run(){
		//System.out.println("MainLauncher started.");
		try {
			String[] args = new String[1];
			args[0] = "debug";
			Main.main(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Model> getClicked(FileType fileType) throws InterruptedException{
		//System.out.println("Calling Main.getClicked() from MainLauncher()");
		return Main.getClicked(fileType);
	}
	
	
	public int getModelQuantity(FileType fileType) throws Exception{
		return Main.getModelQuantity(fileType);
	}
	
	
	public Database getDatabase(){
		return Main.getDatabase();
	}
	
	public void testClickedTestSet() throws Exception{
		Main.testClickedTestSet();
	}
	
	public void testAddTestSet() throws Exception{
		Main.testAddTestSet();
	}
	
	public void testRemoveTestSet(int answer) throws Exception{
		Main.testRemoveTestSet(answer);
	}
	
	public void testClickedOutputReport(String path) throws Exception {
		Main.testClickedOutputReport(path);
	}
	
	public void setNotReady() {
		Main.setNotReady();
	}
	
	public void closeDBConnection() throws Exception{
		Main.closeDBConnection();
	} 
}
