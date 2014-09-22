package todothis;

import java.util.ArrayList;
import java.util.Iterator;

import todothis.ITDTParser.COMMANDTYPE;


public class TDTLogic implements ITDTLogic {
	private TDTStorage storage;
	
	public TDTLogic(TDTStorage storage) {
		this.storage = storage;
	}
	
	@Override
	public String executeCommand(Command command) {
		switch(command.getCommandType()) {
			case ADD :
				storage.getUndoStack().push(storage.copyLabelMap());
				return doADD(command);
			case DELETE :
				storage.getUndoStack().push(storage.copyLabelMap());
				return doDelete(command);
			case EDIT :
				return doEdit(command);
			case LABEL :
				storage.getUndoStack().push(storage.copyLabelMap());
				return doLabel(command);
			case SORT :
				storage.getUndoStack().push(storage.copyLabelMap());
				return doSort(command);
			case SEARCH :
				return doSearch(command);
			case HIDE :
				storage.getUndoStack().push(storage.copyLabelMap());
				return doHide(command);
			case UNDO :
				String feedback = doUndo(command);
				storage.write();
				return feedback;	
			case DISPLAY :
				storage.getUndoStack().push(storage.copyLabelMap());
				return doDisplay(command);
			case DONE :
				storage.getUndoStack().push(storage.copyLabelMap());
				return doDone(command);	
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
				command.getDueDate(), command.getDueTime(), command.isHighPriority());
		storage.addTask(task);
		storage.write();
		return "Add success";
	}
	

	@Override
	public String doDelete(Command command) {
		String label = command.getLabelName().toUpperCase();
		if(command.getTaskID() != -1) {
			//Deleting a task
			if(storage.getLabelMap().containsKey(label)) {
				ArrayList<Task> array = storage.getLabelMap().get(label);
				if(command.getTaskID() <= array.size() && command.getTaskID() > 0) {
					array.remove(command.getTaskID() - 1);
					renumberTaskID(array);
					return "Task deleted";
				} else {
					return "error. Invalid task number.";
				} 
			} else {
				return "error. Label does not exist";
			}
		} else {
			//Deleting a label
			if(label.equals(TodoThis.DEFAULT_LABEL)) {
				storage.getLabelMap().put(TodoThis.DEFAULT_LABEL, new ArrayList<Task>());
				storage.write();
				return "Label deleted";
			}
			if(storage.getLabelMap().containsKey(label)) {
				if(command.getLabelName().equals(storage.getCurrLabel())) {
					storage.setCurrLabel(TodoThis.DEFAULT_LABEL);
				}
				storage.getLabelMap().remove(label);
				storage.write();
				return "Label Deleted";
			} else {
				return "error. Label does not exist";
			}
		}
		
	}

	private void renumberTaskID(ArrayList<Task> array) {
		for(int i = 0; i < array.size(); i++) {
			Task task = array.get(i);
			task.setTaskID(i + 1);
		}
	}

	@Override
	public String doSearch(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doSort(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doEdit(Command command) {
		// TODO Auto-generated method stub
		
		String labelName = command.getLabelName().toUpperCase();
		int taskID = command.getTaskID();
		String commandDetails = command.getCommandDetails();
		String dueDate = command.getDueDate();
		String dueTime = command.getDueTime();
		boolean isHighPriority = command.isHighPriority();
	
		if(!storage.getLabelMap().containsKey(labelName)){
			return "Label Name cannot be found!";
		}else if(storage.getLabelMap().get(labelName).size() < taskID ||
				taskID <=0){
			return "TaskID to be edit cannot be found! OUT OF RANGE!";
			
		}else{
			storage.getLabelMap().get(labelName).get(taskID - 1).setDetails(commandDetails);
			storage.getLabelMap().get(labelName).get(taskID - 1).setDueDate(dueDate);
			storage.getLabelMap().get(labelName).get(taskID - 1).setDueTime(dueTime);
			storage.getLabelMap().get(labelName).get(taskID - 1).setHighPriority(isHighPriority);
		}
		return "";
	}

	@Override
	public String doUndo(Command command) {
		if(command.getCommandType() != COMMANDTYPE.SEARCH) {
			if(!storage.getUndoStack().isEmpty()) {
				storage.setLabelMap(storage.getUndoStack().pop());
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
		// TODO Auto-generated method stub
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
		String labelName = command.getLabelName().toUpperCase();
		int taskID = command.getTaskID();
		Iterator <Task> i;
	
		if(!storage.getLabelMap().containsKey(labelName)){
			return "Label Name cannot be found!";
		}else if(taskID == -1 ){
			i = storage.getLabelMap().get(labelName).iterator();
			while(i.hasNext()){
				Task temp = i.next();
				temp.setDone(true);
			}
		}else if(storage.getLabelMap().get(labelName).size() < taskID ||
				taskID <=0){
			return "TaskID to be marked done cannot be found! OUT OF RANGE!";
		}else{
			storage.getLabelMap().get(labelName).get(taskID - 1).setDone(true);
		}
		return "";
	}
	
	
	@Override
	public String doLabel(Command command) {
		String label = command.getLabelName().toUpperCase();
		if(storage.getLabelMap().containsKey(label)) {
			storage.setCurrLabel(label);
		} else {
			storage.getLabelMap().put(label, new ArrayList<Task>());
			storage.setCurrLabel(label);
		}
		return "";
	}

}
