package todothis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class TDTStorage implements ITDTStorage {
	private String fileName;
	private HashMap<String, ArrayList<Task>> labelMap;
	private String currLabel = "Today";
	private Stack<Command> undoStack;
	private BufferedWriter bw;
	
	public TDTStorage(String fileName) {
		this.setFileName(fileName);
		setLabelMap(new HashMap<String, ArrayList<Task>>());
		setUndoStack(new Stack<Command>());
		labelMap.put(currLabel, new ArrayList<Task>());
	}
	
	
	@Override
	public void readInitialise() throws Exception {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			while(br.ready()) {
				String line = br.readLine();
				String[] params = line.split("\t");
				Task task = new Task(0, params[0], params[1] , params[2], params[3],
						false);
				if(!labelMap.containsKey(params[0])) {
					labelMap.put(params[0], new ArrayList<Task>());
				}
				task.setTaskID(this.getLabelSize(params[0]) + 1);
				if(params[4].equals("true")) {
					task.setHighPriority(true);
				}
				this.addTask(task);	
			}
			br.close();
		} catch(Exception e) {
			bw = new BufferedWriter(new FileWriter(fileName));
		}
		
	}

	@Override
	public void write() throws Exception {
		try {
			bw = new BufferedWriter(new FileWriter(fileName));
			Iterator<Task> iter = this.getTaskIterator();
			while(iter.hasNext()) {
				Task task = iter.next();
				bw.write(task.getLabelName() + "\t" + task.getDetails() + "\t" + 
				task.getDueDate() + "\t" + task.getDueTime() + "\t" + task.isHighPriority());
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			System.out.println("Error. Unable to initialise write file.");
		}
	}
	
	@Override
	public Iterator<Task> getTaskIterator() {
		return new TaskIterator(this.getLabelMap());
	}

	
	
	public int getLabelSize(String labelName) {
		return this.getLabelMap().get(labelName).size();
	}
	
	public void addTask(Task task) {
		this.getLabelMap().get(task.getLabelName()).add(task);
	}
	
	//public Task(int taskID, String labelName, String details, String dueDate,
	//		String dueTime, boolean p) {
	public static void main(String[] arg) throws Exception {
		/*
		TDTStorage storage = new TDTStorage("TestStorage.txt");
		storage.getLabelMap().put("Today", new ArrayList<Task>());
		storage.addTask(new Task(1, "Today", "Buy egg", "20092014", "1400", true));
		storage.addTask(new Task(2, "Today", "Buy rice", "20092014", "1500", false));
		storage.write();
		storage.addTask(new Task(3, "Today", "Buy rice", "19092014", "", false));
		storage.write();*/
		
		TDTStorage storage = new TDTStorage("tdt.txt");
		storage.readInitialise();
		storage.getLabelMap().get("Today").add(new Task(1, "Today", "Buy egg", "20092014", "1400", true));
		storage.write();
		/*
		Iterator<Task> iter = storage.getTaskIterator();
		while(iter.hasNext()) {
			Task task = iter.next();
			System.out.print(task.getTaskID() + "\t" +task.getLabelName() +
					"\t" + task.getDetails() + "\t" +task.getDueDate() + 
					"\t" +task.getDueTime() +"\t" +task.isHighPriority());
			System.out.println();
		}*/
	}
	
	//-----------------------------GETTERS & SETTERS----------------------------------------------
	public String getFileName() {
		return fileName;
	}
	private void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public HashMap<String, ArrayList<Task>> getLabelMap() {
		return labelMap;
	}

	public void setLabelMap(HashMap<String, ArrayList<Task>> labelMap) {
		this.labelMap = labelMap;
	}

	public String getCurrLabel() {
		return currLabel;
	}

	public void setCurrLabel(String currLabel) {
		this.currLabel = currLabel;
	}

	public Stack<Command> getUndoStack() {
		return undoStack;
	}

	public void setUndoStack(Stack<Command> undoStack) {
		this.undoStack = undoStack;
	}
	//-------------------------------------------------------------------------------
	private class TaskIterator implements Iterator<Task>{
		private LinkedList<Task> iterQ;
		
		TaskIterator(HashMap<String,ArrayList<Task>> hmap) {
			iterQ = new LinkedList<Task>();
			Iterator<ArrayList<Task>> tasks = hmap.values().iterator();
			while(tasks.hasNext()) {
				ArrayList<Task> arrayTask = tasks.next();
				for(int i = 0; i < arrayTask.size(); i++) {
					iterQ.add(arrayTask.get(i));
				}
			}
			 
		}
		
		@Override
		public boolean hasNext() {
			return !iterQ.isEmpty();
		}

		@Override
		public Task next() {
			return iterQ.poll();
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}
		
	}


	
}
