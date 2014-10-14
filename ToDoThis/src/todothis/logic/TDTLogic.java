package todothis.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import todothis.command.Command;
import todothis.storage.TDTStorage;



public class TDTLogic implements ITDTLogic {
	private TDTStorage storage;
	
	public TDTLogic(String fileName) {
		TDTStorage storage = new TDTStorage(fileName);
		this.storage = storage;
	}
	
	@Override
	public String executeCommand(Command command) {
		storage.getLabelPointerStack().push(storage.getCurrLabel());
		storage.getUndoStack().push(storage.copyLabelMap());
		String feedback = command.execute(storage);
		storage.write();
		return feedback;
		
	}
	
	public static void renumberTaskID(ArrayList<Task> array) {
		for(int i = 0; i < array.size(); i++) {
			Task task = array.get(i);
			task.setTaskID(i + 1);
		}
	}
	
	public static void sort(HashMap<String,ArrayList<Task>> hmap) {
		Iterator<ArrayList<Task>> iter = hmap.values().iterator();
		while(iter.hasNext()) {
			ArrayList<Task> next = iter.next();
			Collections.sort(next);
			renumberTaskID(next);
		}
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
		return storage.getLabelMap().get(label);
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

}
