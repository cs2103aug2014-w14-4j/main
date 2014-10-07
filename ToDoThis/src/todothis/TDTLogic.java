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
				return doDelete(command);
			case EDIT :
				return doEdit(command);
			case LABEL :
				return doLabel(command);
			case SORT :
				return doSort(command);
			case SEARCH :
				ArrayList<Task> searched = doSearch(command);
				TodoThis.clearScreen();
				this.printSearch(searched);
				return "";
			case HIDE :
				return doHide(command);
			case UNDO :
				storage.getLabelPointerStack().pop();
				storage.getUndoStack().pop();
				feedback = doUndo(command);
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
	
	// public Task(int taskID, String labelName, String details, String dueDate,
	//			String dueTime, boolean p) {
	//public Command(COMMANDTYPE commandType, String labelName, int taskID,
	//		String commandDetails, String dueDate, String dueTime, 
	//		boolean isHighPriority) {
	/**
	 *
	 * @throws Exception 
	 */
	@Override
	public String doADD(Command command){
		String labelName = storage.getCurrLabel();
		int taskId = storage.getLabelSize(labelName) + 1;
		Task task = new Task(taskId, labelName, command.getCommandDetails(),
				command.getDateAndTime(), command.isHighPriority());
		TDTDateAndTime dnt = command.getDateAndTime();
		
		storage.addTask(task);
		sort(storage.getLabelMap());
		storage.write();
		return "Add success";
	}
	
	private void sort(HashMap<String,ArrayList<Task>> hmap) {
		Iterator<ArrayList<Task>> iter = hmap.values().iterator();
		while(iter.hasNext()) {
			ArrayList<Task> next = iter.next();
			Collections.sort(next);
			this.renumberTaskID(next);
		}
	}
	

	@Override
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
	
	private void printSearch(ArrayList<Task> tasks) {
		System.out.println(tasks.size() + " results found.");
		System.out.println("--------------------------------------------------");
		for(int i = 0; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			System.out.println(i + 1 + ") Label: " + task.getLabelName() + "\t" +
			"TaskID: " + task.getTaskID());
			System.out.println("Details: " + task.getDetails());
			System.out.println("--------------------------------------------------");
		}
	}

	@Override
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

	@Override
	public String doSort(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
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

	@Override
	public String doUndo(Command command) {
		if(command.getCommandType() != COMMANDTYPE.SEARCH) {
			if(!storage.getUndoStack().isEmpty()) {
				storage.setLabelMap(storage.getUndoStack().pop());
				storage.setCurrLabel(storage.getLabelPointerStack().pop());
				return "Undo success!";
			} else {
				return "No command to undo.";
			}
		} else {
			return "";
		}
	}

	

	@Override
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

	@Override
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
	
	
	@Override
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
	
	
	@Override
	public String doLabel(Command command) {
		String[] label = command.getLabelName().toUpperCase().split(" ");
		
		if(label.length > 1 || label.length <= 0) {
			return "Error. Invalid Label name.";
		}
		
		if(storage.getLabelMap().containsKey(label[0])) {
			storage.setCurrLabel(label[0]);
		} else if(label[0].equals("") || label[0].matches("\\d+")) {
			return "Error. Invalid label name.";
		} else {
			storage.getLabelMap().put(label[0], new ArrayList<Task>());
			storage.setCurrLabel(label[0]);
			storage.write();
			return "Label created";
		}
		
		//Shouldnt reach here
		return "Invalid Label command.";
	}

}
