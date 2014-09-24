package todothis;

import java.util.Iterator;
import java.util.Scanner;

import todothis.ITDTParser.COMMANDTYPE;

public class TodoThis {
	public static final String FILENAME = "todothis.txt";
	public static final String DEFAULT_LABEL = "TODAY";
	
	private static Scanner sc;
	private String fileName;
	private TDTStorage storage;
	private TDTParser parser;
	private TDTLogic logic;
	
	public TodoThis(String fileName) {
		this.setFileName(fileName);
		sc = new Scanner(System.in);
	}
	
	//Initialise
	private String doInit() {
		
		storage = new TDTStorage(FILENAME);
		parser = new TDTParser();
		logic = new TDTLogic(storage);
		try {
			storage.readInitialise();
		} catch (Exception e) {
			e.printStackTrace();
		}
		clearScreen();
		return "Todo-This ready!";
	}
	
	//Display all task under labels nicely
	private void displayTextUI() {
		String label = "";
		// Label
		Iterator<Task> iterator = storage.getTaskIterator();
		while(iterator.hasNext()) {
			Task task = iterator.next();
			if(!task.isHide()){
				if(label.equals("") || !task.getLabelName().equals(label)) {
					label = task.getLabelName();
					System.out.println("---------------------------");
					System.out.println(task.getLabelName() + ": ");
				}
				System.out.print("\t" + task.getTaskID() + ") " + task.getDetails() + "\t" +
					task.getDateAndTime().getStartDate() + "\t" + task.getDateAndTime().getEndDate() + "\t"
						+ task.getDateAndTime().getStartTime() + "\t" + task.getDateAndTime().getEndTime());
				if(task.isHighPriority()) {
					System.out.print("\t" + "(!!!!)");
				} 
				if(task.isDone()) {
					System.out.print("\t" + "(DONE)");
				}
				System.out.println();
			}
		}
		System.out.println("---------------------------" +"\n");
	}
	
	private void run(){
		while(true) {
			System.out.println("Adding task to: " + storage.getCurrLabel());
			System.out.print("I want to: ");
			String userCommand = sc.nextLine();
			Command command = parser.parse(userCommand);
			String feedback = logic.executeCommand(command);
			if(command.getCommandType() != COMMANDTYPE.SEARCH) {
				clearScreen();
				this.displayTextUI();
				show(feedback);
			} 
		}
	}
	
	public static void clearScreen() {
		for(int i = 0; i < 100; i++) {
			System.out.println();
		}
	}
	
	public static void main(String[] Args) throws Exception {
		TodoThis tdt = new TodoThis(FILENAME);
		show(tdt.doInit());
		tdt.displayTextUI();
		tdt.run();
	}
	
	public static void show(String message) {
		System.out.println(message);
	}
	
	//------------------------GETTER & SETTER---------------------------------

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
