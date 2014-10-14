package todothis;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.text.html.HTMLEditorKit;

import todothis.command.Command;
import todothis.command.RedoCommand;
import todothis.command.SearchCommand;
import todothis.command.UndoCommand;
import todothis.logic.TDTLogic;
import todothis.logic.Task;
import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.parser.TDTParser;

public class TDTGUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String FILENAME = "todothis.txt";
	public static final String DEFAULT_LABEL = "TODAY";
	private TDTLogic logic;
	private TDTParser parser;
	private String userCommand;
	private ArrayList<String> commandHistory = new ArrayList<String>();
	private int historyPointer = 0;
	JLabel commandLabel = new JLabel("I want to: ");
	JTextPane taskPane = new JTextPane();
	JTextArea feedbackArea = new JTextArea();
	JTextField commandField = new JTextField();
	private JPanel contentPane;
	JLabel taskLabel = new JLabel();
	JScrollPane scrollPane = new JScrollPane();
	JViewport vp;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TDTGUI frame = new TDTGUI();
					frame.feedbackArea.setText(frame.doInit());
					frame.setVisible(true);
					
					frame.taskPane.setText(frame.displayTask());
					frame.taskLabel.setText("Adding task to: " + frame.logic.getCurrLabel());
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private String displayTask() {
		StringBuilder sb = new StringBuilder();
		Iterator<String> labelIter = logic.getLabelIterator();
		ArrayList<Task> array;
		
		while(labelIter.hasNext()) {
			String currLabel = labelIter.next();
			array = logic.getTaskListFromLabel(currLabel);
			sb.append("------------------------------------------------------------<br>");
			sb.append("<span color = \"blue\"><b>" + currLabel + "</b></span>: <br>");
			for(int i = 0 ; i < array.size(); i++) {
				Task task = array.get(i);
				if(!task.isHide()){
					if(task.isDone()){
						sb.append("<span color = #606060><strike>");
					} else if(task.isHighPriority()) {
						sb.append("<span color = \"red\">");
					}
					sb.append("[" + task.getTaskID() + "] " + task.getDateAndTime().display() 
							+ "<br>");
					sb.append(task.getDetails()+ "</span><br><br>");
				}
			}
		}
		return sb.toString();
	}

	private String displaySearch(ArrayList<Task> searched) {
		Iterator<Task> iter = searched.iterator();
		StringBuilder res = new StringBuilder();
		res.append("<b>SEARCH RESULTS:</b><br>");
		res.append("------------------------------------------------------------<br>");
		while(iter.hasNext()){
			Task next = iter.next();

			if(next.isDone()){
				res.append("<span color = #606060><strike>");
			} else if(next.isHighPriority()) {
				res.append("<span color = \"red\">");
			}
			res.append("<span color = \"blue\">Label: " + next.getLabelName() 
					+"       TaskID: "+ next.getTaskID() + "</span><br>");
			res.append(next.getDateAndTime().display()+ "<br>");
			res.append(next.getDetails()+ "</span><br>");
			res.append("------------------------------------------------------------<br>");
		}
		return res.toString();
	}
	
	

	/**
	 * Create the frame.
	 */
	public TDTGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300, 100, 800, 600);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//commandLabel
		commandLabel.setBounds(10, 11, 576, 14);
		commandLabel.setFocusable(false);
		contentPane.add(commandLabel);

		//SCROLL PANE
		scrollPane.setBounds(10, 58, 576, 353);
		scrollPane.setRowHeaderView(taskPane);
		scrollPane.setFocusable(true);
		scrollPane.getViewport().setView(taskPane);
		contentPane.add(scrollPane);
		
		//Task Pane
		taskPane.setBackground(Color.CYAN);
		taskPane.setEditorKit(new HTMLEditorKit());
		taskPane.setFocusable(false);
		taskPane.setEditable(false);
		
		//Feedback Area
		feedbackArea.setEditable(false);
		feedbackArea.setFocusable(false);
		feedbackArea.setBounds(10, 422, 576, 128);
		contentPane.add(feedbackArea);
		
		//Command Field
		commandField.setBounds(64, 8, 521, 20);
		commandField.setColumns(10);
		commandField.setFocusable(true);
		contentPane.add(commandField);
		
		//Task Label
		taskLabel.setBounds(10, 36, 319, 14);
		contentPane.add(taskLabel);
		
		//Listerners
		
		scrollPane.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				int keyCode = arg0.getKeyCode();
				if(arg0.isControlDown() && keyCode == KeyEvent.VK_Z) {
					UndoCommand undo = new UndoCommand();
					String feedback = undo.execute(logic.getStorage());
					updateGUI(feedback, displayTask());
				} 
				if(arg0.isControlDown() && keyCode == KeyEvent.VK_X) {
					RedoCommand redo = new RedoCommand();
					String feedback = logic.executeCommand(redo);
					updateGUI(feedback, displayTask());
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		commandField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				int keyCode = arg0.getKeyCode();
				if(arg0.isControlDown() && keyCode == KeyEvent.VK_Z) {
					UndoCommand undo = new UndoCommand();
					String feedback = logic.executeCommand(undo);
					updateGUI(feedback, displayTask());
				} 
				
				if(arg0.isControlDown() && keyCode == KeyEvent.VK_X) {
					RedoCommand redo = new RedoCommand();
					String feedback = logic.executeCommand(redo);
					updateGUI(feedback, displayTask());
				} else {
					switch(keyCode) {
					case KeyEvent.VK_ENTER :
						userCommand = commandField.getText();
						commandHistory.add(userCommand);
						setHistoryPointer(commandHistory.size());
						commandField.setText("");

						Command command = parser.parse(userCommand);

						if(command.getCommandType() != COMMANDTYPE.SEARCH) {
							String feedback = logic.executeCommand(command);
							updateGUI(feedback, displayTask());
						} else {
							String feedback = logic.executeCommand(command);
							ArrayList<Task> searched = ((SearchCommand)command).getSearchedResult();
							updateGUI(feedback, displaySearch(searched));
						}
						break;
					case KeyEvent.VK_UP :
						historyPointer--;
						if(historyPointer < 0) {
							commandField.setText("");
							setHistoryPointer(-1);
						} else {
							commandField.setText(commandHistory.get(historyPointer));
						}
						break;
					case KeyEvent.VK_DOWN :
						historyPointer++;
						if(historyPointer >= commandHistory.size()) {
							commandField.setText("");
							setHistoryPointer(commandHistory.size());
						} else {
							commandField.setText(commandHistory.get(historyPointer));
						}
						break;

					default :
						break;
					}
				}
				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	private void updateGUI(String feedback, String text) {
		taskLabel.setText("Adding task to: " + logic.getCurrLabel());
		feedbackArea.setText(feedback);
		taskPane.setText(text);
	}
	
	private String doInit() {
		parser = new TDTParser();
		logic = new TDTLogic(FILENAME);
		try {
			logic.readAndInitialize();
		} catch (Exception e) {
			return "Unable to create todothis.txt";
		}
		return "Todo-This ready!";
	}

	public ArrayList<String> getCommandHistory() {
		return commandHistory;
	}

	public void setCommandHistory(ArrayList<String> commandHistory) {
		this.commandHistory = commandHistory;
	}

	public int getHistoryPointer() {
		return historyPointer;
	}

	public void setHistoryPointer(int historyPointer) {
		this.historyPointer = historyPointer;
	}
}
