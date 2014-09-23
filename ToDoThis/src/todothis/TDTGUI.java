package todothis;

import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTextField;

import todothis.ITDTParser.COMMANDTYPE;

public class TDTGUI extends JFrame {
	public static final String FILENAME = "todothis.txt";
	public static final String DEFAULT_LABEL = "TODAY";
	private TDTStorage storage;
	private TDTLogic logic;
	private TDTParser parser;
	private String userCommand;
	JLabel commandLabel = new JLabel("I want to: ");
	JTextPane taskPane = new JTextPane();
	JTextArea feedbackArea = new JTextArea();
	JTextField commandField = new JTextField();
	private JPanel contentPane;
	JLabel taskLabel = new JLabel();

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
			
					frame.taskLabel.setText("Adding task to: " + frame.storage.getCurrLabel());
					
	
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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
		
		
		commandLabel.setBounds(10, 11, 679, 14);
		contentPane.add(commandLabel);
		
		
		
		taskPane.setEditable(false);
		taskPane.setBounds(10, 52, 679, 362);
		contentPane.add(taskPane);
		
		
		feedbackArea.setBounds(10, 422, 679, 128);
		contentPane.add(feedbackArea);
		
		
		commandField.setBounds(64, 8, 625, 20);
		contentPane.add(commandField);
		commandField.setColumns(10);
		commandField.setFocusable(true);
		
		
		taskLabel.setBounds(10, 36, 319, 14);
		
		contentPane.add(taskLabel);
		commandField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyChar() == '\n') {
					userCommand = commandField.getText();
					commandField.setText("");
					Command command = parser.parse(userCommand);
					String feedback = logic.executeCommand(command);
					if(command.getCommandType() != COMMANDTYPE.SEARCH) {
						taskPane.setText("");
						feedbackArea.setText(feedback);
						
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
}
