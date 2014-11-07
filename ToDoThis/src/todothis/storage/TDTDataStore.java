package todothis.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import todothis.TDTGUI;
import todothis.command.Command;
import todothis.commons.Task;

public class TDTDataStore {
	private HashMap<String, ArrayList<Task>> taskMap;
	private String currLabel = TDTGUI.DEFAULT_LABEL;
	private Stack<Command> undoStack;
	private Stack<Command> redoStack;
	private ArrayList<String> hideList;
	private ArrayList<String> autoWords;
	
	public TDTDataStore() {
		setTaskMap(new HashMap<String, ArrayList<Task>>());
		setUndoStack(new Stack<Command>());
		setRedoStack(new Stack<Command>());
		setHideList(new ArrayList<String>());
		taskMap.put(currLabel, new ArrayList<Task>());
		setAutoWords(new ArrayList<String>());
		initializeWordsForAutoComplete();
	}
	

	public Iterator<Task> getTaskIterator() {
		return new TaskIterator(this.getTaskMap());
	}
	
	public void insertToAutoWords(String label) {
		autoWords.add(label);
		Collections.sort(autoWords);
	}
	
	public int getLabelSize(String labelName) {
		return this.getTaskMap().get(labelName).size();
	}
	
	public void insertToUndoStack(Command comd) {
		undoStack.add(comd);
	}
	
	public Iterator<String> getLabelIterator() {
		return taskMap.keySet().iterator();
	}	
	
	public void insertToHideList(String label) {
		if(!hideList.contains(label)) {
			hideList.add(label);
		}
	}
	
	public void addTask(Task task) {
		this.getTaskMap().get(task.getLabelName()).add(task);
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
	
	
	
	
	public HashMap<String, ArrayList<Task>> getTaskMap() {
		return taskMap;
	}
	public void setTaskMap(HashMap<String, ArrayList<Task>> taskMap) {
		this.taskMap = taskMap;
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
	public Stack<Command> getRedoStack() {
		return redoStack;
	}
	public void setRedoStack(Stack<Command> redoStack) {
		this.redoStack = redoStack;
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
}
