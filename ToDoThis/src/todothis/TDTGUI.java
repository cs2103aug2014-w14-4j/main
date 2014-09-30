package todothis;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JViewport;

import todothis.ITDTParser.COMMANDTYPE;

public class TDTGUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String FILENAME = "todothis.txt";
	public static final String DEFAULT_LABEL = "TODAY";
	private TDTStorage storage;
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
					frame.taskLabel.setText("Adding task to: " + frame.storage.getCurrLabel());
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private String displayTask() {
		StringBuilder sb = new StringBuilder();
		Set<String> labelSet = storage.getLabelMap().keySet();
		Iterator<String> labelIter = labelSet.iterator();
		ArrayList<Task> array;
		
		while(labelIter.hasNext()) {
			String currLabel = labelIter.next();
			array = storage.getLabelMap().get(currLabel);
			sb.append("------------------------------------------------------------\n");
			sb.append(currLabel + ": \n");
			for(int i = 0 ; i < array.size(); i++) {
				Task task = array.get(i);
				if(!task.isHide()){
					sb.append("\t" + task.getDateAndTime().display() + "\n");
					sb.append("\t" + task.getTaskID() + ") " + task.getDetails());
					if(task.isHighPriority()) {
						sb.append("\t" + "(!!!!)\n");
					} 
					if(task.isDone()) {
						sb.append("\t" + "(DONE)\n");
					}
					sb.append("\n\n");
				}
			}
		}
		return sb.toString();
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
		
		
		commandLabel.setBounds(10, 11, 576, 14);
		contentPane.add(commandLabel);
		
		
		
		
		//SCROLL PANE
		
		
		scrollPane.setBounds(10, 58, 576, 353);
		contentPane.add(scrollPane);
		scrollPane.setRowHeaderView(taskPane);
		taskPane.setBackground(Color.CYAN);
		//taskPane.setEditorKit(new HTMLEditorKit());
		taskPane.setFocusable(false);
		scrollPane.setFocusable(true);
		scrollPane.getViewport().setView(taskPane);
		taskPane.setEditable(false);
		feedbackArea.setEditable(false);
		
		feedbackArea.setFocusable(false);
		commandLabel.setFocusable(false);
		feedbackArea.setBounds(10, 422, 576, 128);
		
		contentPane.add(feedbackArea);
		
		
		commandField.setBounds(64, 8, 521, 20);
		contentPane.add(commandField);
		commandField.setColumns(10);
		commandField.setFocusable(true);
		
		
		taskLabel.setBounds(10, 36, 319, 14);
		
		contentPane.add(taskLabel);
		commandField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				int keyCode = arg0.getKeyCode();
				switch(keyCode) {
					case KeyEvent.VK_ENTER :
						userCommand = commandField.getText();
						commandHistory.add(userCommand);
						setHistoryPointer(commandHistory.size());
						commandField.setText("");
						Command command = parser.parse(userCommand);
						String feedback = logic.executeCommand(command);
						taskLabel.setText("Adding task to: " + storage.getCurrLabel());
						feedbackArea.setText(feedback);
						if(command.getCommandType() != COMMANDTYPE.SEARCH) {
							taskPane.setText(displayTask());
						} else {
							taskPane.setText("");
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
	
	private String doInit() {
		
		storage = new TDTStorage(FILENAME);
		parser = new TDTParser();
		logic = new TDTLogic(storage);
		try {
			storage.readInitialise();
		} catch (Exception e) {
			e.printStackTrace();
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
