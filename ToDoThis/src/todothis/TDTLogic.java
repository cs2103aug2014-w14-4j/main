package todothis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import todothis.ITDTParser.COMMANDTYPE;


public class TDTLogic implements ITDTLogic {
	private TDTStorage storage;
	
	public TDTLogic(TDTStorage storage) {
		this.storage = storage;
	}
	
	@Override
	public String executeCommand(Command command) {
		String feedback = "";
		storage.getLabelPointerStack().push(storage.getCurrLabel());
		storage.getUndoStack().push(storage.copyLabelMap());
		switch(command.getCommandType()) {
			case ADD :
				return doADD(command);
			case DELETE :
				feedback = doDelete(command);	
				storage.write();
				return feedback;
			case EDIT :
				return doEdit(command);
			case LABEL :
				return doLabel(command);
			case SEARCH :
				return "";
			case HIDE :
				return doHide(command);
			case UNDO :
				storage.getLabelPointerStack().pop();
				storage.getUndoStack().pop();
				feedback = doUndo();
				storage.write();
				return feedback;	
			case DISPLAY :
				return doDisplay(command);
			case DONE :
				feedback = doDone(command);	
				storage.write();
				return feedback;
			default:
				return "";
		}
	}
	

	public String doADD(Command command){
		String labelName = storage.getCurrLabel();
		int taskId = storage.getLabelSize(labelName) + 1;
		Task task = new Task(taskId, labelName, command.getCommandDetails(),
				command.getDateAndTime(), command.isHighPriority());
		TDTDateAndTime dnt = command.getDateAndTime();
		if(TDTDateAndTime.isValidDateRange(dnt.getStartDate()) && 
				TDTDateAndTime.isValidDateRange(dnt.getEndDate()) &&
				TDTDateAndTime.isValidTimeRange(dnt.getStartTime()) &&
				TDTDateAndTime.isValidTimeRange(dnt.getEndTime())) {
			storage.addTask(task);
			sort(storage.getLabelMap());
			storage.write();
			return "Add success";
		} else {
			return "Invalid date/time format.";
		}

	}
	
	private void sort(HashMap<String,ArrayList<Task>> hmap) {
		Iterator<ArrayList<Task>> iter = hmap.values().iterator();
		while(iter.hasNext()) {
			ArrayList<Task> next = iter.next();
			Collections.sort(next);
			this.renumberTaskID(next);
		}
	}
	

	public String doDelete(Command command) {
		String label = command.getLabelName().toUpperCase();
		int taskId = command.getTaskID();
		
		//delete
		if(label.equals("") && taskId == -1) {
			storage.setLabelMap(new HashMap<String, ArrayList<Task>>());
			storage.getLabelMap().put(TodoThis.DEFAULT_LABEL, new ArrayList<Task>());
			return storage.getFileName() + " is cleared!";
		}
		
		//delete label
		if(!label.equals("") && taskId == -1) {
			if(storage.getLabelMap().containsKey(label)) {
				storage.getLabelMap().remove(label);
				if(label.equals(TodoThis.DEFAULT_LABEL)) {
					storage.getLabelMap().put(TodoThis.DEFAULT_LABEL, new ArrayList<Task>());
				}
				return "Label deleted.";
			} else {
				return "Error. Label does not exist.";
			}
		}
		
		//delete task from current label
		if(label.equals("") && taskId != -1) {
			ArrayList<Task> array = storage.getLabelMap().get(storage.getCurrLabel());
			if(taskId <= array.size() && command.getTaskID() > 0) {
				array.remove(taskId - 1);
				renumberTaskID(array);
				return "Task deleted";
			} else {
				return "error. Invalid task number.";
			}
		}
		
		//delete task from specific label
		if(!label.equals("") && taskId != -1) {
			if(storage.getLabelMap().containsKey(label)) {
				ArrayList<Task> array = storage.getLabelMap().get(label);
				if(taskId <= array.size() && command.getTaskID() > 0) {
					array.remove(taskId - 1);
					renumberTaskID(array);
					return "Task deleted";
				} else {
					return "error. Invalid task number.";
				}
			} else {
				return "error. Label does not exist.";
			}
		}
		//Shouldnt reach here
		return "Error. Invalid delete.";
		
	}

	private void renumberTaskID(ArrayList<Task> array) {
		for(int i = 0; i < array.size(); i++) {
			Task task = array.get(i);
			task.setTaskID(i + 1);
		}
	}
	

	public ArrayList<Task> doSearch(Command command) {
		boolean found = false;
		ArrayList<Task> searchedTask = new ArrayList<Task>();
		String[] params = command.getCommandDetails().replaceAll("[\\W]", " ").split(" ");
		Iterator<Task> iter = storage.getTaskIterator();
		
		while(iter.hasNext()) {
			Task task = iter.next();
			String[] words = task.getDetails().replaceAll("[\\W]", " ").split(" ");
			for(int j = 0; j < params.length; j++) {
				for(int k = 0; k < words.length; k++) {
					if(words[k].equalsIgnoreCase(params[j])) {
						found = true;
						break;
					} else {
						found = false;
					}
				}
				if(!found) {
					break;
				}
			}
			if(found) {
				searchedTask.add(task);
			} 
			found = false;
		}
		
		return searchedTask;
	}



	public String doEdit(Command command) {
		
		String label = command.getLabelName().toUpperCase();
		int taskId = command.getTaskID();
		String commandDetails = command.getCommandDetails();
		boolean isHighPriority = command.isHighPriority();
		TDTDateAndTime dateAndTime = command.getDateAndTime();

		//edit task from current label
		if(label.equals("") && taskId != -1) {
			ArrayList<Task> array = storage.getLabelMap().get(storage.getCurrLabel());
			if(taskId <= array.size() && command.getTaskID() > 0) {
				Task task = array.get(taskId - 1);
				task.setDetails(commandDetails);
				task.setDateAndTime(dateAndTime);
				task.setHighPriority(isHighPriority);
				return "Task edited";
			} else {
				return "error. Invalid task number.";
			}
		}

		//edit task from specific label
		if(!label.equals("") && taskId != -1) {
			if(storage.getLabelMap().containsKey(label)) {
				ArrayList<Task> array = storage.getLabelMap().get(label);
				if(taskId <= array.size() && command.getTaskID() > 0) {
					Task task = array.get(taskId - 1);
					task.setDetails(commandDetails);
					task.setDateAndTime(dateAndTime);
					task.setHighPriority(isHighPriority);
					return "Task edited";
				} else {
					return "error. Invalid task number.";
				}
			} else {
				return "Error. Label does not exist.";
			}
		}
		
		return "Error. Invalid edit command.";
	}

	public String doUndo() {
		if(!storage.getUndoStack().isEmpty()) {
			storage.setLabelMap(storage.getUndoStack().pop());
			storage.setCurrLabel(storage.getLabelPointerStack().pop());
			return "Undo success!";
		} else {
			return "No command to undo.";
		}
	}

	

	public String doDisplay(Command command) {
		String labelName = command.getLabelName().toUpperCase();
		Iterator<Task> i;
		if(labelName.equals("")){
			i = storage.getTaskIterator();
			while(i.hasNext()){
				Task temp = i.next();
				temp.setHide(false);
			}
		}else if(storage.getLabelMap().containsKey(labelName)){
			i = storage.getTaskIterator();
			while(i.hasNext()){
				Task temp = i.next();
				if(temp.getLabelName().equals(labelName)){
					temp.setHide(false);
				}else{
					temp.setHide(true);
				}
			}
		}else{
			return "Display command invalid!";
		}
		return "";
	}

	public String doHide(Command command) {
		Iterator <Task> i;
		String labelName = command.getLabelName().toUpperCase();

		if(labelName.equals("")){
			i = storage.getTaskIterator();
			while(i.hasNext()){
				i.next().setHide(true);
			}
		}else{
			if(storage.getLabelMap().containsKey(labelName)){
				i = storage.getLabelMap().get(labelName).iterator();
				while(i.hasNext()){
					i.next().setHide(true);
				}
			}else{
				return "Label name cannot be found!";
			}
		}
		return "";
	}
	
	
	public String doDone(Command command) {
		String label = command.getLabelName().toUpperCase();
		int taskId = command.getTaskID();
		
		//done
		if(label.equals("") && taskId == -1) {
			Iterator<Task> iter = storage.getTaskIterator();
			while(iter.hasNext()) {
				Task next = iter.next();
				next.setDone(true);
			}
			return "All tasks are done!";
		}
		
		//done label
		if(!label.equals("") && taskId == -1) {
			if(storage.getLabelMap().containsKey(label)) {
				ArrayList<Task> array = storage.getLabelMap().get(label);
				for(int i = 0 ; i < array.size(); i ++) {
					array.get(i).setDone(true);
				}
				return "Tasks under " + label + "are done.";
			} else {
				return "Error. Label does not exist.";
			}
		}
		
		//done task from current label
		if(label.equals("") && taskId != -1) {
			ArrayList<Task> array = storage.getLabelMap().get(storage.getCurrLabel());
			if(taskId <= array.size() && command.getTaskID() > 0) {
				array.get(taskId - 1).setDone(true);
				return "Task done";
			} else {
				return "error. Invalid task number.";
			}
		}
		
		//delete task from specific label
		if(!label.equals("") && taskId != -1) {
			if(storage.getLabelMap().containsKey(label)) {
				ArrayList<Task> array = storage.getLabelMap().get(label);
				if(taskId <= array.size() && command.getTaskID() > 0) {
					array.get(taskId - 1).setDone(true);
					return "Task done";
				} else {
					return "error. Invalid task number.";
				}
			} else {
				return "Error. Label does not exist.";
			}
		}
		//Shouldnt reach here
		return "Error. Invalid done.";
	}
	
	
	public String doLabel(Command command) {
		String[] label = command.getLabelName().toUpperCase().split(" ");
		
		if(label.length > 1 || label.length <= 0) {
			return "Error. Invalid Label name.";
		}
		
		if(storage.getLabelMap().containsKey(label[0])) {
			storage.setCurrLabel(label[0]);
			return "Current label changed.";
		} else if(label[0].equals("") || label[0].matches("\\d+")) {
			return "Error. Label name cannot be blank or digits only.";
		} else {
			storage.getLabelMap().put(label[0], new ArrayList<Task>());
			storage.setCurrLabel(label[0]);
			storage.write();
			return "Label created";
		}
	}

}
