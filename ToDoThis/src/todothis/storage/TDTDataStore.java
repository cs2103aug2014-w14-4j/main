package todothis.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import todothis.commons.TDTCommons;
import todothis.commons.Task;
import todothis.logic.command.Command;

/**
 * 
 * TDTDataStore holds all the data required by TodoThis during runtime.
 *
 */
public class TDTDataStore {
	private HashMap<String, ArrayList<Task>> taskMap;
	private String currLabel = TDTCommons.DEFAULT_LABEL;
	private Stack<Command> undoStack;
	private Stack<Command> redoStack;
	private ArrayList<String> hideList;
	private ArrayList<String> autoWords;
	private String fileName;
	private TDTFileHandler file;
	
	/**
	 * Construct a new TDTDataStore. All read and write operations will be at the fileName provided
	 * @param fileName
	 */
	public TDTDataStore(String fileName) {
		setFileName(fileName);
		setTaskMap(new HashMap<String, ArrayList<Task>>());
		setUndoStack(new Stack<Command>());
		setRedoStack(new Stack<Command>());
		setHideList(new ArrayList<String>());
		taskMap.put(currLabel, new ArrayList<Task>());
		setAutoWords(new ArrayList<String>());
		initializeWordsForAutoComplete();
		this.file = new TDTFileHandler(this);
	}
	
	/**
	 * Write to the file at fileName.txt
	 */
	public void writeToFile() {
		file.write();
	}
	
	/**
	 * Read from the file fileName.txt and store the content in the TDTDataStore object .
	 * @throws IOException if unable to initialize
	 */
	public void readAndInitialize() throws IOException {
		file.readAndInitialize();
	}
	
	/**
	 * Get all the task in the taskMap and return as a iterator.
	 * @return the iterator containing all the task
	 */
	public Iterator<Task> getTaskIterator() {
		return new TaskIterator(this.getTaskMap());
	}
	
	/**
	 * Insert word to the auto complete dictionary.
	 * @param label - The word to added to autocomplete
	 */
	public void insertToAutoWords(String label) {
		autoWords.add(label);
		Collections.sort(autoWords);
	}
	
	/**
	 * Return the number or task in the label.
	 * @param labelName
	 * @return the number or task in the label
	 */
	public int getLabelSize(String labelName) {
		return this.getTaskMap().get(labelName).size();
	}
	
	
	/**
	 * Insert the command to undoStack
	 * @param comd - The command that have just been executed.
	 */
	public void insertToUndoStack(Command comd) {
		undoStack.add(comd);
	}
	
	/**
	 * Get all the label in the taskMap and return it as a iterator
	 * @return the iterator containing all the labels in taskMap.
	 */
	public Iterator<String> getLabelIterator() {
		return taskMap.keySet().iterator();
	}	
	
	/**
	 * Insert the label to HideList
	 * @param label - to be hidden from view
	 */
	public void insertToHideList(String label) {
		if(!hideList.contains(label)) {
			hideList.add(label);
		}
	}
	
	/**
	 * Return the taskList associated with the labelname.
	 * @param label
	 * @return Return the taskList associated with the labelname.
	 */
	public ArrayList<Task> getTaskListFromLabel(String label) {
		return this.getTaskMap().get(label.toUpperCase());
	}
	
	/**
	 * Add task to the taskMap
	 * @param task
	 */
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
		Collections.sort(autoWords);
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

	public TDTFileHandler getFile() {
		return file;
	}

	public void setFile(TDTFileHandler file) {
		this.file = file;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
