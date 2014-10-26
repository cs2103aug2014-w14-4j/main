package todothis;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import todothis.command.Command;
import todothis.command.RedoCommand;
import todothis.command.SearchCommand;
import todothis.command.UndoCommand;
import todothis.logic.TDTDateAndTime;
import todothis.logic.TDTLogic;
import todothis.logic.Task;
import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.parser.TDTParser;

public class TDTGUI extends JFrame implements DocumentListener{
	/**
	 * 
	 */
	private static final String COMMIT_ACTION = "commit";
    private static enum Mode { INSERT, COMPLETION };
    private Mode mode = Mode.INSERT;
	
	private static final long serialVersionUID = 1L;
	public static final String FILENAME = "todothis.txt";
	public static final String DEFAULT_LABEL = "TODOTHIS";
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
	String css = ".datagrid table {background: \"white\"; border: 3px solid; border-radius: 20px; text-align: center; width: 100%; } "
			+ ".datagrid {font: normal 12px/150% Arial, Helvetica, sans-serif; background: \"white\"; overflow: hidden; border: 4px solid #006699; border-radius: 100px;}"
			+ ".datagrid table td  { text-align: center; padding: 3px 10px;color: #00496B; border-left: 1px solid #5882FA;font-size: 12px;font-weight: normal; }"
			+ ".datagrid table .alt { background: #E1EEF4; color: #00496B; }"
			+ ".datagrid table th{ background: #BDBDBD}"
			+ ".datagrid table .overdue td{ background: #DF0101; color: \"white\" }"
			+ ".datagrid table .priority td{ background: #F781D8; color: \"white\" }"
			+ ".datagrid table tr .datetime{ font-size:10px }"
			+ ".datagrid table .done td{ background: #04B404; text-decoration: line-through}"
			+ ".label{ color: \"blue\"; font-size:15px}";


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final TDTGUI frame = new TDTGUI();
					frame.feedbackArea.setText(frame.doInit());
					frame.setVisible(true);
					
					frame.taskPane.setText(frame.displayTask());
					frame.taskLabel.setText("Adding task to: " + frame.logic.getCurrLabel());
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						   public void run() { 
						       frame.scrollPane.getVerticalScrollBar().setValue(0);
						   }
						});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private String displayTask() {
		StringBuilder sb = new StringBuilder();
		String currLabel = logic.getCurrLabel();
		Iterator<String> labelIter = logic.getLabelIterator();
		ArrayList<Task> array;
		
		//Display task from current label first
		array =  logic.getTaskListFromLabel(currLabel);
		displayFormat(sb, currLabel, array);
		
		while(labelIter.hasNext()) {
			String label = labelIter.next();
			if(!label.equals(currLabel)) {
				array = logic.getTaskListFromLabel(label);
				displayFormat(sb, label, array);
			}
		}
		return sb.toString();
	}
	
	private void displayFormat(StringBuilder sb, String currLabel,
			ArrayList<Task> array) {
		if(array != null) {
			sb.append("<span class = \"label\"><b>" + currLabel + "("
					+ array.size() + ")" + "</b></span>: <br>");
			if(!logic.isHideLabel(currLabel)) {
				for(int i = 0 ; i < array.size(); i++) {
					Task task = array.get(i);
					if(i == 0) {
						sb.append("<div class=\"datagrid\"><table>");
						sb.append("<tr><th>TaskID</th><th>TaskDetails</th><th>Date/Time</th>");
					}
					if(task.isDone()) {
						sb.append("<tr class = \"done\"><td>" + task.getTaskID() + "</td><td>" 
								+ task.getDetails() + "</td><td class = \"datetime\">" 
								+ task.getDateAndTime().display() + "</td></tr>" );
					} else if(task.getDateAndTime().isOverdue()) {
						sb.append("<tr class = \"overdue\"><td>" + task.getTaskID() + "</td><td>" 
								+ task.getDetails() + "</td><td class = \"datetime\">" 
								+ task.getDateAndTime().display() + "</td></tr>" );
					} else if(task.isHighPriority()) {
						sb.append("<tr class = \"priority\"><td>" + task.getTaskID() + "</td><td>" 
								+ task.getDetails() + "</td><td class = \"datetime\">" 
								+ task.getDateAndTime().display() + "</td></tr>" );
					} else if(i % 2 == 0) {
						sb.append("<tr class = \"alt\"><td>" + task.getTaskID() + "</td><td>" 
								+ task.getDetails() + "</td><td class = \"datetime\">" 
								+ task.getDateAndTime().display() + "</td></tr>" );
					} else {
						sb.append("<tr><td>" + task.getTaskID() + "</td><td>" + task.getDetails() 
								+ "</td><td class = \"datetime\">" + task.getDateAndTime().display() 
								+ "</td></tr>");
					}
				}
				sb.append("</table></div>");
			}
		}
		
	}
	
	private String displaySearch(ArrayList<Task> searched) {
		Collections.sort(searched);
		Iterator<Task> iter = searched.iterator();
		StringBuilder res = new StringBuilder();
		int i = 0;
		res.append("<span class = \"label\"><b>SEARCH RESULTS</b></span>: <br>");
		res.append("<div class=\"datagrid\"><table>");
		res.append("<tr><th>Label</th><th>TaskID</th><th>TaskDetails</th><th>Date/Time</th>");
		while(iter.hasNext()){
			Task next = iter.next();
			if(next.isDone()) {
				res.append("<tr class = \"done\"><td>" + next.getLabelName() + "</td><td>" 
						+ next.getTaskID() + "</td><td>" + next.getDetails() + "</td><td class = \"datetime\">" 
						+ next.getDateAndTime().display() + "</td></tr>" );
			} else if(next.getDateAndTime().isOverdue()) {
				res.append("<tr class = \"overdue\"><td>" + next.getLabelName() + "</td><td>" 
						+ next.getTaskID() + "</td><td>" 
						+ next.getDetails() + "</td><td class = \"datetime\">" 
						+ next.getDateAndTime().display() + "</td></tr>" );
			} else if(next.isHighPriority()) {
				res.append("<tr class = \"priority\"><td>" + next.getLabelName() + "</td><td>" 
						+ next.getTaskID() + "</td><td>" + next.getDetails() + "</td><td class = \"datetime\">" 
						+ next.getDateAndTime().display() + "</td></tr>" );
			} else if(i % 2 == 0) {
				res.append("<tr class = \"alt\"><td>" + next.getLabelName() + "</td><td>" 
						+ next.getTaskID() + "</td><td>" + next.getDetails() + "</td><td class = \"datetime\">" 
						+ next.getDateAndTime().display() + "</td></tr>" );
			} else {
				res.append("<tr><td>" + next.getLabelName() + "</td><td>" + next.getTaskID() 
						+ "</td><td>" + next.getDetails() + "</td><td class = \"datetime\">" 
						+ next.getDateAndTime().display() + "</td></tr>");
			}
			i++;
		}
		return res.toString();
	}
	

	/**
	 * Create the frame.
	 */
	public TDTGUI() {
		commandField.getDocument().addDocumentListener(this);
        InputMap im = commandField.getInputMap();
        ActionMap am = commandField.getActionMap();
        im.put(KeyStroke.getKeyStroke("RIGHT"), COMMIT_ACTION);
        am.put(COMMIT_ACTION, new CommitAction());
		
		//Content Pane
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new MatteBorder(3, 3, 3, 3, (Color) new Color(0, 0, 0)));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//Command label
		commandLabel.setBounds(10, 3, 526, 17);
		commandLabel.setFocusable(false);
		contentPane.add(commandLabel);
		
		//CommandField
		
		
		
		commandField.setBounds(61, 3, 713, 20);
		commandField.setColumns(10);
		commandField.setFocusable(true);
		commandField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				int keyCode = arg0.getKeyCode();
				if(arg0.isControlDown() && keyCode == KeyEvent.VK_Z) {
					UndoCommand undo = new UndoCommand();
					String feedback = logic.executeCommand(undo);
					updateGUI(feedback, displayTask());
				} 
				
				if(arg0.isControlDown() && keyCode == KeyEvent.VK_Y) {
					RedoCommand redo = new RedoCommand();
					String feedback = logic.executeCommand(redo);
					updateGUI(feedback, displayTask());
				}
				
				
				if(arg0.isControlDown() && keyCode == KeyEvent.VK_DOWN) {
					Rectangle rec = getBounds();
					rec.setSize((int)rec.getWidth(), (int)rec.getHeight() + 10);
					setBounds(rec);
					
					rec = scrollPane.getBounds();
					rec.setSize((int)rec.getWidth(), (int)rec.getHeight() + 10);
					scrollPane.setBounds(rec);
					
					rec = feedbackArea.getBounds();
					rec.setSize((int)rec.getWidth(), (int)rec.getHeight() + 10);
					rec.setLocation((int)rec.getX(), (int)rec.getY() + 10);
					feedbackArea.setBounds(rec);
				}
				
				if(arg0.isControlDown() && keyCode == KeyEvent.VK_UP) {
					Rectangle rec = getBounds();
					rec.setSize((int)rec.getWidth(), (int)rec.getHeight() - 10);
					setBounds(rec);
					
					rec = scrollPane.getBounds();
					rec.setSize((int)rec.getWidth(), (int)rec.getHeight() - 10);
					scrollPane.setBounds(rec);
					
					rec = feedbackArea.getBounds();
					rec.setSize((int)rec.getWidth(), (int)rec.getHeight() - 10);
					rec.setLocation((int)rec.getX(), (int)rec.getY() - 10);
					feedbackArea.setBounds(rec);
				}
				
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
					
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						   public void run() { 
						       scrollPane.getVerticalScrollBar().setValue(0);
						   }
						});
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
				case KeyEvent.VK_SPACE :
					userCommand = commandField.getText();
					String[] words = userCommand.split(" ");
					if(words.length == 2) {
						if(words[0].equalsIgnoreCase("edit")) {
							try {
								int id = Integer.parseInt(words[1]);
								if(id > 0 && id <= logic.getTaskListFromLabel(logic.getCurrLabel()).size()) {
									Task task = logic.getTaskListFromLabel(logic.getCurrLabel()).get(id-1);
									TDTDateAndTime dat = task.getDateAndTime();
									String datString = getDateTimeStringForEdit(dat);
									commandField.setText(userCommand + " " +task.getDetails() + datString);
									javax.swing.SwingUtilities.invokeLater(new Runnable() {
										   public void run() { 
											   commandField.select(userCommand.length() + 1,
													   commandField.getText().length() - 1);
										   }
										});
								}
							} catch(NumberFormatException e) {
								;
							}
						}
					}
					if(words.length == 3) {
						if(words[0].equalsIgnoreCase("edit")) {
							try {
								int id = Integer.parseInt(words[2]);
								if(logic.getStorage().getLabelMap().containsKey(words[1].toUpperCase())) {
									if(id > 0 && id <= logic.getTaskListFromLabel(words[1]).size()) {
										Task task = logic.getTaskListFromLabel(words[1]).get(id-1);
										TDTDateAndTime dat = task.getDateAndTime();
										String datString = getDateTimeStringForEdit(dat);
										commandField.setText(userCommand + " " + task.getDetails() + datString);
										javax.swing.SwingUtilities.invokeLater(new Runnable() {
											   public void run() { 
												   commandField.select(userCommand.length() + 1,
														   commandField.getText().length() - 1);
											   }
											});
									}
								}
							} catch(NumberFormatException e) {
								;
							}
						}
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
		contentPane.add(commandField);
		
		//Tasklabel
		taskLabel.setBounds(10, 25, 566, 14);
		contentPane.add(taskLabel);
		
		
		
		//Task Pane
		taskPane.setBackground(Color.LIGHT_GRAY);
		HTMLEditorKit kit = new HTMLEditorKit();
		taskPane.setEditorKit(kit);
		StyleSheet styleSheet = kit.getStyleSheet();
		styleSheet.addRule(css);
		taskPane.setFocusable(false);
		taskPane.setEditable(false);
		
		//Scroll PaneListeners
		scrollPane.setBounds(10, 42, 764, 427);
		scrollPane.setRowHeaderView(taskPane);
		scrollPane.setFocusable(true);
		scrollPane.getViewport().setView(taskPane);
		contentPane.add(scrollPane);
		scrollPane.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				int keyCode = arg0.getKeyCode();
				if(arg0.isControlDown() && keyCode == KeyEvent.VK_Z) {
					UndoCommand undo = new UndoCommand();
					String feedback = undo.execute(logic.getStorage());
					updateGUI(feedback, displayTask());
				} 
				if(arg0.isControlDown() && keyCode == KeyEvent.VK_Y) {
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

		//Feedback Area
		feedbackArea.setBounds(10, 480, 764, 70);
		feedbackArea.setEditable(false);
		feedbackArea.setFocusable(false);
		contentPane.add(feedbackArea);
	}
	
	private String getDateTimeStringForEdit(TDTDateAndTime dat) {
		String datString = "";
		if(dat.isTimedTask()) {
			datString = " from";
			if(!dat.getStartDate().equals("null")) {
				datString = datString + " " + dat.getStartDate();
			}
			if(!dat.getStartTime().equals("null")) {
				datString = datString + " " + dat.getStartTime();
			}
			if(!dat.getEndDate().equals("null") || !dat.getEndTime().equals("null") ) {
				datString = datString + " to";
				if(!dat.getEndDate().equals("null")) {
					datString = datString + " " + dat.getEndDate();
				}
				if(!dat.getEndTime().equals("null")) {
					datString = datString + " " + dat.getEndTime();
				}
			}
		}
		if(dat.isDeadlineTask()) {
			datString = " by";
			if(!dat.getEndDate().equals("null")) {
				datString = datString + " " + dat.getEndDate();
			}
			if(!dat.getEndTime().equals("null")) {
				datString = datString + " " +dat.getEndTime();
			}
		}
		return datString;
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
	
	//Document

	@Override
	public void changedUpdate(DocumentEvent arg0) {
	}

	@Override
	public void insertUpdate(DocumentEvent ev) {
		if (ev.getLength() != 1) {
            return;
        }
         
        int pos = ev.getOffset();
        String content = null;
        try {
            content = commandField.getText(0, pos + 1);
        } catch (BadLocationException e) {
            System.out.println("Bad location.");
        }
         
        // Find where the word starts
        int w;
        for (w = pos; w >= 0; w--) {
            if (! Character.isLetter(content.charAt(w))) {
                break;
            }
        }
        if (pos - w < 2) {
            // Too few chars
            return;
        }
         
        String prefix = content.substring(w + 1).toUpperCase();
        int n = Collections.binarySearch(logic.getAutoWords(), prefix);

        if (n < 0  && -n <= logic.getAutoWords().size()) {
            String match = logic.getAutoWords().get(-n - 1);
            if (match.toLowerCase().startsWith(prefix.toLowerCase())) {
                String completion = match.substring(pos - w).toLowerCase();
                SwingUtilities.invokeLater(
                        new CompletionTask(completion, pos + 1));
            }
        } else {
            // Nothing found
            mode = Mode.INSERT;
        }
		
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
	}

	
	private class CompletionTask implements Runnable {
        String completion;
        int position;
         
        CompletionTask(String completion, int position) {
            this.completion = completion;
            this.position = position;
        }
         
        public void run() {
            try {
				commandField.getDocument().insertString(position, completion, null);
			} catch (BadLocationException e) {
				System.out.println("Bad location error.");
			}
            commandField.setCaretPosition(position + completion.length());
            commandField.moveCaretPosition(position);
            mode = Mode.COMPLETION;
        }
    }
	
	private class CommitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ev) {
            if (mode == Mode.COMPLETION) {
                int pos = commandField.getSelectionEnd();
                try {
					commandField.getDocument().insertString(pos, " ", null);
				} catch (BadLocationException e) {
					System.out.println("Bad location error.");
				}
                commandField.setCaretPosition(pos + 1);
                mode = Mode.INSERT;
            } else {
            	commandField.setCaretPosition(Math.min(commandField.getCaretPosition()+1,
            			commandField.getText().length()));
            }
        }
    }
	
}
