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



import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;



/**
 * The class Window manages the GUI which allows the user to interact with the
 * application. It provides the software core functionality:
 * - EC, test set, train set, configuration addition/removal operations.
 * - EC serial execution and output retrieval
 */

public class Window {

	private JFrame frame;
	private Listener[] listener;
	private JTable[] jTable;
	private DefaultTableModel[] defaultTableModel;
	
	private FileManager fileManager;
	private JButton testSetRemove;
	private JButton analysisButton;
	private Object stateFlag;
	private Analyzer analyzer;
	
	/**
	 * The Window constructor initializes the jTable's used to display
	 * EC, test/train set, and configuration data; furthermore, it 
	 * initializes the tables Listener's and the DefaultTableModel's
	 * for tabular display of data.
	 * 
	 * @param fileManager
	 * @param runManager
	 * @param configuration
	 * @throws Exception
	 */
	
	public Window(FileManager fileManager) throws Exception {
		this.fileManager = fileManager;

		final int modelsNumber = FileType.values().length;
		
		listener = new Listener[modelsNumber];
		jTable = new JTable[modelsNumber];
		defaultTableModel = new DefaultTableModel[modelsNumber];
	}
	
	/**
	 * The initialize() function creates the main frame, instantiates
	 * the graphic controls and attaches them to the frame.
	 * In particular, this function initializes all the buttons for
	 * managing file models (EC, test/train set, configurations), 
	 * fills tables with the model data contained in the fileManager 
	 * (derived from database queries), and starts the application GUI. 
	 * 
	 * @throws Exception
	 */

	public void initialize() throws Exception {
		
		/*
		 * Refreshes the model data
		 */
		
		fileManager.updateModelData(FileType.TEST);
		
		/*
		 * Creates the window main frame
		 */
		
		frame = new JFrame();
		frame.setMinimumSize(new Dimension(700, 400));
		frame.setBounds(100, 100, 1000, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		frame.setVisible(true);

		
		/*
		 * Creates the left panel and attaches it to the main frame
		 */
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		frame.getContentPane().add(leftPanel);

		/*
		 * Creates the run and save path panels and buttons
		 * and attaches them to the main frame
		 */
		
		JPanel executionPanel = new JPanel();
		leftPanel.add(executionPanel);
		
		
		/*
		 * Creates the analysis button, add it to execution
		 * panel and attaches a listener to the button.
		 */
		analysisButton = new JButton("Do Analysis");
		executionPanel.add(analysisButton);
		analysisButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					JFileChooser f = new JFileChooser();
					f.setFileSelectionMode(JFileChooser.FILES_ONLY);
					f.showSaveDialog(null);
					File directoryToSave = f.getSelectedFile();
					if (directoryToSave == null)
						return;
					Parser parser = new Parser(directoryToSave.toString());
					stateFlag = new Object();
					analyzer = new Analyzer(parser.parseFile(),  
							directoryToSave.getParent().toString(), stateFlag);
					Thread analyzerThread = new Thread(analyzer);
					JFrame analysisFrame = new JFrame();
					analyzerThread.start();
					
					/*
					 * Shows a information window that informs 
					 * the current user about the running analysis 
					 * and its result path.
					 */
					
					new Thread(new Runnable() {
						public void run() {
								try {
									showRunningWindow(analysisFrame, 
											directoryToSave.getParent().toString());
									synchronized(stateFlag){
										stateFlag.wait();
										if(!analyzer.state){
											JOptionPane.showMessageDialog(null,
													"Problem during the analysis of chosen file");
										}
									}
									analyzerThread.join();
									hideRunningWindow(analysisFrame);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}  
						}).start();
				} catch (Exception ee){
					ee.printStackTrace();
					JOptionPane.showMessageDialog(null,
							"Problem during the analysis of chosen file : " + ee.getMessage());	
				}
			}
		});
		
		
		/*
		 * Creates the right panel and attaches it to the main frame
		 */
		
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		frame.getContentPane().add(rightPanel);
		
		/*
		 * Creates the test set panels, buttons, and tables
		 * and attaches them to the main frame
		 */

		JPanel testSetPanel = new JPanel();
		testSetPanel.setLayout(new GridLayout(1, 0, 0, 0));
		testSetPanel.setMinimumSize(new Dimension(100, 100));
		testSetPanel.add(addScrollablePane(FileType.TEST));
		rightPanel.add(testSetPanel);

		JPanel testSetPanelButtons = new JPanel();
		testSetPanel.add(testSetPanelButtons);

		JButton testSetAdd = new JButton("+ Test");
		testSetPanelButtons.add(testSetAdd);
		testSetAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionPerformedAdd(FileType.TEST);
			}
		});

		testSetRemove = new JButton("- Test");
		testSetPanelButtons.add(testSetRemove);
		testSetRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionPerformedRemove(FileType.TEST);
			}
		});
	}

	/**
	 * @param fileType
	 * @return the appropriate jTable column name associated to fileType.
	 * @throws Exception
	 */
	
	private String getColumnName(FileType fileType) throws Exception {
		switch (fileType) {
			case TEST:
				return "TestSet";
			default:
				throw new Exception("Unrecognized file type");
		}
	}

	/**
	 * @param fileType
	 * @return the appropriate jTable listener associated to fileType.
	 * @throws Exception
	 */
	
	private Listener createListener(FileType fileType) throws Exception {
		switch (fileType) {
			case TEST:
				return new TestSetListener(fileManager);
			default:
				throw new Exception("Unrecognized file type");
		}
	}

	/**
	 * @param fileType
	 * @return the jScrollPane associated to the jTable specified by fileType,
	 * filled with appropriated data which is appropriately queried to the File
	 * Manager. 
	 * @throws Exception
	 */
	
	private JScrollPane addScrollablePane(FileType fileType) throws Exception {
		Object[] columnNames = { getColumnName(fileType), "Nome" };

		/*
		 * Fills the data matrix with the models data of file type "fileType".
		 * Each matrix row is formatted as (clicked, model name)
		 */
		
		int index = fileType.ordinal();
		int size = fileManager.getArraySize(fileType);
		Object[][] data = new Object[size][2];
		for (int i = 0; i < size; i++) {
			Model tmpModel = fileManager.getElement(fileType, i);
			data[i][0] = tmpModel.getClicked();
			data[i][1] = tmpModel.getName();
		}

		/*
		 * Prepares the table grid
		 */
		
		defaultTableModel[index] = new DefaultTableModel(data, columnNames) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 0;
			}
		};

		/*
		 * Prepares the jTable and its column types
		 */
		
		jTable[index] = new JTable(defaultTableModel[index]) {

			private static final long serialVersionUID = 1L;

			@Override
			public Class<?> getColumnClass(int column) {
				switch (column) {
				case 0:
					return Boolean.class;
				case 1:
					return String.class;
				default:
					return String.class;
				}
			}
		};
		
		/*
		 * Creates the listener and attaches it to the table
		 */
		
		listener[index] = createListener(fileType);
		defaultTableModel[index].addTableModelListener(listener[index]);
		jTable[index].setPreferredScrollableViewportSize(jTable[index].getPreferredSize());
		JScrollPane scrollPane = new JScrollPane(jTable[index], JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		return scrollPane;
	}

	/**
	 * Callback function triggered by the window's add buttons associated
	 * to the selection of a file (EC or test/train set) from local storage.
	 * 
	 * @param fileType
	 */
	
	private void actionPerformedAdd(FileType fileType) {
		try {
			OpenFile openFile = new OpenFile(fileType);
			openFile.pickMe();

			String name = openFile.getName();
			String path = openFile.getPath();
			
			if (name.isEmpty() || path.isEmpty())
				return;
			
			fileManager.insert(name, path, fileType); // updates the file manager
			this.addRow(fileType.ordinal(), name);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null,
					"Problem adding the selected " + fileType.name() + "\n" + ex.getMessage());
			//ex.printStackTrace();
		}
	}
	
	/**
	 * Callback function triggered by the window's remove buttons.
	 * This function removes the element designated for removal
	 * from the appropriate jTable and the fileManager.
	 * 
	 * @param fileType
	 */

	private void actionPerformedRemove(FileType fileType) {
		try {
			int dialogResult = JOptionPane.showConfirmDialog(null, 
					"Do you really want to delete the selected element?");
			if (dialogResult != JOptionPane.YES_OPTION)
				return;
			
			int index = fileType.ordinal();
			int rowToDelete = jTable[index].getSelectedRow();
			if (rowToDelete == -1)
				return;

			fileManager.remove(jTable[index].getModel().getValueAt(rowToDelete, 1).toString(), fileType);
			this.removeRow(index);
			
			/*
			 * If the file was an EC, then the configuration must be updated 
			 * accordingly (i.e. deleting all the - at this point - outdated
			 * configurations)
			 */
			
			if (fileType == FileType.EC) {
				for (int i = 0; i < fileManager.getArraySize(FileType.CONFIGURATION); i++) {
					listener[FileType.CONFIGURATION.ordinal()].setLock();
					defaultTableModel[FileType.CONFIGURATION.ordinal()]
							.removeRow(0);
					listener[FileType.CONFIGURATION.ordinal()].unsetLock();
				}
				fileManager.updateModelData(FileType.CONFIGURATION);
			}
			
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Problem removing the selected " + fileType.name());
			ex.printStackTrace();
		}
	}
	
	/**
	 * Adds a row with name field filled to "name" to the table specified by 
	 * "index" without firing the tableChanged callback function.
	 * 
	 * @param index
	 * @param name
	 */

	private void addRow(int index, String name) {
		listener[index].setLock();
		defaultTableModel[index].addRow(new Object[] { false, name });
		listener[index].unsetLock();
	}
	
	/**
	 * Deletes a row from the table specified by "index" 
	 * without firing the tableChanged callback function.
	 * 
	 * @param index
	 * @param name
	 */
	
	private void removeRow(int index) {
		listener[index].setLock();
		defaultTableModel[index].removeRow(jTable[index].getSelectedRow());
		listener[index].unsetLock();
	}
	
	public void testClickedTestSet() throws InterruptedException{
		ArrayList<Model> testSet = fileManager.getClicked(FileType.TEST);
		if(!testSet.isEmpty()){
			int clickedIndex = -1;
			for(int i = 0; i < jTable[FileType.TEST.ordinal()].getRowCount(); i++){
				if((boolean) defaultTableModel[FileType.TEST.ordinal()].getValueAt(i, 0)){
					clickedIndex = i;
					break;
				}
			}
			int toBeClicked = ((clickedIndex + 1) > defaultTableModel[FileType.TEST.ordinal()].getRowCount() - 1)
					? 0 : (clickedIndex + 1);
			defaultTableModel[FileType.TEST.ordinal()].setValueAt(true, toBeClicked, 0);
			defaultTableModel[FileType.TEST.ordinal()].fireTableChanged(
					new TableModelEvent(defaultTableModel[FileType.TEST.ordinal()],toBeClicked,toBeClicked,0));
		}
	}
	
	public void testClickedOutputReport(String path) throws Exception{
		scheduleAutoAnswerFileChooser(path);
		scheduleAutoAnswerJOptionPane(JOptionPane.OK_OPTION,10000);
		analysisButton.doClick();
		Thread.sleep(2000);
	}
	
	public void testAddTestSet(String path) throws Exception{
		scheduleAutoAnswerFileChooser(path);
		scheduleAutoAnswerJOptionPane(JOptionPane.OK_OPTION,10000);
		actionPerformedAdd(FileType.TEST);
		Thread.sleep(2000);
	}
	
	public void testRemoveTestSet(int answer) throws Exception{
		FileType fileType = FileType.TEST;
		int index = fileType.ordinal();
		jTable[index].setRowSelectionInterval(0, 0);
		Thread.sleep(2000);
		int rowToDelete = jTable[index].getSelectedRow();
		if (rowToDelete == -1)
			return;

		scheduleAutoAnswerJOptionPane(answer,5000);
		testSetRemove.doClick();
		
		Thread.sleep(2000);
	}
	
	private void scheduleAutoAnswerJOptionPane(int answer,int timeToWait){
		TimerTask timerTask = new TimerTask() {
	        @Override
	        public void run() {
	        	java.awt.Window[] windows = java.awt.Window.getWindows();
	            for (java.awt.Window window : windows) {
	                if (window instanceof JDialog) {
	                    JDialog dialog = (JDialog) window;
	                    if (dialog.getContentPane().getComponentCount() == 1
	                        && dialog.getContentPane().getComponent(0) instanceof JOptionPane){
	                    	JOptionPane op = (JOptionPane) dialog.getContentPane().getComponent(0);
	                    	op.setValue(answer);
	                    }
	                }
	            }
	        }
	    };
	
	    Timer timer = new Timer("MyTimer");//create a new Timer
	    timer.schedule(timerTask, timeToWait);
	}
	
	
	private void scheduleAutoAnswerFileChooser(String path){
		TimerTask timerTask = new TimerTask() {
	        @Override
	        public void run() {
	        	java.awt.Window[] windows = java.awt.Window.getWindows();
	            for (java.awt.Window window : windows) {
	                if (window instanceof JDialog) {
	                    JDialog dialog = (JDialog) window;
	                    if (dialog.getContentPane().getComponentCount() == 1
	                        && dialog.getContentPane().getComponent(0) instanceof JFileChooser){
	                    	JFileChooser jfc = (JFileChooser) dialog.getContentPane().getComponent(0);
	                    	File f = new File(path);
	                    	try {
	                    		jfc.setSelectedFile(f);
		                    	jfc.approveSelection();
	                    	} catch (Exception e) {
	                    		e.printStackTrace();
	                    		return;
	                    	}
	                    	
	                    }
	                }
	            }
	        }
	    };
	
	    Timer timer = new Timer("MyTimer");//create a new Timer
	    timer.schedule(timerTask, 5000);
	}
	
	
	/**
	 * 
	 * Shows a information window that informs 
	 * the current user about the running analysis 
	 * and its result path.
	 * 
	 * @param analysisFrame
	 * @param directory
	 * @throws InterruptedException
	 */
	
	private void showRunningWindow(JFrame analysisFrame, String directory) 
			throws InterruptedException {
		this.frame.setEnabled(false);
		String message = "Document analysis running...\nPlease wait\n\n";
		message += "\nAfter document analysis this window will be automatically closed.\nProduced output will "
				+ "be available at " + directory + File.separator + "analysis.txt";
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());

		JTextArea textArea = new JTextArea(message);
		
		
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setText(message);
		textArea.setWrapStyleWord(true);
		textArea.setSize(new Dimension(280,280));
		textArea.setBackground(new Color(0,0,0,0));
		textArea.setVisible(true);
		
		panel.add(textArea);
		
		analysisFrame.add(panel);
		analysisFrame.setTitle("Work in progress. Please wait");
		analysisFrame.setSize(300, 300);
		analysisFrame.setLocationRelativeTo(null);
		analysisFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		analysisFrame.setVisible(true);
		analysisFrame.setResizable(false);
		
		
	}
	
	
	/**
	 * 
	 * Hides the window created by showRunningWindow()
	 * 
	 * @param analysisFrame
	 */
	private void hideRunningWindow(JFrame analysisFrame) {
		//this.frame.setEnabled(true);
		analysisFrame.setVisible(false);
	}
}
