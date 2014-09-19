package todothis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TodoThis {
	public static ArrayList<Task> allTask;
	public static HashMap<String, Integer> labelMap;
	private static Scanner sc;
	
	//Initialise
	private static String doInit() {
		TDTStorage storage = new TDTStorage();
		try {
			storage.readInitialise();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	//Display all task under labels nicely
	public static void displayTextUI() {
		
	}
	
	public static void main(String[] Args) {
		
	}
}
