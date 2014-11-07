package todothis.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

import todothis.TDTGUI;
import todothis.command.Command;
import todothis.commons.TDTTimeMethods;
import todothis.logic.TDTReminder;
import todothis.logic.Task;
import todothis.parser.TDTDateAndTime;

public class TDTStorage implements ITDTStorage {
	private String fileName;
	private HashMap<String, ArrayList<Task>> labelMap;
	private String currLabel = TDTGUI.DEFAULT_LABEL;
	private Stack<Command> undoStack;
	private Stack<String> labelPointerStack;
	private BufferedWriter bw;
	private Stack<Command> redoStack;
	private Stack<String> redoLabelPointerStack;
	private ArrayList<String> hideList;
	private ArrayList<String> autoWords;
	
	public TDTStorage(String fileName) {
		this.setFileName(fileName);
		setLabelMap(new HashMap<String, ArrayList<Task>>());
		setUndoStack(new Stack<Command>());
		setLabelPointerStack(new Stack<String>());
		setRedoStack(new Stack<Command>());
		setRedoLabelPointerStack(new Stack<String>());
		setHideList(new ArrayList<String>());
		labelMap.put(currLabel, new ArrayList<Task>());
		setAutoWords(new ArrayList<String>());
		initializeWordsForAutoComplete();
	}

	
	
	@Override
	public void readInitialise() throws IOException {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			int totalLabel = Integer.parseInt(br.readLine());
			for(int i = 0; i < totalLabel; i ++) {
				String label = br.readLine();
				labelMap.put(label, new ArrayList<Task>());
				autoWords.add(label);
			}
			Collections.sort(autoWords);
			while(br.ready()) {
				String line = br.readLine();
				String[] params = line.split("\t");
				assert(params.length == 9);
				TDTDateAndTime date = new TDTDateAndTime(params[4], params[5], 
						params[6], params[7] );
				Task task = new Task(0, params[0], params[1] , date, false, false, params[8]);
				if(!labelMap.containsKey(params[0])) {
					labelMap.put(params[0], new ArrayList<Task>());
				}
				task.setTaskID(this.getLabelSize(params[0]) + 1);
				if(params[2].equals("true")) {
					task.setHighPriority(true);
				}
				if(params[3].equals("true")) {
					task.setDone(true);
				}
				if(!params[8].equals("null")) {
					task.setReminder(new TDTReminder(
							TDTTimeMethods.calculateRemainingTime(params[8]), task));
				}
				this.addTask(task);	
			}
			br.close();
		} catch(Exception e) {
			bw = new BufferedWriter(new FileWriter(fileName));
		}
		
	}

	@Override
	public void write(){
		try {
			bw = new BufferedWriter(new FileWriter(fileName));
			bw.write(this.getLabelMap().size()+ "");
			bw.newLine();
			Set<String> labels = this.getLabelMap().keySet();
			Iterator<String> labelIter = labels.iterator();
			while(labelIter.hasNext()) {
				bw.write(labelIter.next());
				bw.newLine();
			}
			
			Iterator<Task> iter = this.getTaskIterator();
			while(iter.hasNext()) {
				Task task = iter.next();
				bw.write(task.getLabelName() + "\t" + task.getDetails() + "\t" + 
				task.isHighPriority() + "\t" + task.isDone() + "\t" +
				task.getDateAndTime().getStartDate() + "\t" +
				task.getDateAndTime().getEndDate() + "\t" +
				task.getDateAndTime().getStartTime() + "\t" +
				task.getDateAndTime().getEndTime() + "\t" +
				task.getRemindDateTime());
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

	public Iterator<String> getLabelIterator() {
		return labelMap.keySet().iterator();
	}	
	
	public void insertToHideList(String label) {
		if(!hideList.contains(label)) {
			hideList.add(label);
		}
	}
	
	public int getLabelSize(String labelName) {
		return this.getLabelMap().get(labelName).size();
	}
	
	public void addTask(Task task) {
		this.getLabelMap().get(task.getLabelName()).add(task);
	}
	
	public void insertToAutoWords(String label) {
		autoWords.add(label);
		Collections.sort(autoWords);
	}
	
	public void insertToUndoStack(Command comd) {
		undoStack.add(comd);
	}

	private void initializeWordsForAutoComplete() {
		autoWords.add("DELETE");
		autoWords.add("DONE");
		autoWords.add("REDO");
		autoWords.add("UNDO");
		autoWords.add("HIDE");
		autoWords.add("SHOW");
		autoWords.add("EDIT");
		autoWords.add("LABEL");
		autoWords.add("SEARCH");
		autoWords.add("REMIND");
		autoWords.add("EXIT");
		autoWords.add("HELP");
		autoWords.add("TODOTHIS");
	}
	
	public ArrayList<String> copyHideList() {
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0 ; i < hideList.size(); i++) {
			list.add(hideList.get(i));
		}
		return list;
	}
	
	public HashMap<String, ArrayList<Task>> copyLabelMap() {
		HashMap<String, ArrayList<Task>> hmap = new HashMap<String, ArrayList<Task>>();
		Iterator<String> labelIter = labelMap.keySet().iterator();
		Iterator<Task> taskIter = this.getTaskIterator();
		
		while(labelIter.hasNext()) {
			String next = labelIter.next();
			hmap.put(next, new ArrayList<Task>());
		}
		while(taskIter.hasNext()) {
			Task task =  taskIter.next();
			hmap.get(task.getLabelName()).add(new Task(task.getTaskID(), task.getLabelName(),
					 task.getDetails(), task.getDateAndTime(), task.isHighPriority(), 
					 task.isDone(), "null"));
		}
		return hmap;
	}
	
	public ArrayList<Task> copyTaskList(ArrayList<Task> array) {
		ArrayList<Task> res = new ArrayList<Task>();
		Iterator<Task> taskIter = array.iterator();
		while(taskIter.hasNext()) {
			Task task =  taskIter.next();
			res.add(new Task(task.getTaskID(), task.getLabelName(),
					 task.getDetails(), task.getDateAndTime(), task.isHighPriority(), 
					 task.isDone(), "null"));
		}
		return res;
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

	public Stack<String> getLabelPointerStack() {
		return labelPointerStack;
	}


	public void setLabelPointerStack(Stack<String> labelPointerStack) {
		this.labelPointerStack = labelPointerStack;
	}


	public Stack<Command> getRedoStack() {
		return redoStack;
	}


	public void setRedoStack(Stack<Command> redoStack) {
		this.redoStack = redoStack;
	}


	public Stack<String> getRedoLabelPointerStack() {
		return redoLabelPointerStack;
	}


	public void setRedoLabelPointerStack(Stack<String> redoLabelPointerStack) {
		this.redoLabelPointerStack = redoLabelPointerStack;
	}


	public ArrayList<String> getHideList() {
		return hideList;
	}


	public void setHideList(ArrayList<String> hideList) {
		this.hideList = hideList;
	}


	public ArrayList<String> getAutoWords() {
		return autoWords;
	}


	public void setAutoWords(ArrayList<String> autoWords) {
		this.autoWords = autoWords;
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
