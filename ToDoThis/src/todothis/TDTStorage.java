package todothis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

public class TDTStorage implements ITDTStorage {
	private String fileName;
	private HashMap<String, ArrayList<Task>> labelMap;
	private String currLabel = "Today";
	private Stack<Command> undoStack;
	
	public TDTStorage(String fileName) {
		this.setFileName(fileName);
		setLabelMap(new HashMap<String, ArrayList<Task>>());
		setUndoStack(new Stack<Command>());
	}
	
	@Override
	public void readInitialise() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Iterator<Task> getTaskIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	public int getLabelSize(String labelName) {
		return this.getLabelMap().get(labelName).size();
	}
	
	public void addTask(Task task) {
		this.getLabelMap().get(task.getLabelName()).add(task);
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


	
}
