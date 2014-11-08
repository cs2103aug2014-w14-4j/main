package todothis.gui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import todothis.commons.TDTCommons;
import todothis.commons.TDTDateAndTime;
import todothis.commons.TDTDateMethods;
import todothis.commons.TDTTimeMethods;
import todothis.commons.Task;
import todothis.logic.TDTController;

public class TDTGUI extends JFrame implements DocumentListener {

	private static final String COMMIT_ACTION = "commit";

	private static enum Mode {
		INSERT, COMPLETION
	};

	private Mode mode = Mode.INSERT;

	private static final long serialVersionUID = 1L;
	private TDTController control;
	private String userCommand;
	private ArrayList<String> commandHistory = new ArrayList<String>();
	private int historyPointer = 0;
	private JLabel commandLabel = new JLabel(" I want to: ");
	private JTextPane taskPane = new JTextPane();
	private JTextPane feedbackArea = new JTextPane();
	private JTextField commandField = new JTextField();
	private JPanel contentPane;
	private JLabel taskLabel = new JLabel();
	private JScrollPane scrollPane = new JScrollPane();
	private JPanel top1 = new JPanel();
	private JPanel top2 = new JPanel();
			   
	String css = ".datagrid table {background: \"white\"; text-align: center; width: 100%; } "
			+ ".datagrid {font: normal 12px/150% Candara, Helvetica, sans-serif; overflow: hidden; border: 4px solid #006699; }"
			+ ".datagrid table td  { text-align: left; color: #00496B; border: 1px solid white;border-left: 1px solid #5882FA; font-size: 13px; font-weight: normal; }"
			+ ".datagrid table .alt { background: #E1EEF4; color: #00496B; }"
			+ ".datagrid table .heading{ border-left: 1px solid #5882FA;border-right: 1px solid #5882FA; background: #BDBDBD}"
			+ ".datagrid table .heading th{ border: 1px solid white;}"
			+ ".datagrid table .overdue { background: #FC7C7C; color: white}"
			+ ".datagrid table .priority { background: #ff4040; color: white }"
			+ ".datagrid table tr .datetime{ font-size:12px }"
			+ ".datagrid table .target { border: 4px solid #0F0E0E }"
			+ ".datagrid table .clash { border: 4px solid #EEB111 }"
			+ ".datagrid table .priorityclash { border: 4px solid #EEB111; background: #ff4040; color: white }"
			+ ".datagrid table .overdueclash { border: 4px solid #EEB111; background: #FC7C7C; color: white }"
			+ ".datagrid table .altclash { border: 4px solid #EEB111; background: #E1EEF4; color: #00496B; }"
			+ ".datagrid table .prioritytarget { border: 4px solid #0F0E0E; background: #ff4040; color: white }"
			+ ".datagrid table .overduetarget { border: 4px solid #0F0E0E; background: #FC7C7C; color: white }"
			+ ".datagrid table .alttarget { border: 4px solid #0F0E0E; background: #E1EEF4; color: #00496B; }"
			+ ".datagrid table .taskId { width: 10%; }"
			+ ".datagrid table .dateTime { width: 35%; }"
			+ ".datagrid table .labelhead { width: 10%; }"
			+ ".datagrid table .done { background: #00ff7f; text-decoration: line-through}"
			+ ".datagrid table .donetarget { background: #00ff7f; text-decoration: line-through}"
			+ ".label{ color: #0174DF; font-size:15px font-family: Candara;}"
			+ ".helptable td { text-align: left; color: #00496B; border: 1px solid black;border-left: 1px solid #5882FA; font-size: 13px; font-weight: normal; }"
			+ ".helptable tr { text-align: left; color: #00496B; border: 1px solid black;border-left: 1px solid #5882FA; font-size: 13px; font-weight: normal; }"
			+ ".helptable th { text-align: left; color: #00496B; border: 1px solid black;border-left: 1px solid #5882FA; font-size: 13px; font-weight: normal; }";
			

	/**
	 * Create the frame.
	 */
	public TDTGUI() {
		control = new TDTController(TDTCommons.FILENAME);
		initContentPane();
		initCommandLabel();
		initCommandField();
		initTaskLabel();
		initTaskPane();
		initScrollPane();
		initFeedbackArea();
	}

	String displayTask(ArrayList<Task> target, Task addedTask) {
		StringBuilder sb = new StringBuilder();
		String currLabel = control.getCurrLabel();
		Iterator<String> labelIter = control.getLabelIterator();
		ArrayList<Task> array;
		Iterator<String> hideIter = control.getHideIter();

		// Display task from current label first
		array = control.getTaskListFromLabel(currLabel);
		displayFormat(sb, currLabel, array, target, addedTask);

		while (labelIter.hasNext()) {
			String label = labelIter.next();
			if (!label.equals(currLabel)) {
				array = control.getTaskListFromLabel(label);
				displayFormat(sb, label, array, target, addedTask);
			}
		}
		while (hideIter.hasNext()) {
			String label = hideIter.next();
			array = control.getTaskListFromLabel(label);
			if(array != null) {
				sb.append("<span class = label><b>" + label + "("
						+ array.size() + ")" + "</b></span>: <br>");
			}
		}
		return sb.toString();
	}

	private void displayFormat(StringBuilder sb, String currLabel,
			ArrayList<Task> array, ArrayList<Task> target, Task addedTask) {
		if (array != null) {	
			if (!control.isHideLabel(currLabel)) {
				sb.append("<span class = label><b>" + currLabel + "("
						+ array.size() + ")" + "</b></span>: <br>");
				for (int i = 0; i < array.size(); i++) {
					Task task = array.get(i);
					if (i == 0) {
						sb.append("<div class= datagrid><table>");
						sb.append("<tr class = heading> <th class = taskId>TaskID</th> <th>TaskDetails</th> "
								+ "<th class = dateTime>Date/Time</th> </tr>");
					}
					if (task.isDone()) {
						sb.append(displayTaskInRow(task, " class = done", target, addedTask));
					} else if (task.getDateAndTime().isOverdue()) {
						sb.append(displayTaskInRow(task, " class = overdue", target, addedTask));
					} else if (task.isHighPriority()) {
						sb.append(displayTaskInRow(task, " class = priority", target, addedTask));
					} else if (i % 2 == 0) {
						sb.append(displayTaskInRow(task, " class = alt", target, addedTask));
					} else {
						sb.append(displayTaskInRow(task, " class =", target, addedTask));
					}
				}
				sb.append("</table></div>");
			}
		}
	}

	private String displayTaskInRow(Task task, String type, ArrayList<Task> target, Task addedTask) {
		if(addedTask == task) {
			type = type + "target";
		}
		if(target != null)  {
			if(target.contains(task) ) {
				type = type + "clash";
			}
		} 
		return "<tr" + type + "><td>" + task.getTaskID() + "</td><td>"
				+ task.getDetails() + "</td><td class = datetime>"
				+ task.getDateAndTime().display()
				+ checkIfHaveReminder(task.getRemindDateTime()) 
				+"</td></tr>";
	}
	
	
	private String checkIfHaveReminder(String remind) {
		String res = "";
		if(!remind.equals("null")) {
			String[] temp = remind.split(" ");
			res = res + TDTDateMethods.changeToDayOfWeek(temp[0]) + " ";
			res = res + TDTDateMethods.changeDateFormatDisplay(temp[0]) + " ";
			res = res + TDTTimeMethods.changeTimeFormatDisplay(temp[1]);
			res = "Reminder: " + res;
		}
		return res;
	}

	String displaySearch(ArrayList<Task> searched) {
		Collections.sort(searched);
		Iterator<Task> iter = searched.iterator();
		StringBuilder res = new StringBuilder();
		int i = 0;
		res.append("<span class = \"label\"><b>SEARCH RESULTS</b></span>: <br>");
		res.append("<div class=\"datagrid\"><table>");
		res.append("<tr class = heading><th class = labelhead>Label</th> <th class = taskId>TaskID</th> "
				+ "<th>TaskDetails</th> <th class = dateTime>Date/Time</th> </tr>");
		while (iter.hasNext()) {
			Task next = iter.next();
			if (next.isDone()) {
				res.append(displaySearchTaskInRow(next, " class = done"));
			} else if (next.getDateAndTime().isOverdue()) {
				res.append(displaySearchTaskInRow(next, " class = overdue"));
			} else if (next.isHighPriority()) {
				res.append(displaySearchTaskInRow(next, " class = priority"));
			} else if (i % 2 == 0) {
				res.append(displaySearchTaskInRow(next, " class = alt"));
			} else {
				res.append(displaySearchTaskInRow(next, ""));
			}
			i++;
		}
		return res.toString();
	}

	private String displaySearchTaskInRow(Task next, String type) {
		return "<tr" + type + "><td>" + next.getLabelName()
				+ "</td><td>" + next.getTaskID() + "</td><td>"
				+ next.getDetails() + "</td><td class = \"datetime\">"
				+ next.getDateAndTime().display() 
				+ checkIfHaveReminder(next.getRemindDateTime()) 
				+"</td></tr>";
	}

	String getDateTimeStringForEdit(TDTDateAndTime dat) {
		String datString = "";
		if (dat.isTimedTask()) {
			datString = " from";
			if (!dat.getStartDate().equals("null")) {
				datString = datString + " " + dat.getStartDate();
			}
			if (!dat.getStartTime().equals("null")) {
				datString = datString + " " + dat.getStartTime();
			}
			if (!dat.getEndDate().equals("null") || !dat.getEndTime().equals("null")) {
				datString = datString + " to";
				if (!dat.getEndDate().equals("null")) {
					datString = datString + " " + dat.getEndDate();
				}
				if (!dat.getEndTime().equals("null")) {
					datString = datString + " " + dat.getEndTime();
				}
			}
		}
		if (dat.isDeadlineTask()) {
			datString = " by";
			if (!dat.getEndDate().equals("null")) {
				datString = datString + " " + dat.getEndDate();
			}
			if (!dat.getEndTime().equals("null")) {
				datString = datString + " " + dat.getEndTime();
			}
		}
		return datString;
	}

	public void updateGUI(String feedback, String text) {
		taskLabel.setText(" Current Label: " + control.getCurrLabel());
		getFeedbackArea().setText(feedback);
		taskPane.setText(text);
		changeColorOfFeedbackArea(feedback);
	}

	private void changeColorOfFeedbackArea(String feedback) {
		String[] feedbackParams = feedback.split(" ");
		if(feedbackParams.length > 0) {
			String firstWord = feedbackParams[0];
			if(firstWord.equals("Invalid")) {
				getFeedbackArea().setBackground(Color.pink);
			} else if(firstWord.equals("Clashes")){
				getFeedbackArea().setBackground(Color.yellow);
			} else {
				getFeedbackArea().setBackground(Color.green);
			}
		} else {
			getFeedbackArea().setBackground(Color.green);
		}
	}
	
	//----------------Component Initialisaiton------------------------------------

	private void initTaskLabel() {
		top1.add(taskLabel, BorderLayout.SOUTH);
		taskLabel.setFocusable(false);
	}

	private void initFeedbackArea() {
		getFeedbackArea().setPreferredSize(new Dimension(100, 40));
		contentPane.add(getFeedbackArea(), BorderLayout.SOUTH);
		getFeedbackArea().setEditable(false);
		getFeedbackArea().setFocusable(false);
	}

	private void initScrollPane() {
		contentPane.add(getScrollPane(), BorderLayout.CENTER);
		getScrollPane().setRowHeaderView(taskPane);
		getScrollPane().setFocusable(true);
		getScrollPane().getViewport().setView(taskPane);
		getScrollPane().addKeyListener(new TDTKeyListener(this));
	}

	private void initTaskPane() {
		taskPane.setBackground(Color.white);
		HTMLEditorKit kit = new HTMLEditorKit();
		taskPane.setEditorKit(kit);
		StyleSheet styleSheet = kit.getStyleSheet();
		styleSheet.addRule(css);
		taskPane.setFocusable(false);
		taskPane.setEditable(false);
	}

	private void initCommandField() {
		getCommandField().setPreferredSize(new Dimension(100, 27));
		getCommandField().getDocument().addDocumentListener(this);
		InputMap im = getCommandField().getInputMap();
		ActionMap am = getCommandField().getActionMap();
		im.put(KeyStroke.getKeyStroke("RIGHT"), COMMIT_ACTION);
		am.put(COMMIT_ACTION, new CommitAction());
		top2.add(getCommandField());
		getCommandField().setColumns(10);
		getCommandField().setFocusable(true);
		getCommandField().addKeyListener(new TDTKeyListener(this));
	}

	private void initCommandLabel() {
		top2.add(commandLabel);
		commandLabel.setFocusable(false);
	}

	private void initContentPane() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(300, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new MatteBorder(3, 3, 3, 3, (Color) new Color(0,
				0, 0)));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add(top1, BorderLayout.NORTH);
		top1.setLayout(new BorderLayout(0, 0));
		top1.add(top2);
		top2.setLayout(new BoxLayout(top2, BoxLayout.X_AXIS));
	}

	public String doInit() {
		try {			
			control.readAndInitialize();
		} catch (Exception e) {
			return "Unable to create todothis.txt";
		}
		setVisible(true);
		Image image = Toolkit.getDefaultToolkit().getImage("src/taeyeon.jpg");
		setIconImage(image);
		setTitle("TodoThis");
		
		taskPane.setText(displayTask(null, null));
		taskLabel.setText(" Current Label: "
				+ control.getCurrLabel());
		return "Todo-This ready!\nType \"help\" for more information.";
	}
	
	

	//----------------------------- DocumentListener -----------------------------------
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
			content = getCommandField().getText(0, pos + 1);
		} catch (BadLocationException e) {
			System.out.println("Bad location.");
		}

		// Find where the word starts
		int w;
		for (w = pos; w >= 0; w--) {
			if (!Character.isLetter(content.charAt(w))) {
				break;
			}
		}
		if (pos - w < 2) {
			// Too few chars
			return;
		}

		String prefix = content.substring(w + 1).toUpperCase();
		int n = Collections.binarySearch(control.getAutoWords(), prefix);

		if (n < 0 && -n <= control.getAutoWords().size()) {
			String match = control.getAutoWords().get(-n - 1);
			if (match.toLowerCase().startsWith(prefix.toLowerCase())) {
				String completion = match.substring(pos - w).toLowerCase();
				SwingUtilities.invokeLater(new CompletionTask(completion,
						pos + 1));
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
				getCommandField().getDocument().insertString(position, completion,
						null);
			} catch (BadLocationException e) {
				System.out.println("Bad location error.");
			}
			getCommandField().setCaretPosition(position + completion.length());
			getCommandField().moveCaretPosition(position);
			mode = Mode.COMPLETION;
		}
	}

	private class CommitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ev) {
			if (mode == Mode.COMPLETION) {
				int pos = getCommandField().getSelectionEnd();
				try {
					getCommandField().getDocument().insertString(pos, "", null);
				} catch (BadLocationException e) {
					System.out.println("Bad location error.");
				}
				getCommandField().setCaretPosition(pos);
				mode = Mode.INSERT;
			} else {
				getCommandField().setCaretPosition(Math.min(getCommandField()
						.getCaretPosition() + 1, getCommandField().getText()
						.length()));
			}
		}
	}
	//--------------------------------System Tray -----------------------------
	public void sysTray(){
		final TDTGUI gui = this;
		if(!SystemTray.isSupported()){
			System.out.println("System tray is not supported !!! ");
			gui.control.writeToFile();
			System.exit(0);
		}
		SystemTray systemTray = SystemTray.getSystemTray();
		Image image = Toolkit.getDefaultToolkit().getImage("src/taeyeon.jpg");

		PopupMenu trayPopupMenu = new PopupMenu();
		MenuItem close = new MenuItem("Close");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gui.control.writeToFile();
				System.exit(0);             
			}
		});
		trayPopupMenu.add(close);
		TrayIcon trayIcon = new TrayIcon(image, "TodoThis", trayPopupMenu);
		trayIcon.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getButton() == MouseEvent.BUTTON1) {
					gui.setVisible(true);
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {	
			}
		});
		trayIcon.setImageAutoSize(true);

		try{
			systemTray.add(trayIcon);
		}catch(AWTException awtException){
			awtException.printStackTrace();
		}
	}

	//--------------------------Getters & Setters------------------------------------

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

	public String getUserCommand() {
		return userCommand;
	}

	public void setUserCommand(String userCommand) {
		this.userCommand = userCommand;
	}


	public TDTController getControl() {
		return control;
	}

	public void setControl(TDTController control) {
		this.control = control;
	}

	public JTextPane getFeedbackArea() {
		return feedbackArea;
	}

	public void setFeedbackArea(JTextPane feedbackArea) {
		this.feedbackArea = feedbackArea;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}

	public JTextField getCommandField() {
		return commandField;
	}

	public void setCommandField(JTextField commandField) {
		this.commandField = commandField;
	}


}

