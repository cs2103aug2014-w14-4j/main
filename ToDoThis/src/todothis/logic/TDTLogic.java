package todothis.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import todothis.command.Command;
import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;



public class TDTLogic implements ITDTLogic {
	private TDTStorage storage;
	
	public TDTLogic(String fileName) {
		TDTStorage storage = new TDTStorage(fileName);
		this.storage = storage;
	}
	
	@Override
	public String executeCommand(Command command) {
		if(command.getCommandType() != COMMANDTYPE.UNDO &&
				command.getCommandType() != COMMANDTYPE.REDO) {
			storage.getRedoStack().clear();
		}

		String feedback = command.execute(storage);
		storage.write();
		return feedback;
		
	}
	
	public static int renumberTaskID(ArrayList<Task> array, Task t) {
		int newNum = 0;
		for(int i = 0; i < array.size(); i++) {
			Task task = array.get(i);
			task.setTaskID(i + 1);
			if(task == t) {
				newNum = i + 1;
			}
		}
		return newNum;
	}
	
	public static int sort(ArrayList<Task> array, Task task) {
		Collections.sort(array);
		return renumberTaskID(array, task);
	}
	
	public boolean isInLabelMap(String label) {
		return storage.getLabelMap().containsKey(label.toUpperCase());
	}
	
	public boolean isHideLabel(String label) {
		return storage.getHideList().contains(label);
	}

	@Override
	public Iterator<Task> getTaskIterator() {
		return storage.getTaskIterator();
	}

	@Override
	public Iterator<String> getLabelIterator() {
		return storage.getLabelMap().keySet().iterator();
	}
	
	public ArrayList<Task> getTaskListFromLabel(String label) {
		return storage.getLabelMap().get(label.toUpperCase());
	}
	
	public TDTStorage getStorage() {
		return storage;
	}
	
	public String getCurrLabel() {
		return storage.getCurrLabel();
	}
	
	public void readAndInitialize() throws IOException {
		storage.readInitialise();
	}
	
	public void write() {
		storage.write();
	}
	
	public ArrayList<String> getAutoWords() {
		return storage.getAutoWords();
	}


}
