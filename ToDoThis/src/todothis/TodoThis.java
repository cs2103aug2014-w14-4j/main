package todothis;

import java.util.Scanner;

public class TodoThis {
	public static final String FILENAME = "todothis.txt";
	public static final String DEFAULT_LABEL = "Today";
	
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
		return "Todo-This ready!";
	}
	
	//Display all task under labels nicely
	private void displayTextUI() {
	}
	
	private void run(){
		while(true) {
			System.out.print("Adding task to: " + storage.getCurrLabel());
			System.out.print("I want to: ");
			String userCommand = sc.nextLine();
			Command command = parser.parse(userCommand);
			String feedback = logic.executeCommand(command);
			this.displayTextUI();
			show(feedback);
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
